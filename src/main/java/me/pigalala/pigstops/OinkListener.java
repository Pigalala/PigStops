package me.pigalala.pigstops;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.pigalala.pigstops.pit.*;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
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
        if(pp.isPitting() || !pp.getPlayer().getLocation().add(new Vector(0, -2, 0)).getBlock().getType().equals(PigStops.pitBlock)) return;

        if(!(pp.getPlayer().getVehicle() instanceof Boat)) {
            if(!pp.isInDebugMode()) return;
            pp.getPlayer().sendActionBar(Component.text("§aYou are standing on a pitblock"));
            return;
        }

        var driver = TimingSystemAPI.getDriverFromRunningHeat(pp.getPlayer().getUniqueId());
        if(driver.isEmpty()) {
            if(pp.isInPracticeMode()) {
                pp.newPit(Pit.Type.FAKE);
            }
            if(pp.isInDebugMode()) {
                pp.newPit(Pit.Type.FAKE);
                pp.getPlayer().sendMessage("§aDebugMode has been " + (pp.toggleDebugMode() ? "enabled" : "disabled"));
            }
            return;
        } else {
            if(pp.isInPracticeMode()) pp.togglePracticeMode();
            if(pp.isInDebugMode()) pp.toggleDebugMode();
        }

        if(driver.get().getHeat().isActive() && driver.get().getCurrentLap() != null && !driver.get().getCurrentLap().isPitted()) pp.newPit(Pit.Type.REAL);
    }

    @EventHandler
    public void onPlayerExitBoat(VehicleExitEvent e) {
        if(e.getExited() instanceof Player player && e.getVehicle() instanceof Boat) {
            PitPlayer pp = PitPlayer.of(player);
            if(pp.isInPracticeMode()) {
                pp.togglePracticeMode();
                player.sendMessage("§aPracticeMode has been disabled");
            }
        }
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
