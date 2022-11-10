package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.OinkConfig;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PitEditor implements Listener {

    private final PitPlayer pp;
    private final PitGame pitGame;

    public PitEditor(PitPlayer pp, PitGame pitGame) {
        PigStops.getPlugin().getServer().getPluginManager().registerEvents(this, PigStops.getPlugin());
        this.pp = pp;
        this.pitGame = pitGame;

        pitGame.update();
        open();
    }

    public void open() {
        Inventory editInventory = Bukkit.createInventory(pp.player, pitGame.inventorySize, Component.text("§6" + pitGame.name));

        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < pitGame.inventorySize; i++) {
            items.add(pitGame.contents.get(i));
        }
        editInventory.setContents(items.toArray(new ItemStack[0]));
        pp.player.openInventory(editInventory);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if(!e.getPlayer().equals(pp.player)) return;
        if(!PigStops.pitGames.containsKey(e.getView().getTitle().replaceAll("§6", ""))) return;
        File f = new File(OinkConfig.customPSPath + File.separator + e.getView().getTitle().replaceAll("§6", "") + ".pigstop");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);

        int g = 0;
        for(int i = 0; i < pitGame.inventorySize; i++) {
            if(e.getInventory().getContents()[i] == null) {
                yamlConfig.set("item" + i, new ItemStack(Material.AIR));
                continue;
            }
            yamlConfig.set("item" + i, e.getInventory().getContents()[i]);
            g++;
        }

        yamlConfig.set("itc", g);

        try {
            yamlConfig.save(f);
        } catch (IOException t) {
        }

        pitGame.update();

        pp.editor = null;
    }
}
