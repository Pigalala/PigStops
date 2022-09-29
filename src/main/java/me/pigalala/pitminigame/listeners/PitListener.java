package me.pigalala.pitminigame.listeners;

import me.makkuusen.timing.system.event.EventDatabase;
import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import me.pigalala.pitminigame.pit.PitCOOKIE;
import me.pigalala.pitminigame.pit.PitNORMAL;
import org.bukkit.Material;
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
        if (e.getView().getTitle().startsWith(PigStops.getPitWindow().pitNameBase)) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null) return;
            if (PigStops.getPlugin().getDefaultPitGame() == PitGame.NORMAL){
                PitNORMAL.onItemClick(player, e.getCurrentItem());
                return;
            }
            if (PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE){
                PitCOOKIE.onItemClick(player, e.getCurrentItem());
                return;
            }
        }
    }

    @EventHandler
    public void onMove(VehicleMoveEvent e) {
        if (!(e.getVehicle() instanceof Boat b)) return;
        if (e.getVehicle().getPassengers().isEmpty()) return;
        if (e.getVehicle().getPassengers().size() != 1) return;

        Player p = (Player) b.getPassenger();
        if (p.getLocation().add(new Vector(0, -1, 0)).getBlock().getType() != Material.REDSTONE_BLOCK) return;

        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;

        if(driver.get().getHeat().isActive()){
            if (!driver.get().getCurrentLap().isPitted()) {
                if(PigStops.getPlugin().getDefaultPitGame() == PitGame.NORMAL){
                    new PitNORMAL(p, PitType.REAL);
                    return;
                }
                if(PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE){
                    new PitCOOKIE(p, PitType.REAL);
                }
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        if(e.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) return;

        PigStops.getPitWindow().reset((Player) e.getPlayer());
    }
}
