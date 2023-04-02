package me.pigalala.pigstops.pit;

import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.Utils;
import me.pigalala.pigstops.pit.management.Modifications;
import me.pigalala.pigstops.pit.management.PitGame;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;

public class Pit implements Listener {

    public enum Type {
        REAL,
        FAKE;
    }

    private final PitPlayer pp;
    private final ItemStack defaultBackground;
    private final PitGame pitGame;
    private final Type pitType;

    private int itemsToClick;
    private int maxItemToClick;
    private int clicks;
    private int accuracy;

    private Instant startTime;
    private Inventory pitWindow;

    private final List<PitPlayer> spectators = new ArrayList<>();

    public Pit(PitPlayer pp, Type pitType) {
        this.pp = pp;
        this.pitType = pitType;
        this.pitGame = PigStops.defaultPitGame;
        this.defaultBackground = pitGame.backgroundItem;

        setItemMetas();
        registerSpectators();

        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());

        createWindow(registerContents(), pitGame.name, pitGame.inventorySize);
    }

    private ItemStack[] registerContents() {
        List<ItemStack> contentsLines = new ArrayList<>();
        int itc = 0;
        for(ItemStack item : pitGame.contents) {
            if(contentsLines.size() >= pitGame.inventorySize) break;
            if(item.getType().equals(Material.AIR)) {
                contentsLines.add(defaultBackground);
            } else {
                contentsLines.add(item);
                itc++;
            }
        }
        this.itemsToClick = itc;
        this.maxItemToClick = itemsToClick;

        if(itc == 0) {
            // No items setup
            return new ItemStack[]{defaultBackground};
        }

        return contentsLines.toArray(new ItemStack[0]);
    }

    private void registerSpectators() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            PitPlayer pp = PitPlayer.of(p);

            if(p.getSpectatorTarget() != this.pp.getPlayer()) continue;
            // pp is a spectator
            spectators.add(pp);
        }
        spectators.forEach(p -> p.pit = this);
    }

    private void setItemMetas() {
        ItemMeta backgroundMeta = defaultBackground.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        defaultBackground.setItemMeta(backgroundMeta);
    }

    public Boolean isFinished(){
        return itemsToClick <= 0;
    }

    private void createWindow(ItemStack[] contents, String windowName, Integer windowSize){
        Inventory pitWindow = Bukkit.createInventory(null, windowSize, Component.text(Utils.pitNameBase + windowName));

        this.pitWindow = pitWindow;
        this.startTime = Instant.now();

        pitWindow.setContents(contents);
        if(pitGame.hasModification(Modifications.RANDOMISE_ON_START)) shuffleItems(false);

        pp.getPlayer().openInventory(pitWindow);
        spectators.forEach(pitPlayer -> pitPlayer.getPlayer().openInventory(pitWindow));
    }

    public void finishPit() {
        end();

        String finalTime = formatAsTime(Duration.between(startTime, Instant.now()).toMillis());

        accuracy = Math.round((float) maxItemToClick / (float) clicks * 100f);


        if(pitType != Type.REAL) {
            pp.getPlayer().sendMessage(Utils.getCustomMessage("&7PigStops » &a%TIME%&7 |&a%ACC% &7ACC|",
                    "%TIME%", finalTime,
                    "%ACC%", accuracy + "%"));
            return;
        }

        TPlayer p = TimingSystemAPI.getTPlayer(pp.getPlayer().getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;
        Driver d = driver.get();
        Heat heat = driver.get().getHeat();

        if (!d.getCurrentLap().isPitted()) {
            d.setPits(d.getPits() + 1);
            d.getCurrentLap().setPitted(true);
            heat.updatePositions();

            Utils.broadcastMessage(Utils.getCustomMessage("&7PigStop %PIT% &7» %PLAYER%&7 &7in %TIME%&7 |%ACC% &7ACC|",
                    "%PLAYER%", p.getColorCode() + p.getName(),
                    "%PIT%", p.getColorCode() + d.getPits(),
                    "%ACC%", p.getColorCode() + accuracy + "%",
                    "%TIME%", p.getColorCode() + finalTime),
                    heat);
        }
    }

    @EventHandler
    public void onItemClicked(InventoryClickEvent e) {
        if(e.getWhoClicked() != pp.getPlayer() || !e.getView().getTitle().startsWith("§dPigStops") || e.getClickedInventory() instanceof PlayerInventory) return;
        e.setCancelled(true);
        clicks++;

        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getType() != defaultBackground.getType()) {
            itemsToClick -= 1;
            pitWindow.setItem(e.getSlot(), new ItemStack(Material.AIR));

            if(!isFinished()) {
                pp.playSound(Sound.BLOCK_BAMBOO_HIT);
                spectators.forEach(ppp -> ppp.playSound(Sound.BLOCK_BAMBOO_HIT));
            } else {
                pp.playSound(Sound.BLOCK_SMITHING_TABLE_USE);
                spectators.forEach(ppp -> ppp.playSound(Sound.BLOCK_SMITHING_TABLE_USE));
                finishPit();
            }
        } else {
            shuffleItems(true);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if(e.getInventory() != pitWindow || isFinished() || e.getReason() == InventoryCloseEvent.Reason.PLUGIN) return;

        if(e.getPlayer() == pp.getPlayer()) end();
        else removeSpectator(PitPlayer.of((Player) e.getPlayer()));
    }

    private void shuffleItems(Boolean playFailSound) {
        if(pitGame.hasModification(Modifications.RANDOMISE_ON_FAIL)) {
            List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pitWindow.getContents()).toList());
            do {
                Collections.shuffle(shuffled);
            } while(shuffled.equals(Arrays.stream(pitWindow.getContents()).toList()));

            pitWindow.setContents(shuffled.toArray(new ItemStack[0]));
        }

        if(playFailSound) {
            pp.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
            spectators.forEach(p -> p.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f));
        }
    }

    private void end() {
        pp.pit = null;
        pp.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        spectators.forEach(p -> {
            p.pit = null;
            p.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        });
        HandlerList.unregisterAll(this);
    }

    public void removeSpectator(PitPlayer pp) {
        spectators.remove(pp);
    }
}
