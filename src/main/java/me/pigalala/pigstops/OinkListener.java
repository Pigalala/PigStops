package me.pigalala.pigstops;

import me.makkuusen.timing.system.event.EventDatabase;
import me.pigalala.pigstops.pit.*;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        Player p = e.getPlayer();
        if(!(p.getVehicle() instanceof Boat)) return;

        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;

        if(!driver.get().getHeat().isActive()) return;

        if(!p.getLocation().add(new Vector(0, -2, 0)).getBlock().getType().equals(PigStops.pitBlock)) return;

        if(PigStops.pitPlayers.get(p).pit != null) return;
        if(driver.get().getCurrentLap() == null) return;

        if (!driver.get().getCurrentLap().isPitted()) {
            PigStops.pitPlayers.get(p).pit = new Pit(PigStops.pitPlayers.get(p), PitType.REAL);
        }
    }
}
