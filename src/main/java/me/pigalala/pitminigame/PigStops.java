package me.pigalala.pitminigame;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.pigalala.pitminigame.commands.CommandPit;
import me.pigalala.pitminigame.listeners.PitListener;
import me.pigalala.pitminigame.pit.PitCOOKIE;
import me.pigalala.pitminigame.pit.PitSTANDARD;
import me.pigalala.pitminigame.pit.PitWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;

    private Material pitBlock;

    @Override
    public void onEnable() {
        plugin = this;

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        new PitListener();

        setupCommands();
        loadPitBlock();
        loadPitGame();
    }

    @Override
    public void onDisable(){
        Bukkit.getOnlinePlayers().forEach(PitWindow::reset);
    }

    private void loadPitBlock(){
        try {
            setPitBlock(Material.valueOf(getConfig().getString("pitBlock").toUpperCase()));
        } catch (IllegalArgumentException e) {
            getLogger().log(Level.SEVERE, "'pitBlock' in PigStops plugin config does not exist");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadPitGame(){
        try {
            setDefaultPitGame(PitGame.valueOf(getConfig().getString("pitGame").toUpperCase()));
        } catch (IllegalArgumentException e) {
            getLogger().log(Level.SEVERE, "'pitGame' in PigStops plugin config does not exist");
            getServer().getPluginManager().disablePlugin(this);
        }
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

    /** Opens a pit game based on the default game, or using the 3rd parameter **/
    public static void openPitGame(Player player, PitType pitType, PitGame... pitGame){
        if(pitGame.length == 1){
            if(PitGame.STANDARD == pitGame[0]) new PitSTANDARD(player, pitType);
            if(PitGame.COOKIE == pitGame[0]) new PitCOOKIE(player, pitType);
        } else if(pitGame.length > 1) {
            getPlugin().getLogger().log(Level.SEVERE, "Error opening pit game. Please notify Pigalala.");
        } else {
            if(getPlugin().getDefaultPitGame() == PitGame.STANDARD) new PitSTANDARD(player, pitType);
            if(getPlugin().getDefaultPitGame() == PitGame.COOKIE) new PitCOOKIE(player, pitType);
        }
    }
}