package me.pigalala.pitminigame.pit;

import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
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
        PigStops.getPitWindow().createWindow(player, pitType, setContents());
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

    public static void cookieItemClick(Player player, ItemStack clickedItem){
        if(clickedItem.getType() == Material.WHEAT){
            if((PigStops.getPitWindow().itemsToClick.get(player) != 3 && PigStops.getPitWindow().itemsToClick.get(player) != 1)) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
                return;
            }

            ItemMeta wheatMeta = clickedItem.getItemMeta();
            if(wheatMeta.hasEnchants()) return;
            wheatMeta.addEnchant(Enchantment.LUCK, 1, true);
            clickedItem.setItemMeta(wheatMeta);

            PigStops.getPitWindow().itemsToClick.put(player, PigStops.getPitWindow().itemsToClick.get(player) - 1);
            player.playSound(player, Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 0.5f, 1f);
        }
        if(clickedItem.getType() == Material.COCOA_BEANS) {
            if(PigStops.getPitWindow().itemsToClick.get(player) != 2) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
                return;
            }

            ItemMeta cocoaMeta = clickedItem.getItemMeta();
            if(cocoaMeta.hasEnchants()) return;
            cocoaMeta.addEnchant(Enchantment.LUCK, 1, true);
            clickedItem.setItemMeta(cocoaMeta);

            PigStops.getPitWindow().itemsToClick.put(player, PigStops.getPitWindow().itemsToClick.get(player) - 1);
            player.playSound(player, Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 0.5f, 1f);
        }

        if(PigStops.getPitWindow().isFinished(player)){
            player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 0.5f, 1f);
            PigStops.getPitWindow().finishPits(player);
        }

        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            PigStops.getPitWindow().shufflePlayer(player);
        }
    }
}
