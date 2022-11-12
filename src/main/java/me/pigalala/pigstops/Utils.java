package me.pigalala.pigstops;

import me.makkuusen.timing.system.heat.Heat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public abstract class Utils {

    public static final String pitNameBase = "§dPigStop §r- §d";

    public static String getCustomMessage(String message, String... replacements) {

        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }

        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    public static void broadcastMessage(String message, Heat heat) {
        heat.getParticipants().forEach(participant -> {
            if(participant.getTPlayer().getPlayer() == null) return;
            participant.getTPlayer().getPlayer().sendMessage(message);
        });
    }

    public static void createNewPitFile(String path, String name, Integer invSize) {
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);

        yamlConfig.set("name", name);
        yamlConfig.set("invsize", invSize);
        yamlConfig.set("background", new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        for (int i = 0; i < 54; i++) {
            yamlConfig.set("item" + i, new ItemStack(Material.AIR));
        }

        try {
            yamlConfig.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setPitBlock(Material block) {
        PigStops.pitBlock = block;
        PigStops.getPlugin().getConfig().set("pitBlock", PigStops.pitBlock.name().toLowerCase());
        PigStops.getPlugin().saveConfig();
        PigStops.getPlugin().reloadConfig();
    }
}
