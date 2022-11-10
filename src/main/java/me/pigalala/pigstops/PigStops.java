package me.pigalala.pigstops;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.pigalala.pigstops.pit.PitGame;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;
    public static HashMap<String, PitGame> pitGames;

    @Override
    public void onEnable() {
        plugin = this;
        pitGames = new HashMap<>();

        new OinkListener();

        OinkConfig.onStartup();
        setupCommands();
    }

    private void setupCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new OinkCommand());

        commandManager.getCommandCompletions().registerAsyncCompletion("pits", c -> pitGames.keySet());

        commandManager.getCommandCompletions().registerAsyncCompletion("items", c -> {
            List<String> e = new ArrayList<>();
            for(Material v : Material.values()) {
                if(v == Material.AIR) continue;
                e.add(v.toString().toLowerCase());
            }
            return e;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("blocks", c-> {
            List<String> blocks = new ArrayList<>();
            Arrays.stream(Material.values()).toList().forEach(e -> {
                if(!e.isSolid()) return;
                if(!e.isBlock()) return;
                blocks.add(e.toString().toLowerCase());
            });
            return ImmutableList.copyOf(blocks.toArray(new String[0]));
        });
    }

    public static PigStops getPlugin() {return plugin;}
}