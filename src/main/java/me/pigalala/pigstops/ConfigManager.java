package me.pigalala.pigstops;

import me.pigalala.pigstops.pit.PitManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.logging.Level;

public class ConfigManager {

    private static final PigStops plugin = PigStops.getPlugin();
    private static final FileConfiguration config = plugin.getConfig();

    public static final String customPSPath = plugin.getDataFolder().getPath() + File.separator + "customPS";

    public static void onStartup() {
        plugin.saveDefaultConfig();

        loadPitBlock();
        loadPitGame();

        File dir = new File(customPSPath);
        if(!dir.exists()) dir.mkdir();
    }

    public static void loadPitBlock(){
        try {
            PitManager.setPitBlock(Material.valueOf(config.getString("pitBlock").toUpperCase()));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "'pitBlock' in PigStops plugin config does not exist, resetting to default.");
            PitManager.setPitBlock(Material.REDSTONE_BLOCK);
        }
    }

    public static void loadPitGame(){
        try {
            PitManager.setDefaultPitGame(new File(config.getString("pitGame")));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "'pitGame' in PigStops plugin config does not exist, resetting to default.");
            PitManager.setDefaultPitGame(new File(customPSPath + File.separator + "standard.pigstop"));
        }
    }
}
