package me.pigalala.pigstops;

import me.makkuusen.timing.system.heat.Heat;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Utils {

    // Parts taken from TS :)
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
         */

        File f = new File(path);
        f.createNewFile();
        FileWriter writer = new FileWriter(f);
        String b = ChatColor.translateAlternateColorCodes('&', name) + "\n" +
                invSize + "\n" +
                1 + "\n" +
                ("null\n").repeat(54);
        writer.write(b);
        writer.close();
    }
}
