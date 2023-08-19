package me.pigalala.pigstops;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.pigalala.pigstops.pit.management.pitmodes.Pit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class OinkListener implements Listener {

    public OinkListener(){
        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new PitPlayer(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PitPlayer.getAll().remove(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        PitPlayer pp = PitPlayer.of(e.getPlayer());
        if(pp.isInPracticeMode()) pp.getPlayer().sendActionBar(Component.text("You are in PigStop's PracticeMode").color(TextColor.color(0xF38AFF)));
        if(pp.isInPracticeMode() && TimingSystemAPI.getDriverFromRunningHeat(pp.getPlayer().getUniqueId()).isPresent()) pp.togglePracticeMode();
        if(pp.isPitting() || !pp.getPlayer().getLocation().add(new Vector(0, -2, 0)).getBlock().getType().equals(PigStops.pitBlock)) return;

        var driver = TimingSystemAPI.getDriverFromRunningHeat(pp.getPlayer().getUniqueId());
        if(driver.isEmpty()) {
            if(pp.isInPracticeMode()) {
                pp.newPit(Pit.Type.FAKE);
            }
            return;
        } else {
            if(pp.isInPracticeMode()) pp.togglePracticeMode();
        }

        if(driver.get().getHeat().isActive() && driver.get().getCurrentLap() != null && !driver.get().getCurrentLap().isPitted()) pp.newPit(Pit.Type.REAL);
    }

    @EventHandler
    public void onStopSpectating(PlayerStopSpectatingEntityEvent e) {
        if(!(e.getSpectatorTarget() instanceof Player t)) return;
        PitPlayer spectator = PitPlayer.of(e.getPlayer());
        PitPlayer target = PitPlayer.of(t);

        if(!target.isPitting()) return;
        target.getPit().removeSpectator(spectator);
    }
}
