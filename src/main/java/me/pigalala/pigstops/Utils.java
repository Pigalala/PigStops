package me.pigalala.pigstops;

import me.makkuusen.timing.system.heat.Heat;
import org.bukkit.ChatColor;

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
}
