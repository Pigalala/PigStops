package me.pigalala.pigstops.pit.management.pitmodes;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pigstops.OinkMessages;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.Utils;
import me.pigalala.pigstops.pit.management.PitGame;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;
import static me.makkuusen.timing.system.ApiUtilities.spawnBoat;

public abstract class Pit implements Listener {

    public enum Type {
        REAL,
        FAKE;
    }

    public enum PitMode {
        DEFAULT("Default"),
        ITEM_PER_PAGE("Items Per Page");

        private final String displayName;

        PitMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static PitMode of(String name) {
            try {
                return PitMode.valueOf(name);
            } catch (Exception e) {
                PigStops.getPlugin().getLogger().log(Level.WARNING, "Unknown PitMode detected, please shout at Pigalala.");
                return null;
            }
        }

        public static ContextResolver<PitMode, BukkitCommandExecutionContext> getPitModeCommandContext() {
            return c -> {
                try {
                    return of(c.popFirstArg().toUpperCase());
                } catch (Exception e) {
                    throw new InvalidCommandArgument();
                }
            };
        }
    }

    protected final PitPlayer pp;
    protected final ItemStack defaultBackground;
    protected final PitGame pitGame;
    protected final Type pitType;

    protected int itemsToClick;
    protected int maxItemsToClick;
    protected int clicks;

    protected Instant startTime;
    protected Inventory pitWindow;

    protected final List<PitPlayer> spectators = new ArrayList<>();

    public Pit(PitPlayer pp, Type pitType) {
        this.pp = pp;
        this.pitType = pitType;
        this.pitGame = PigStops.defaultPitGame;
        this.defaultBackground = pitGame.backgroundItem;

        setup();
    }

    public Pit(PitPlayer pp, Type pitType, PitGame pg) {
        this.pp = pp;
        this.pitType = pitType;
        this.pitGame = pg;
        this.defaultBackground = pg.backgroundItem;

        setup();
    }

    private void setup() {
        setItemMetas();
        registerSpectators();

        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());

        createWindow(registerContents(), pitGame.name, pitGame.inventorySize);
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


    public void finishPit() {
        end();
        Instant endTime = Instant.now();
        String finalTime = formatAsTime(Duration.between(startTime, endTime).toMillis());

        int accuracy = Math.round((float) maxItemsToClick / clicks * 100f);
        int misclicks = clicks - maxItemsToClick;

        if(pitType != Type.REAL) {
            pp.getPlayer().sendMessage(OinkMessages.getSoloFinishText(finalTime, accuracy, misclicks));
            return;
        }

        TPlayer tp = TimingSystemAPI.getTPlayer(pp.getPlayer().getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(tp.getUniqueId());
        if(driver.isEmpty()) return;
        Driver d = driver.get();
        Heat heat = driver.get().getHeat();

        if (!d.getCurrentLap().isPitted()) {
            d.setPits(d.getPits() + 1);
            d.getCurrentLap().setPitted(true);
            heat.updatePositions();

            Utils.broadcastMessage(OinkMessages.getRaceFinishText(tp, pitGame.name, d.getPits(), finalTime, accuracy, misclicks, pitGame.pitMode), heat, String.format("%s in %s (%s)", tp.getName(), finalTime, d.getPits()));
        }
    }

    @EventHandler
    public void itemClickedEvent(InventoryClickEvent e) {
        if(e.getWhoClicked() != pp.getPlayer() || !e.getView().getTitle().startsWith("§dPigStops")) return;
        e.setCancelled(true);
        if(e.getClickedInventory() instanceof PlayerInventory) return;

        // ANTICHEAT
        if(e.getAction() == InventoryAction.CLONE_STACK) return; // MMB PICKING
        if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) return; // SHIFT CLICKING
        if(e.getAction() == InventoryAction.HOTBAR_SWAP) return; // PRESSING HOTBAR KEYBINDS

        clicks++;

        if(e.getCurrentItem() == null) return;
        onItemClicked(e.getClickedInventory(), e.getView(), e.getCurrentItem(), e.getSlot());
    }


    @EventHandler
    public void invCloseEvent(InventoryCloseEvent e) {
        if(e.getInventory() != pitWindow || isFinished() || e.getReason() == InventoryCloseEvent.Reason.PLUGIN) return;

        if(e.getPlayer() == pp.getPlayer()) end();
        else removeSpectator(PitPlayer.of((Player) e.getPlayer()));
    }

    protected void shuffleItems(Boolean playFailSound) {
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pitWindow.getContents()).toList());
        do {
            Collections.shuffle(shuffled);
        } while(shuffled.equals(Arrays.stream(pitWindow.getContents()).toList()));

        pitWindow.setContents(shuffled.toArray(new ItemStack[0]));

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

        if(pp.isInPracticeMode()) {
            if(pp.getPlayer().getVehicle() instanceof Boat b) b.remove();
            pp.practiceModeStart.setPitch(pp.getPlayer().getLocation().getPitch());
            TPlayer tPlayer = TimingSystemAPI.getTPlayer(pp.getPlayer().getUniqueId());
            Boat boat = spawnBoat(pp.practiceModeStart, tPlayer.getBoat(), tPlayer.isChestBoat());
            boat.addPassenger(pp.getPlayer());
        }
    }

    public void removeSpectator(PitPlayer pp) {
        spectators.remove(pp);
    }

    protected abstract void createWindow(ItemStack[] contents, String windowName, Integer windowSize);
    protected abstract ItemStack[] registerContents();
    protected abstract void onItemClicked(Inventory inventory, InventoryView inventoryView, ItemStack clickedItem, int clickedSlot);

    public static Pit newPitOfMode(PitGame pg, PitPlayer pp, Type pitType) {
        switch(pg.pitMode) {
            case ITEM_PER_PAGE -> {
                return new PitModeItemPerPage(pp, pitType);
            }
            default -> {
                return new PitModeDefault(pp, pitType);
            }
        }
    }
}
