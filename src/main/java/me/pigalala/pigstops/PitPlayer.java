package me.pigalala.pigstops;

import me.pigalala.pigstops.pit.*;
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
    public boolean isEditing = false;

    public PitPlayer(Player player){
        this.player = player;
        PitManager.addPitPlayer(player, this);
    }

    public void reset() {
        pitWindow = null;
        pit = null;
    }
}