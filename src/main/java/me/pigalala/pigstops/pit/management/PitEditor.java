package me.pigalala.pigstops.pit.management;

import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PitEditor implements Listener {

    private final PitPlayer pp;
    private final PitGame pitGame;

    public PitEditor(PitPlayer pp, PitGame pitGame) {
        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());
        this.pp = pp;
        this.pitGame = pitGame;

        open();
    }

    public void open() {
        Inventory editInventory = Bukkit.createInventory(pp.getPlayer(), pitGame.inventorySize, Component.text("§6" + pitGame.name));

        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < pitGame.inventorySize; i++) {
            items.add(pitGame.contents.get(i));
        }
        editInventory.setContents(items.toArray(new ItemStack[0]));
        pp.getPlayer().openInventory(editInventory);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if(!e.getPlayer().equals(pp.getPlayer()) || !PigStops.pitGames.containsKey(e.getView().getTitle().replaceAll("§6", ""))) return;

        List<ItemStack> newContents = new ArrayList<>();
        for(ItemStack item : e.getInventory().getContents()) {
            if(item == null) {
                newContents.add(new ItemStack(Material.AIR));
                continue;
            }
            newContents.add(item);
        }

        for(int i = newContents.size(); i < 54; i++) {
            newContents.add(new ItemStack(Material.AIR));
        }

        pitGame.setContents(newContents);
        pp.editor = null;
        HandlerList.unregisterAll(this);
    }
}
