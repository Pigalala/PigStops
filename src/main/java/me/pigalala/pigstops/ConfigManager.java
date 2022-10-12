package me.pigalala.pigstops;

import me.pigalala.pigstops.enums.PitGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

public class ConfigManager {

    private static final PigStops plugin = PigStops.getPlugin();
    private static final FileConfiguration config = PigStops.getPlugin().getConfig();

    private final String currentVersion = "1.1";

    public ConfigManager(){
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

    public static String getValue(String messagePath, String... replacements) {
        if(replacements.length % 2 != 0) {
            PigStops.getPlugin().getLogger().log(Level.WARNING, "Message replacements uneven, please contact Pigalala#3520", new Exception());
            return null;
        }

        if(config.getString(messagePath) == null) return null;
        String message = config.getString(messagePath);

        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }

        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }
}
