package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import me.pigalala.pigstops.pit.pitvariants.COOKIE;
import me.pigalala.pigstops.pit.pitvariants.MARIANA;
import me.pigalala.pigstops.pit.pitvariants.ONFISHE;
import me.pigalala.pigstops.pit.pitvariants.STANDARD;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

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
            case STANDARD -> new STANDARD(player, pitType);
            case COOKIE -> new COOKIE(player, pitType);
            case MARIANA -> new MARIANA(player, pitType);
            case ONFISHE -> new ONFISHE(player, pitType);
        }
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer s) {
        switch (defaultPitGame) {
            case STANDARD -> STANDARD.onItemClick(player, clickedItem, s);
            case COOKIE -> COOKIE.onItemClick(player, clickedItem, s);
            case MARIANA -> MARIANA.onItemClick(player, clickedItem, s);
            case ONFISHE -> ONFISHE.onItemClick(player, clickedItem, s);
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
