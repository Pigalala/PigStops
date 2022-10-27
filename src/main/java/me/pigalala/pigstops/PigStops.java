package me.pigalala.pigstops;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;

    @Override
    public void onEnable() {
        plugin = this;

        new PitListener();

        setupCommands();
        ConfigManager.onStartup();
    }

    private void setupCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new CommandPit());

        commandManager.getCommandCompletions().registerAsyncCompletion("pits", c -> {
            List<String> games = new ArrayList<>();
            Arrays.stream(new File(ConfigManager.customPSPath).listFiles()).toList().forEach(f -> {
                try {
                    games.add(Files.readAllLines(f.toPath()).get(0));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return games;
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