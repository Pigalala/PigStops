package me.pigalala.pigstops;

import me.pigalala.pigstops.pit.management.PitEditor;
import me.pigalala.pigstops.pit.management.PitGame;
import me.pigalala.pigstops.pit.management.pitmodes.Pit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PitPlayer {
    private final Player player;

    public Pit pit;
    public PitEditor editor;

    private boolean practiceMode;

    public Location practiceModeStart;

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
        pit = Pit.newPitOfMode(PigStops.defaultPitGame, this, pitType);
    }

    public void newPit(Pit.Type pitType, PitGame pg) {
        pit = Pit.newPitOfMode(pg, this, pitType);
    }

    public Pit getPit() {
        return pit;
    }

    public PitEditor newEditor(PitGame game) {
        this.editor = new PitEditor(this, game);
        return editor;
    }

    public boolean togglePracticeMode() {
        practiceMode = !practiceMode;
        return practiceMode;
    }

    public boolean isInPracticeMode() {
        return practiceMode;
    }

    public boolean isPitting() {
        return pit != null;
    }

    public Player getPlayer() {
        return player;
    }

    public static PitPlayer of(Player player) {
        PitPlayer pp = PigStops.pitPlayers.get(player);

        if(pp == null) return new PitPlayer(player);
        return pp;
    }

    public static HashMap<Player, PitPlayer> getAll() {
        return PigStops.pitPlayers;
    }
}