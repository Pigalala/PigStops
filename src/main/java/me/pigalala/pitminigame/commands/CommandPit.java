package me.pigalala.pitminigame.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.CommandCompletion;
import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import me.pigalala.pitminigame.pit.PitCOOKIE;
import me.pigalala.pitminigame.pit.PitNORMAL;
import org.bukkit.entity.Player;

@CommandAlias("pigstop|pit")
public class CommandPit extends BaseCommand {

    @Default
    public static void practisePit(Player player) {
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.NORMAL) {
            new PitNORMAL(player, PitType.FAKE);
            return;
        }
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE) {
            new PitCOOKIE(player, PitType.FAKE);
            return;
        }
    }

    @Subcommand("setgame")
    @CommandCompletion("@pits")
    @CommandPermission("pigstop.modify")
    public static void setNewGame(Player player, PitGame game) {
        PigStops.getPlugin().setDefaultPitGame(game);
        player.sendMessage("§aSet new pit game to " + game.name());
    }
}
