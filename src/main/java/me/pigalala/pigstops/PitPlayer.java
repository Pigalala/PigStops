package me.pigalala.pigstops;

import me.pigalala.pigstops.enums.PitType;
import me.pigalala.pigstops.pit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.time.Instant;

public class PitPlayer {
    private final Player player;

    private Instant startingTime;
    private Integer itemsToClick;
    private PitType pitType;
    private Inventory pitWindow;
    private Pit pit;

    public PitPlayer(Player player){
        this.player = player;
        PitManager.addPitPlayer(player, this);
    }

    public Player getPlayer() {return player;}

    public void reset() {
        this.pitWindow = null;
        this.pit = null;
    }

    public void setItemsToClick(Integer itemsToClick) {
        this.itemsToClick = itemsToClick;
    }
    public Integer getItemsToClick() {
        return itemsToClick;
    }

    public void setStartingTime(Instant time) {
        this.startingTime = time;
    }
    public Instant getStartingTime() {
        return startingTime;
    }

    public void setPitWindow(Inventory pitWindow) {
        this.pitWindow = pitWindow;
    }
    public Inventory getPitWindow() {
        return pitWindow;
    }

    public void setPitType(PitType pitType) {
        this.pitType = pitType;
    }
    public PitType getPitType() {
        return pitType;
    }

    public void attachPit(Pit pit) {
        this.pit = pit;
    }
    public Pit getAttachedPit() {
        return pit;
    }
}