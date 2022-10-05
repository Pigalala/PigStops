package me.pigalala.pigstops;

import me.pigalala.pigstops.enums.PitGame;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

public class ConfigManager {

    private static final PigStops plugin = PigStops.getPlugin();
    private static final FileConfiguration config = PigStops.getPlugin().getConfig();

    public static void onStartup(){
        config.options().copyDefaults();
        plugin.saveDefaultConfig();

        loadPitBlock();
        loadPitGame();
    }

    public static void loadPitBlock(){
        try {
            plugin.setPitBlock(Material.valueOf(config.getString("pitBlock").toUpperCase()));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "'pitBlock' in PigStops plugin config does not exist, resetting to default.");
            plugin.setPitBlock(Material.REDSTONE_BLOCK);
        }
    }

    public static void loadPitGame(){
        try {
            plugin.setDefaultPitGame(PitGame.valueOf(config.getString("pitGame").toUpperCase()));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "'pitGame' in PigStops plugin config does not exist, resetting to default.");
            plugin.setDefaultPitGame(PitGame.STANDARD);
        }
    }
}
