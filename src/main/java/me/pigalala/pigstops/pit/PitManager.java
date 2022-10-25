package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PitManager {
    private static final HashMap<Player, PitPlayer> pitPlayers = new HashMap<>();

    /** Base name for a pigstop **/
    public static final String pitNameBase = "§dPigStop §r- §d";

    public static void addPitPlayer(Player player, PitPlayer pitPlayer) {
        if(pitPlayers.containsKey(player)) return;
        pitPlayers.put(player, pitPlayer);
    }

    public static PitPlayer getPitPlayer(Player player) {
        if(hasPitPlayer(player)) return new PitPlayer(player);

        return pitPlayers.get(player);
    }

    /** True = PP Not Found, False = PP Found **/
    public static Boolean hasPitPlayer(Player player) {
        return !pitPlayers.containsKey(player);
    }

    public static void removePitPlayer(Player player) {
        if(!hasPitPlayer(player)) return;
        pitPlayers.remove(player);
    }
}
