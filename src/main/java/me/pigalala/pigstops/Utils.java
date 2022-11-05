package me.pigalala.pigstops;

import me.makkuusen.timing.system.heat.Heat;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import static me.pigalala.pigstops.PigStops.pitGames;

public abstract class Utils {

    public static String defaultContentLine = "000 f";

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

    public static void createNewPitFile(String path, String name, Integer invSize) throws IOException {
        /*
        name
        invsize
        itemstoclick
        contents (54 lines)
        backgroundItem

        Contents line:

        000 "yo" 32 false
         */

        File f = new File(path);
        f.createNewFile();

        FileWriter writer = new FileWriter(f);
        String b = ChatColor.translateAlternateColorCodes('&', name) + "\n" +
                invSize + "\n" +
                1 + "\n" +
                Material.BLUE_STAINED_GLASS_PANE.ordinal() + "\n" +
                (defaultContentLine + "\n").repeat(54);
        writer.write(b);
        writer.close();
    }

    public static Material getMaterialFromI(int i) {
        final HashMap<Integer, Material> intMat = new HashMap<>();
        for(Material m : Material.values()) intMat.put(m.ordinal(), m);
        return intMat.get(i);
    }

    public static void updateContents(File file, String name, String invSize, String itemsToClick, Integer backgroundItem, List<String> contents) throws IOException {
        file.delete();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);

        writer.write(name + "\n");
        writer.write(invSize + "\n");
        writer.write(itemsToClick + "\n");
        writer.write(backgroundItem + "\n");
        for (String item: contents) {
            writer.write(item + "\n");
        }

        writer.close();
    }

    public static List<String> readFile(File file) throws IOException {
        return Files.readAllLines(file.toPath());
    }

    public static File renameFile(File file, String newName) {
        file.renameTo(new File(OinkConfig.customPSPath + File.separator + newName + ".pigstop"));
        file = new File(OinkConfig.customPSPath + File.separator + newName + ".pigstop");

        return file;
    }
}
