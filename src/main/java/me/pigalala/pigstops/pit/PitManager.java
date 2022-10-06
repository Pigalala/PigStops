package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PitManager {

    public static HashMap<Player, Integer> itemsToClick = new HashMap<>();
    public static HashMap<Player, Boolean> hasStarted = new HashMap<>();
    public static HashMap<Player, PitType> pitTypes = new HashMap<>();
    public static HashMap<Player, Inventory> pitWindows = new HashMap<>();
    public static HashMap<Player, Instant> playerTime = new HashMap<>();

    /** Base name for a pigstop **/
    public static final String pitNameBase = "§dPigStop - ";

    private static PitGame defaultPitGame;

    public static void updatePitGame(PitGame game){
        defaultPitGame = game;
    }

    /** Opens a pit game based on the default game, or using the 3rd parameter **/
    public static void openPitGame(Player player, PitType pitType) {
        if(defaultPitGame == PitGame.STANDARD) new PitSTANDARD(player, pitType);
        if(defaultPitGame == PitGame.COOKIE) new PitCOOKIE(player, pitType);
        if(defaultPitGame == PitGame.MARIANA) new PitMARIANA(player, pitType);
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer... slot) {
        if(defaultPitGame == PitGame.STANDARD) PitSTANDARD.onItemClick(player, clickedItem);
        if(defaultPitGame == PitGame.COOKIE) PitCOOKIE.onItemClick(player, clickedItem);
        if(defaultPitGame == PitGame.MARIANA) PitMARIANA.onItemClick(player, clickedItem, slot[0]);
    }

    public static List<PitGame> cancelGames() {
        List<PitGame> games = new ArrayList<>();
        games.add(PitGame.MARIANA);

        return games;
    }
}
