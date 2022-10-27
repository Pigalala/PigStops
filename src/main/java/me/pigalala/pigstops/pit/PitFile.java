package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class PitFile {

    public PitFile(String path, String name, Integer invSize) throws IOException {
        File f = new File(path);
        f.createNewFile();

        FileWriter writer = new FileWriter(f);
        writer.write(ChatColor.translateAlternateColorCodes('&', name) + "\n" +
                invSize + "\n" +
                1 + "\n" +
                ("null\n").repeat(54));
        writer.close();
    }

    public static void updateContents(File file, String name, String invSize, String itemsToClick, List<String> contents) throws IOException {
        file.delete();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);

        writer.write(name + "\n");
        writer.write(invSize + "\n");
        writer.write(itemsToClick + "\n");
        for (String item: contents) {
            writer.write(item + "\n");
        }

        writer.close();
    }

    public static List<String> readFile(File file) throws IOException {
        return Files.readAllLines(file.toPath());
    }

    public static File renameFile(File file, String newName) {
        file.renameTo(new File(ConfigManager.customPSPath + File.separator + newName + ".pigstop"));
        file = new File(ConfigManager.customPSPath + File.separator + newName + ".pigstop");

        return file;
    }
}
