package me.pigalala.pitminigame.pit;

import me.makkuusen.timing.system.Database;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import me.pigalala.pitminigame.PigStops;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;

public class PitWindow {

    private static final HashMap<Player, Integer> itemsToClick = new HashMap<>();
    private static final HashMap<Player, Boolean> hasStarted = new HashMap<>();
    private static final HashMap<Player, PitType> pitTypes = new HashMap<>();
    private static final HashMap<Player, Inventory> pitWindows = new HashMap<>();
    private static final HashMap<Player, Instant> playerTime = new HashMap<>();

    /** Base name for a pigstop **/
    public static final String pitNameBase = "§dPigStop - ";

    private static void setHashMaps(Player player, PitType pitType){
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.STANDARD) itemsToClick.put(player, 2);
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE) itemsToClick.put(player, 10);
        hasStarted.put(player, true);
        pitTypes.put(player, pitType);
        pitWindows.put(player, null);
        playerTime.put(player, Instant.now());
    }

    public static Boolean isFinished(Player player){
        return itemsToClick.get(player) <= 0;
    }

    public static void reset(Player player){
        hasStarted.put(player, false);
        pitWindows.put(player, null);
    }

    /** Creates a pigstop inventory and displays it to the player. Also includes all setup needed **/
    public static void createWindow(Player player, PitType pitType, ItemStack[] contents, String windowName, Integer windowSize){
        if(hasStarted.get(player) != null) {
            if(hasStarted.get(player)) return;
        }

        setHashMaps(player, pitType);
        Inventory pitWindow = Bukkit.createInventory(player, windowSize, pitNameBase + windowName);

        pitWindow.setContents(contents);
        player.openInventory(pitWindow);
        pitWindows.put(player, pitWindow);
    }

    /** Finishes a player's pigstop. Includes closing inventory, displaying finish time, passing pits and resetting player for next time **/
    public static void finishPits(Player player){
        player.closeInventory();
        hasStarted.put(player, false);
        pitWindows.put(player, null);

        long finalTime = Duration.between(playerTime.get(player), Instant.now()).toMillis();

        if(pitTypes.get(player) != PitType.REAL) {
            player.sendMessage("§aYou finished a pigstop in §6" + formatAsTime(finalTime));
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
                participant.getTPlayer().getPlayer().sendMessage("§6" + player.getName() + " §afinished their pigstop §c" + d.getPits() + "§a in §6" + formatAsTime(finalTime) + "§a.");
            });
        }
    }

    public static void shuffleItems(Player player){
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pitWindows.get(player).getContents()).toList());
        Collections.shuffle(shuffled);

        pitWindows.get(player).setContents(shuffled.toArray(new ItemStack[0]));
        player.openInventory(pitWindows.get(player));
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }

    public static HashMap<Player, Integer> getItemsToClick(){
        return itemsToClick;
    }
    public static HashMap<Player, Boolean> getHasStarted(){
        return hasStarted;
    }
    public static HashMap<Player, PitType> getPitTypes(){
        return pitTypes;
    }
    public static HashMap<Player, Inventory> getPitWindows(){
        return pitWindows;
    }
    public static HashMap<Player, Instant> getPlayerTime(){
        return playerTime;
    }
}