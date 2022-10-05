package me.pigalala.pigstops.listeners;

import me.makkuusen.timing.system.event.EventDatabase;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.enums.PitType;
import me.pigalala.pigstops.pit.Pit;
import me.pigalala.pigstops.pit.PitManager;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class PitListener implements Listener {

    public PitListener(){
        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());
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
    public void onMove(VehicleMoveEvent e) {
        if (!(e.getVehicle() instanceof Boat b)) return;
        if (e.getVehicle().getPassengers().isEmpty()) return;
        if (e.getVehicle().getPassengers().size() == 0) return;

        Player p = (Player) b.getPassengers().get(0);
        if (p.getLocation().add(new Vector(0, -1, 0)).getBlock().getType() != PigStops.getPlugin().getPitBlock()) return;

        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;

        if(driver.get().getHeat().isActive()){
            if (!driver.get().getCurrentLap().isPitted()) {
                PitManager.openPitGame(p, PitType.REAL);
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        if(e.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) return;

        Pit.reset((Player) e.getPlayer());
    }
}
