package me.pigalala.pitminigame.commands;

import me.pigalala.pitminigame.PitGame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PitTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1 && sender.hasPermission("pigstop.modify")){
            String[] options1 = {"setnew"};
            return Arrays.stream(options1).toList();
        }
        if(args[0].equalsIgnoreCase("setnew") && args.length == 2 && sender.hasPermission("pigstop.modify")){
            List<String> games = new ArrayList<>();
            for(PitGame game : PitGame.values()){
                games.add(game.name());
            }
            return games;
        }
        return new ArrayList<>();
    }
}
