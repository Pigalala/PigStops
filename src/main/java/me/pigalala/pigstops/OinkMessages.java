package me.pigalala.pigstops;

import me.makkuusen.timing.system.TPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class OinkMessages {

    private OinkMessages() {
    }

    public static TextComponent getSoloFinishText(String time, int accuracy, int misclicks) {
        return Component.text()
                .content("PigStop finished in ").color(NamedTextColor.GREEN)
                .append(Component.text(time).color(NamedTextColor.GOLD))
                .hoverEvent(
                        Component.text().content(time).color(TextColor.color(0xF38AFF))
                        .appendNewline().append(Component.text("Accuracy: ").color(TextColor.color(0x7BF200)))
                        .append(Component.text(accuracy + "%"))
                        .appendNewline().append(Component.text("Misclicks: ").color(TextColor.color(0x7BF200)))
                        .append(Component.text(misclicks))
                        .build()
                )
                .build();
    }

    public static TextComponent getRaceFinishText(TPlayer player, String pitName, int pit, String time, int accuracy, int misclicks) {
        return Component.text().content("").color(NamedTextColor.GREEN)
                .append(Component.text("|| ", player.getTextColor(), TextDecoration.BOLD, TextDecoration.ITALIC))
                .append(Component.text(player.getName(), NamedTextColor.WHITE))
                .append(Component.text(" has completed PigStop "))
                .append(Component.text(pit).color(NamedTextColor.GOLD))
                .append(Component.text(" in "))
                .append(Component.text(time).color(NamedTextColor.GOLD))
                .hoverEvent(
                        Component.text().content("").color(TextColor.color(0xF38AFF))
                        .append(Component.text("|| ", player.getTextColor(), TextDecoration.BOLD, TextDecoration.ITALIC))
                        .append(Component.text(player.getName(), NamedTextColor.WHITE))
                        .appendNewline().append(Component.text("----------").color(NamedTextColor.GRAY))
                        .appendNewline().append(Component.text(pitName))
                        .appendNewline().append(Component.text("----------").color(NamedTextColor.GRAY))
                        .appendNewline().append(Component.text(time))
                        .appendNewline().append(Component.text("Accuracy: ").color(TextColor.color(0x7BF200))).append(Component.text(accuracy + "%"))
                        .appendNewline().append(Component.text("Misclicks: ").color(TextColor.color(0x7BF200))).append(Component.text(misclicks))
                        .build()
                )
                .build();
    }
}
