package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitGame;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;

public class PitManager {
    private static final HashMap<Player, PitPlayer> pitPlayers = new HashMap<>();

    /** Base name for a pigstop **/
    public static final String pitNameBase = "§dPigStop - ";

    private static PitGame defaultPitGame;

    public static void updatePitGame(PitGame game){
        defaultPitGame = game;
    }

    public static ItemStack[] chooseContents(PitGame pitGame) {
        switch (pitGame) {
            case STANDARD -> {
                return PitVariants.STANDARD.getContents();
            }
            case COOKIE -> {
                return PitVariants.COOKIE.getContents();
            }
            case MARIANA -> {
                return PitVariants.MARIANA.getContents();
            }
            case ONFISHE -> {
                return PitVariants.ONFISHE.getContents();
            }
        }
        return null;
    }

    /** FORM: [windowSize, itemsToClick] **/
    @Contract("_ -> !null")
    public static Integer[] chooseSizes(PitGame pitGame) {
        switch (pitGame) {
            case STANDARD -> {
                return new Integer[]{PitVariants.STANDARD.windowSize, PitVariants.STANDARD.itemsToClick};
            }
            case COOKIE -> {
                return new Integer[]{PitVariants.COOKIE.windowSize, PitVariants.COOKIE.toClick};
            }
            case MARIANA -> {
                return new Integer[]{PitVariants.MARIANA.windowSize, PitVariants.MARIANA.toClick};
            }
            case ONFISHE -> {
                return new Integer[]{PitVariants.ONFISHE.windowSize, PitVariants.ONFISHE.toClick};
            }
        }
        return null;
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer s) {
        PitPlayer pp = getPitPlayer(player);
        switch (defaultPitGame) {
            case STANDARD -> PitVariants.STANDARD.onItemClick(pp, clickedItem, s);
            case COOKIE -> PitVariants.COOKIE.onItemClick(pp, clickedItem, s);
            case MARIANA -> PitVariants.MARIANA.onItemClick(pp, clickedItem, s);
            case ONFISHE -> PitVariants.ONFISHE.onItemClick(pp, clickedItem, s);
        }
    }

    public static void addPitPlayer(Player player, PitPlayer pitPlayer) {
        if(pitPlayers.containsKey(player)) return;
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

    public static void removePitPlayer(Player player) {
        if(!hasPitPlayer(player)) return;
        pitPlayers.remove(player);
    }
}
