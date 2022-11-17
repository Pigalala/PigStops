package me.pigalala.pigstops;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.pigalala.pigstops.pit.PitGame;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;
    public static final HashMap<Player, PitPlayer> pitPlayers = new HashMap<>();
    public static final HashMap<String, PitGame> pitGames = new HashMap<>();
    public static PitGame defaultPitGame;

    public static Material pitBlock;

    @Override
    public void onEnable() {
        plugin = this;

        OinkConfig.onStartup();
        setupCommands();
        new OinkListener();
    }

    private void setupCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(plugin);

        commandManager.getCommandCompletions().registerAsyncCompletion("pits", c -> pitGames.keySet());

        commandManager.getCommandCompletions().registerAsyncCompletion("items", c -> {
            List<String> e = new ArrayList<>();
            for(Material v : Material.values()) {
                if(v == Material.AIR) continue;
                e.add(v.toString().toLowerCase());
            }
            return e;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("blocks", c -> {
            List<String> blocks = new ArrayList<>();
            Arrays.stream(Material.values()).toList().forEach(e -> {
                if(!e.isSolid()) return;
                if(!e.isBlock()) return;
                blocks.add(e.toString().toLowerCase());
            });
            return ImmutableList.copyOf(blocks.toArray(new String[0]));
        });

        commandManager.getCommandContexts().registerContext(PitGame.class, PitGame.getPitGameContextResolver());
        commandManager.getCommandContexts().registerContext(Material.class, OinkCommand.getMaterialContextResolver());
        commandManager.getCommandContexts().registerContext(Sound.class, OinkCommand.getSoundContextResolver());

        commandManager.registerCommand(new OinkCommand());
    }

    public static PigStops getPlugin() {return plugin;}
}