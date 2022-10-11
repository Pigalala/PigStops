package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PitManager {
    private static final HashMap<Player, PitPlayer> pitPlayers = new HashMap<>();

    /** Base name for a pigstop **/
    public static final String pitNameBase = "§dPigStop - ";

    private static PitGame defaultPitGame;

    public static void updatePitGame(PitGame game){
        defaultPitGame = game;
    }

    /** Opens a pit game based on the default game, or using the 3rd parameter **/
    public static void openPitGame(Player player, PitType pitType) {
        switch (defaultPitGame) {
            case STANDARD -> new PitSTANDARD(player, pitType);
            case COOKIE -> new PitCOOKIE(player, pitType);
            case MARIANA -> new PitMARIANA(player, pitType);
            case ONFISHE -> new PitONFISHE(player, pitType);
        }
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer s) {
        switch (defaultPitGame) {
            case STANDARD -> PitSTANDARD.onItemClick(player, clickedItem, s);
            case COOKIE -> PitCOOKIE.onItemClick(player, clickedItem, s);
            case MARIANA -> PitMARIANA.onItemClick(player, clickedItem, s);
            case ONFISHE -> PitONFISHE.onItemClick(player, clickedItem, s);
        }
    }

    public static void addPitPlayer(Player player, PitPlayer pitPlayer) {
        pitPlayers.put(player, pitPlayer);
    }

    public static PitPlayer getPitPlayer(Player player) {
        if(hasPitPlayer(player)) return null;

        return pitPlayers.get(player);
    }

    /** True = PP Not Found, False = PP Found **/
    public static Boolean hasPitPlayer(Player player) {
        if(!pitPlayers.containsKey(player)){
            player.kick(Component.text("You have been kicked xd.\nPlease contact Pigalala#3520"));
            return true;
        }
        return false;
    }

    public static void removePitPlayer(PitPlayer pitPlayer) {
        pitPlayers.remove(pitPlayer.getPlayer());
    }
}
