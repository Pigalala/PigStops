package me.pigalala.pitminigame.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("pigstop|pit")
public class CommandPit extends BaseCommand {

    @Default
    public static void practiseDefaultPit(Player player) {
        PigStops.openPitGame(player, PitType.FAKE);
    }

    @Subcommand("practise")
    @CommandCompletion("@pits")
    public static void practisePit(Player player, PitGame pitGame){
        PigStops.openPitGame(player, PitType.FAKE, pitGame);
    }

    @Subcommand("info")
    @CommandPermission("pigstop.admin")
    public static void getInfo(Player player) {
        player.sendMessage("§dPitBlock: §a" + PigStops.getPlugin().getPitBlock().name() + "\n§dPitGame: §a " + PigStops.getPlugin().getDefaultPitGame().name());
    }

    @Subcommand("setgame")
    @CommandCompletion("@pits")
    @CommandPermission("pigstop.admin")
    public static void setNewGame(Player player, PitGame game) {
        PigStops.getPlugin().setDefaultPitGame(game);
        player.sendMessage("§aSet new pit game to " + game.name());
    }

    @Subcommand("setpitblock")
    @CommandCompletion("@blocks")
    @CommandPermission("pigstop.admin")
    public static void setPitBlock(Player player, String blocke) {
        Material block;
        try {
            block = Material.valueOf(blocke.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cThat is not a valid block");
            return;
        }

        PigStops.getPlugin().setPitBlock(block);
        player.sendMessage("§aSuccessfully set pit block to " + block.toString().toLowerCase());
    }
}
