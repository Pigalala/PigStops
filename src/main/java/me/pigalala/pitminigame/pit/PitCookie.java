package me.pigalala.pitminigame.pit;

import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PitCookie {
    private final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    private final ItemStack wheat = new ItemStack(Material.WHEAT);
    private final ItemStack cocoa = new ItemStack(Material.COCOA_BEANS);

    public PitCookie(Player player, PitType pitType){
        setItemMetas();
        PigStops.getPlugin().getPitWindow().createWindow(player, pitType, setContents());
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            items.add(background);
        }
        for (int i = 0; i < 2; i++) {
            int rand = (int) Math.floor(Math.random() * 25);
            items.add(rand, wheat);
        }
        int rand = (int) Math.floor(Math.random() * 25);
        items.add(rand, cocoa);

        return items.toArray(new ItemStack[0]);
    }

    private void setItemMetas(){
        // BACKGROUND ITEM
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // COCOA
        ItemMeta cocoaMeta = cocoa.getItemMeta();
        cocoaMeta.setDisplayName("Cocoa Beans");
        cocoaMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        cocoaMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        cocoa.setItemMeta(cocoaMeta);

        // WHEAT
        ItemMeta wheatMeta = wheat.getItemMeta();
        wheatMeta.setDisplayName("Wheat");
        wheatMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        wheatMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wheat.setItemMeta(wheatMeta);
    }
}
