package me.pigalala.pigstops.pit;

import me.makkuusen.timing.system.Database;
import me.makkuusen.timing.system.LanguageManager;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.TimingSystem;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pigstops.ConfigManager;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;

import static me.pigalala.pigstops.PigStops.getPlugin;
import static me.pigalala.pigstops.pit.PitManager.pitNameBase;
import static me.pigalala.pigstops.pit.PitManager.pitWindows;
import static me.pigalala.pigstops.pit.PitManager.itemsToClick;
import static me.pigalala.pigstops.pit.PitManager.pitTypes;
import static me.pigalala.pigstops.pit.PitManager.hasStarted;
import static me.pigalala.pigstops.pit.PitManager.playerTime;

public interface Pit {

    private static void setHashMaps(Player player, PitType pitType, Integer toClick){
        if(getPlugin().getDefaultPitGame() == PitGame.STANDARD) itemsToClick.put(player, toClick);
        if(getPlugin().getDefaultPitGame() == PitGame.COOKIE) itemsToClick.put(player, toClick);
        hasStarted.put(player, true);
        pitTypes.put(player, pitType);
        pitWindows.put(player, null);
        playerTime.put(player, Instant.now());
    }

    static Boolean isFinished(Player player){
        return itemsToClick.get(player) <= 0;
    }

    static void reset(Player player){
        hasStarted.put(player, false);
        pitWindows.put(player, null);
    }

    /** Creates a pigstop inventory and displays it to the player. Also includes all setup needed **/
    static void createWindow(Player player, PitType pitType, ItemStack[] contents, String windowName, Integer windowSize, Integer toClick){
        if(hasStarted.get(player) != null) {
            if(hasStarted.get(player)) return;
        }

        setHashMaps(player, pitType, toClick);
        Inventory pitWindow = Bukkit.createInventory(player, windowSize, pitNameBase + windowName);

        pitWindow.setContents(contents);
        player.openInventory(pitWindow);
        pitWindows.put(player, pitWindow);
    }

    /** Finishes a player's pigstop. Includes closing inventory, displaying finish time, passing pits and resetting player for next time **/
    static void finishPits(Player player){
        player.closeInventory();
        hasStarted.put(player, false);
        pitWindows.put(player, null);

        String finalTime = formatAsTime(Duration.between(playerTime.get(player), Instant.now()).toMillis());

        if(pitTypes.get(player) != PitType.REAL) {
            player.sendMessage("§aYou finished in §d" + finalTime + "§a.");
            return;
        }

        TPlayer p = Database.getPlayer(player.getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;
        Driver d = driver.get();
        Heat heat = driver.get().getHeat();

        if (!d.getCurrentLap().isPitted()) {
            d.setPits(d.getPits() + 1);
            d.getCurrentLap().setPitted(true);
            heat.updatePositions();

            heat.getParticipants().forEach(participant -> {
                if(participant.getTPlayer().getPlayer() == null) return;
                participant.getTPlayer().getPlayer().sendMessage("§d" + d.getTPlayer().getName() + "§a has completed pigstop §d" + d.getPits() + "§a in §d" + finalTime + "§a.");
            });
        }
    }

    static void shuffleItems(Player player){
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pitWindows.get(player).getContents()).toList());
        Collections.shuffle(shuffled);

        pitWindows.get(player).setContents(shuffled.toArray(new ItemStack[0]));
        player.openInventory(pitWindows.get(player));
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }

    static HashMap<Player, Integer> getItemsToClick(){
        return itemsToClick;
    }
    static HashMap<Player, Inventory> getPitWindows(){
        return pitWindows;
    }
}
