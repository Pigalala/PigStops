package me.pigalala.pitminigame.pit;

import me.makkuusen.timing.system.Database;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import me.pigalala.pitminigame.PigStops;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PitWindow {

    public final HashMap<Player, Integer> itemsToClick = new HashMap<>();
    public final HashMap<Player, Boolean> hasStarted = new HashMap<>();
    public final HashMap<Player, PitType> pitTypes = new HashMap<>();
    public final HashMap<Player, Inventory> pitWindows = new HashMap<>();

    public final String pitNameBase = "§dPigStop - ";

    private void setHashMaps(Player player, PitType pitType){
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.NORMAL) itemsToClick.put(player, 2);
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE) itemsToClick.put(player, 3);
        hasStarted.put(player, true);
        pitTypes.put(player, pitType);
        pitWindows.put(player, null);
    }

    public Boolean isFinished(Player player){
        return itemsToClick.get(player) <= 0;
    }

    public void reset(Player player){
        hasStarted.put(player, false);
        pitWindows.put(player, null);
    }

    public void onItemClick(Player player, ItemStack clickedItem) throws ClassNotFoundException {
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.NORMAL) PitNORMAL.onItemClick(player, clickedItem);
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE) PitCOOKIE.onItemClick(player, clickedItem);
    }

    public void createWindow(Player player, PitType pitType, ItemStack[] contents, String windowName, Integer windowSize){
        if(hasStarted.get(player) != null) {
            if(hasStarted.get(player)) return;
        }

        setHashMaps(player, pitType);
        Inventory pitWindow = Bukkit.createInventory(player, windowSize, pitNameBase + windowName);

        pitWindow.setContents(contents);
        player.openInventory(pitWindow);
        pitWindows.put(player, pitWindow);
    }

    public void finishPits(Player player){
        player.closeInventory();
        hasStarted.put(player, false);
        pitWindows.put(player, null);

        if(pitTypes.get(player) != PitType.REAL) return;

        TPlayer p = Database.getPlayer(player.getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;
        Heat heat = driver.get().getHeat();

        if (driver.get().passPit()) {
            heat.updatePositions();
        }
    }

    public void shufflePlayer(Player player){
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pitWindows.get(player).getContents()).toList());
        Collections.shuffle(shuffled);

        pitWindows.get(player).setContents(shuffled.toArray(new ItemStack[0]));
        player.openInventory(pitWindows.get(player));
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }
}