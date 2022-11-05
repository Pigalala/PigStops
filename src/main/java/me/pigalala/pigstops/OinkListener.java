package me.pigalala.pigstops;

import me.makkuusen.timing.system.event.EventDatabase;
import me.pigalala.pigstops.pit.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        PitManager.removePitPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPitWindowClick(InventoryClickEvent e) {
        PitPlayer pp = PitManager.getPitPlayer((Player) e.getWhoClicked());
        if (e.getView().getTitle().startsWith(PitManager.pitNameBase)) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null) return;
            pp.pit.onItemClicked(e.getCurrentItem(), e.getSlot());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(!(p.getVehicle() instanceof Boat)) return;

        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;

        if(!driver.get().getHeat().isActive()) return;

        if(!p.getLocation().add(new Vector(0, -2, 0)).getBlock().getType().equals(PitManager.getPitBlock())) return;

        if(PitManager.getPitPlayer(p).pit != null) return;
        if(driver.get().getCurrentLap() == null) return;

        if (!driver.get().getCurrentLap().isPitted()) {
            PitManager.getPitPlayer(p).pit = new Pit(PitManager.getPitPlayer(p), PitType.REAL);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        PitPlayer pp = PitManager.getPitPlayer((Player) e.getPlayer());
        if(pp.isEditing) {
            pp.isEditing = false;
            List<ItemStack> contents = Arrays.stream(e.getInventory().getContents()).toList();
            File f = new File(OinkConfig.customPSPath + File.separator + e.getView().getTitle().replaceAll("§6", "") + ".pigstop");

            try {
                List<String> lines = Files.readAllLines(f.toPath());
                List<String> c = new ArrayList<>();

                int t = Integer.parseInt(lines.get(1));
                for (ItemStack item: contents) {
                    if(item == null) {
                        t--;
                    }
                }

                int r = 3;
                for (ItemStack item: contents) {
                    if(item == null) {
                        c.add(Utils.defaultContentLine);
                        r++;
                        continue;
                    }
                    ItemMeta meta = item.getItemMeta();
                    PitItem h = new PitItem(item.getType().ordinal(), meta.hasEnchants());
                    c.add(h.toFileLine());
                    r++;
                }
                // fill in empty things
                for (int i = r; i < 57; i++) {
                    c.add(Utils.defaultContentLine);
                }

                Utils.updateContents(f, lines.get(0), lines.get(1), String.valueOf(t), Integer.valueOf(lines.get(3)), c);
            } catch (IOException d) {
                d.printStackTrace();
                return;
            }
            return;
        }

        if(e.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) return;
        if(pp.pit == null) return;
        pp.reset();
    }
}
