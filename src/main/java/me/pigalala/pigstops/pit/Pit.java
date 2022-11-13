package me.pigalala.pigstops.pit;

import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;

public class Pit implements Listener {

    private final PitPlayer pp;
    private final ItemStack defaultBackground;

    private int itemsToClick;
    private Instant startTime;
    private PitType pitType;
    private Inventory pitWindow;

    public Pit(PitPlayer pp, PitType pitType) {
        this.pp = pp;
        this.pitType = pitType;
        PitGame pitGame = PigStops.defaultPitGame;
        defaultBackground = pitGame.backgroundItem;

        setItemMetas();
        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());

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
        createWindow(contentsLines.toArray(new ItemStack[0]), pitGame.name, pitGame.inventorySize);
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

        Inventory pitWindow = Bukkit.createInventory(pp.player.getPlayer(), windowSize, Component.text(Utils.pitNameBase + windowName));

        this.pitWindow = pitWindow;
        this.startTime = Instant.now();

        pitWindow.setContents(contents);
        shuffleItems(false);
        pp.player.openInventory(pitWindow);
    }

    public void finishPit() {
        pp.pit = null;
        pp.player.closeInventory();

        String finalTime = formatAsTime(Duration.between(startTime, Instant.now()).toMillis());

        if(pitType != PitType.REAL) {
            pp.player.sendMessage(Utils.getCustomMessage("&aYou finished in &d%TIME%&a.",
                    "%TIME%", finalTime));
            return;
        }

        TPlayer p = TimingSystemAPI.getTPlayer(pp.player.getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;
        Driver d = driver.get();
        Heat heat = driver.get().getHeat();

        if (!d.getCurrentLap().isPitted()) {
            d.setPits(d.getPits() + 1);
            d.getCurrentLap().setPitted(true);
            heat.updatePositions();

            Utils.broadcastMessage(Utils.getCustomMessage("&d%PLAYER% &ahas completed pigstop &d%PITS% &ain &d%TIME%&a.",
                    "%PLAYER%", d.getTPlayer().getName(),
                    "%PITS%", String.valueOf(d.getPits()),
                    "%TIME%", finalTime),
                    heat);
        }

        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onItemClicked(InventoryClickEvent e) {
        if(e.getCurrentItem() == null || e.getWhoClicked() != pp.player || !e.getView().getTitle().startsWith("§dPigStops")) return;
        e.setCancelled(true);

        if(e.getCurrentItem().getType() != defaultBackground.getType()) {
            itemsToClick -= 1;
            pitWindow.setItem(e.getSlot(), new ItemStack(Material.AIR));

            if(!isFinished()) {
                pp.playSound(Sound.BLOCK_BAMBOO_HIT);
            } else {
                pp.playSound(Sound.BLOCK_SMITHING_TABLE_USE);
                finishPit();
            }
        } else {
            shuffleItems(true);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        if(e.getPlayer() != pp.player || e.getInventory() != pitWindow) return;
        pp.pit = null;
        HandlerList.unregisterAll(this);
    }

    public void shuffleItems(Boolean playFailSound){
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pitWindow.getContents()).toList());
        do {
            Collections.shuffle(shuffled);
        } while(shuffled.equals(Arrays.stream(pitWindow.getContents()).toList()));

        pitWindow.setContents(shuffled.toArray(new ItemStack[0]));
        if(playFailSound) pp.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }
}
