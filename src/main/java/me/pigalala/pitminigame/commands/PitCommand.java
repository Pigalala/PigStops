package me.pigalala.pitminigame.commands;

import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import me.pigalala.pitminigame.pit.PitCookie;
import me.pigalala.pitminigame.pit.PitNormal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PitCommand implements CommandExecutor {

    public PitCommand(String command){
        PigStops.getPlugin().getCommand(command).setExecutor(this);
        PigStops.getPlugin().getCommand(command).setTabCompleter(new PitTabCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p){
            if(args.length >= 1){
                if(args[1].isEmpty()) {
                    p.sendMessage("§cPlease provide a pit game");
                    return true;
                }

                List<String> games = new ArrayList<>();
                for(PitGame game : PitGame.values()){
                    games.add(game.name());
                }

                if(games.contains(args[1])) {
                    if(args[0].equalsIgnoreCase("setnew") && p.hasPermission("pigstop.modify")){
                        PigStops.getPlugin().setDefaultPitGame(PitGame.valueOf(args[1]));
                        p.sendMessage("§aSuccessfully changed pit game");
                    }
                } else {
                    p.sendMessage("§cPit game not found");
                    return true;
                }
            } else {
                if(PigStops.getPlugin().getDefaultPitGame() == PitGame.NORMAL) new PitNormal(p, PitType.FAKE);
                if(PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE) new PitCookie(p, PitType.FAKE);
                return true;
            }
        }
        return true;
    }
}