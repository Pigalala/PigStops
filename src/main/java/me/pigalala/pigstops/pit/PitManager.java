package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

import static me.pigalala.pigstops.PigStops.pitGames;

public abstract class PitManager {

    private static final PigStops plugin = PigStops.getPlugin();

    private static final HashMap<Player, PitPlayer> pitPlayers = new HashMap<>();
    private static Material pitBlock;

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


    public static void setDefaultPitGame(PitGame game) {
        plugin.getConfig().set("pitGame", game.getPath());
        plugin.saveConfig();
        plugin.reloadConfig();
    }
    public static void setDefaultPitGame(File file) {
        plugin.getConfig().set("pitGame", file.getPath());
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public static PitGame getDefaultPitGame() {
        File f = new File(plugin.getConfig().getString("pitGame"));

        return pitGames.get(new PitGame(f).name);
    }


    public static void setPitBlock(Material block) {
        pitBlock = block;
        plugin.getConfig().set("pitBlock", pitBlock.name().toLowerCase());
        plugin.saveConfig();
        plugin.reloadConfig();
    }
    public static Material getPitBlock() {return pitBlock;}
}
