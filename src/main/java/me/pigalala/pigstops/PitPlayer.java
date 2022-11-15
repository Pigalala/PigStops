package me.pigalala.pigstops;

import me.pigalala.pigstops.pit.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class PitPlayer {
    private final Player player;

    public Pit pit;
    public PitEditor editor;

    private boolean debugMode;

    public PitPlayer(Player player){
        this.player = player;
        PigStops.pitPlayers.put(player, this);
    }

    public void playSound(Sound sound, Float... volumepitch) {
        float vol = 1f, pitch = 1f;
        if(volumepitch.length == 2) {
            vol = volumepitch[0];
            pitch = volumepitch[1];
        }

        player.playSound(player, sound, vol, pitch);
    }

    public void newPit(Pit.Type pitType) {
        pit = new Pit(this, pitType);
    }

    public PitEditor newEditor(PitGame game) {
        this.editor = new PitEditor(this, game);
        return editor;
    }

    public boolean toggleDebugMode() {
        debugMode = !debugMode;
        return debugMode;
    }

    public boolean isInDebugMode() {
        return debugMode;
    }

    public boolean isPitting() {
        return pit != null;
    }

    public Player getPlayer() {
        return player;
    }

    public static PitPlayer of(Player player) {
        return PigStops.pitPlayers.get(player);
    }

    public static List<PitPlayer> getAll() {
        return (List<PitPlayer>) PigStops.pitPlayers.values();
    }
}