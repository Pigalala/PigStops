package me.pigalala.pigstops;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.pigalala.pigstops.commands.CommandPit;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.listeners.PitListener;
import me.pigalala.pigstops.pit.PitManager;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;

    private Material pitBlock;

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
            Arrays.stream(PitGame.values()).toList().forEach(e -> {
                games.add(e.toString());
            });
            return ImmutableList.copyOf(games.toArray(new String[0]));
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("blocks", c-> {
            List<String> blocks = new ArrayList<>();
            Arrays.stream(Material.values()).toList().forEach(e -> {
                if(!e.isSolid()) return;
                if(!e.isBlock()) return;
                blocks.add(e.toString().toLowerCase(Locale.ROOT));
            });
            return ImmutableList.copyOf(blocks.toArray(new String[0]));
        });
    }

    public static PigStops getPlugin() {return plugin;}

    public void setDefaultPitGame(PitGame pit) {
        getConfig().set("pitGame", pit.name());
        saveConfig();
        reloadConfig();
        PitManager.updatePitGame(getDefaultPitGame());
    }

    public PitGame getDefaultPitGame() {
        return PitGame.valueOf(getConfig().getString("pitGame"));
    }

    public void setPitBlock(Material block) {
        pitBlock = block;
        getConfig().set("pitBlock", pitBlock.name().toLowerCase());
        saveConfig();
        reloadConfig();
    }
    public Material getPitBlock() {return pitBlock;}
}