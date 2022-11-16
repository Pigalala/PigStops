package me.pigalala.pigstops;

import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.pigalala.pigstops.pit.*;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Boat;
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
        PigStops.pitPlayers.remove(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        PitPlayer pp = PitPlayer.of(e.getPlayer());
        if(!pp.getPlayer().getLocation().add(new Vector(0, -2, 0)).getBlock().getType().equals(PigStops.pitBlock) || pp.isPitting()) return;

        if(!(pp.getPlayer().getVehicle() instanceof Boat)) {
            if(!pp.isInDebugMode()) return;
            pp.getPlayer().sendActionBar(Component.text("§aYou are standing on a pitblock"));
            return;
        }

        var driver = TimingSystemAPI.getDriverFromRunningHeat(pp.getPlayer().getUniqueId());
        if(!driver.isPresent()) {
            if(pp.isInDebugMode()) {
                pp.newPit(Pit.Type.FAKE);
                pp.getPlayer().sendMessage("§aDebugMode has been " + (pp.toggleDebugMode() ? "enabled" : "disabled"));
            }
            return;
        }

        if(driver.get().getHeat().isActive() && driver.get().getCurrentLap() != null && !driver.get().getCurrentLap().isPitted()) pp.newPit(Pit.Type.REAL);
    }
}
