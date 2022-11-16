package me.pigalala.pigstops;

import me.pigalala.pigstops.pit.PitGame;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

import static me.pigalala.pigstops.PigStops.pitGames;

public abstract class OinkConfig {

    private static final PigStops plugin = PigStops.getPlugin();
    private static final FileConfiguration config = plugin.getConfig();

    public static final String customPSPath = plugin.getDataFolder().getPath() + File.separator + "customPS";

    public static void onStartup() {
        plugin.saveDefaultConfig();

        loadPitBlock();
        loadPitGame();

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

    public static void loadPitGame() {
        try {
            File fi = new File(config.getString("pitGame"));
            if(!fi.exists()) throw new NullPointerException();
            PigStops.defaultPitGame = new PitGame(fi);

            Arrays.stream(new File(OinkConfig.customPSPath).listFiles()).toList().forEach(f -> {
                if(f == null) return;
                PitGame pitGame = new PitGame(f);
                pitGames.put(pitGame.name, pitGame);
            });

        } catch (IllegalArgumentException | NullPointerException e) {
            plugin.getLogger().log(Level.WARNING, "'pitGame' in PigStops plugin config does not exist.");
            Utils.setDefaultPitGame(null);
        }
    }

    private static void updateConfig() {
        if(!config.isSet("pitBlock")) {
            config.set("pitBlock", "");
        }
        if(!config.isSet("pitGame")) {
            config.set("pitGame", "");
        }

        plugin.saveConfig();
    }
}
