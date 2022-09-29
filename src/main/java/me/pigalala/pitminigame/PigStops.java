package me.pigalala.pitminigame;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.pigalala.pitminigame.commands.CommandPit;
import me.pigalala.pitminigame.listeners.PitListener;
import me.pigalala.pitminigame.pit.PitWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;
    private static PitWindow pitWindow;
    private PitGame defaultPit;

    @Override
    public void onEnable() {
        plugin = this;
        defaultPit = PitGame.NORMAL;

        new PitListener();

        pitWindow = new PitWindow();

        setupCommands();
    }

    @Override
    public void onDisable(){
        Bukkit.getOnlinePlayers().forEach(player -> getPitWindow().reset(player));
    }

    private void setupCommands(){
        PaperCommandManager commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new CommandPit());

        commandManager.getCommandCompletions().registerAsyncCompletion("pits", c -> {
            List<String> games = new ArrayList<>();
            Arrays.stream(PitGame.values()).toList().forEach(e -> {
                games.add(e.toString());
            });
            return ImmutableList.copyOf(games.toArray(new String[0]));
        });
    }

    public static PitWindow getPitWindow() {return pitWindow;}

    public static PigStops getPlugin() {return plugin;}

    public void setDefaultPitGame(PitGame defaultPit) {this.defaultPit = defaultPit;}

    public PitGame getDefaultPitGame() {return defaultPit;}
}