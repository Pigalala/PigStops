package me.pigalala.pitminigame.pit;

import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.enums.PitGame;
import me.pigalala.pitminigame.enums.PitType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.HashMap;

public class PitManager {

    public static HashMap<Player, Integer> itemsToClick = new HashMap<>();
    public static HashMap<Player, Boolean> hasStarted = new HashMap<>();
    public static HashMap<Player, PitType> pitTypes = new HashMap<>();
    public static HashMap<Player, Inventory> pitWindows = new HashMap<>();
    public static HashMap<Player, Instant> playerTime = new HashMap<>();

    /** Base name for a pigstop **/
    public static final String pitNameBase = "§dPigStop - ";

    /** Opens a pit game based on the default game, or using the 3rd parameter **/
    public static void openPitGame(Player player, PitType pitType, PitGame... pitGame) {
        PitGame toCompare = pitGame.length == 1 ? pitGame [0] : PigStops.getPlugin().getDefaultPitGame();

        if(toCompare == PitGame.STANDARD) new PitSTANDARD(player, pitType);
        if(toCompare == PitGame.COOKIE) new PitCOOKIE(player, pitType);
        if(toCompare == PitGame.MARIANA) new PitMARIANA(player, pitType);
    }

    public static void onItemClick(Player player, PitGame game, ItemStack clickedItem, Integer... slot) {
        if(game == PitGame.STANDARD) PitSTANDARD.onItemClick(player, clickedItem);
        if(game == PitGame.COOKIE) PitCOOKIE.onItemClick(player, clickedItem);
        if(game == PitGame.MARIANA) PitMARIANA.onItemClick(player, clickedItem, slot[0]);
    }
}
