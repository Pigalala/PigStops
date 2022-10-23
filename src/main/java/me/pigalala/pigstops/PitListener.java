package me.pigalala.pigstops;

import me.makkuusen.timing.system.event.EventDatabase;
import me.pigalala.pigstops.enums.PitType;
import me.pigalala.pigstops.pit.Pit;
import me.pigalala.pigstops.pit.PitManager;
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

public class PitListener implements Listener {

    public PitListener(){
        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new PitPlayer(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PitManager.removePitPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPitWindowClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().startsWith(PitManager.pitNameBase)) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null) return;
            PitManager.onItemClick(player, e.getCurrentItem(), e.getSlot());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(!(p.getVehicle() instanceof Boat)) return;

        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;

        if(!driver.get().getHeat().isActive()) return;

        if(!p.getLocation().add(new Vector(0, -2, 0)).getBlock().getType().equals(PigStops.getPlugin().getPitBlock())) return;

        if(PitManager.getPitPlayer(p).getAttachedPit() != null) return;

        if (!driver.get().getCurrentLap().isPitted()) {
            PitManager.getPitPlayer(p).attachPit(new Pit(PitManager.getPitPlayer(p), PitType.REAL));
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        if(e.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) return;
        PitPlayer pp = PitManager.getPitPlayer((Player) e.getPlayer());
        if(pp.getAttachedPit() == null) return;
        pp.reset();
    }
}
