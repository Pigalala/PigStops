package me.pigalala.pigstops;

import me.pigalala.pigstops.pit.management.PitGame;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static me.pigalala.pigstops.PigStops.defaultPitGame;
import static me.pigalala.pigstops.PigStops.pitGames;

public abstract class OinkConfig {

    private static final PigStops plugin = PigStops.getPlugin();
    private static final FileConfiguration config = plugin.getConfig();

    public static final String customPSPath = plugin.getDataFolder().getPath() + File.separator + "customPS";

    public static void onStartup() {
        plugin.saveDefaultConfig();

        loadPitBlock();
        loadPitGames();

        File dir = new File(customPSPath);
        if(!dir.exists()) dir.mkdir();

        updateConfig();
    }

    public static void loadPitBlock() {
        try {
            Utils.setPitBlock(Material.valueOf(config.getString("pitBlock").toUpperCase()));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "'pitBlock' in PigStops plugin config does not exist.");
            Utils.setPitBlock(Material.REDSTONE_BLOCK);
        }
    }

    public static void loadPitGames() {
        File dir = new File(customPSPath);
        if(dir.listFiles() == null) return;

        for(File f : Arrays.stream(dir.listFiles()).toList()) {
            PitGame pg = new PitGame(f);
            plugin.getLogger().log(Level.INFO, "Loaded PigStop: " + pg.name);

            if(f.getPath().equals(config.getString("pitGame"))) {
                defaultPitGame = pg;
                plugin.getLogger().log(Level.INFO, "DefaultPitGame Activated: " + pg.name);
            }
        }
    }

    private static void updateConfig() {
        if(!config.isSet("version")) {
            config.set("version", "'2.2.0'");
        }
        if(!config.isSet("pitBlock")) {
            config.set("pitBlock", "");
        }
        if(!config.isSet("pitGame")) {
            config.set("pitGame", "");
        }

        plugin.saveConfig();
    }
}
