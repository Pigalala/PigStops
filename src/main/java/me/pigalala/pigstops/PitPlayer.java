package me.pigalala.pigstops;

import me.pigalala.pigstops.pit.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.time.Instant;

public class PitPlayer {
    public final Player player;

    public Instant startingTime;
    public Integer itemsToClick;
    public PitType pitType;
    public Inventory pitWindow;
    public Pit pit;
    public PitEditor editor;

    public PitPlayer(Player player){
        this.player = player;
        PitManager.addPitPlayer(player, this);
    }

    public void reset() {
        pitWindow = null;
        pit = null;
    }

    public void playSound(Sound sound, Float... volumepitch) {
        float vol = 1f, pitch = 1f;
        if(volumepitch.length == 2) {
            vol = volumepitch[0];
            pitch = volumepitch[1];
        }

        player.playSound(player, sound, vol, pitch);
    }

    public void newEditor(PitGame game) {
        this.editor = new PitEditor(this, game);
    }
}