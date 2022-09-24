package me.pigalala.pitminigame;

import me.pigalala.pitminigame.commands.PitCommand;
import me.pigalala.pitminigame.listeners.PitListener;
import me.pigalala.pitminigame.pit.PitWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;
    private PitWindow pitWindow;
    private PitGame defaultPit;

    private final Material pitBlock = Material.REDSTONE_BLOCK;

    @Override
    public void onEnable() {
        plugin = this;
        defaultPit = PitGame.NORMAL;

        new PitCommand("pit");
        new PitListener();

        pitWindow = new PitWindow();
    }

    @Override
    public void onDisable(){
        Bukkit.getOnlinePlayers().forEach(player -> getPitWindow().reset(player));
    }

    public PitWindow getPitWindow() {return pitWindow;}

    public static PigStops getPlugin() {return plugin;}

    public void setDefaultPitGame(PitGame defaultPit) {this.defaultPit = defaultPit;}

    public PitGame getDefaultPitGame() {return defaultPit;}

    public Material getPitBlock() {return pitBlock;}
}