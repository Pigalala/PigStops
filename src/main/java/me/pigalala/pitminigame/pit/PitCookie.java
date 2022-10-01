package me.pigalala.pitminigame.pit;

import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitGame;
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
import java.util.Random;

public class PitCOOKIE {

    private final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    private final ItemStack cookie = new ItemStack(Material.COOKIE);

    public PitCOOKIE(Player player, PitType pitType){
        setItemMetas();
        PigStops.getPitWindow().createWindow(player, pitType, setContents(), PitGame.COOKIE.name(), 27);
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            items.add(background);
        }
        for (int i = 0; i < 10; i++) {
            try {
                int rand = new Random().nextInt(0, 27);
                items.add(rand, cookie);
            } catch (IndexOutOfBoundsException e) {
                i--;
            }
        }

        return items.toArray(new ItemStack[0]);
    }

    private void setItemMetas(){
        // BACKGROUND ITEM
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // COOKIE
        ItemMeta cookieMeta = cookie.getItemMeta();
        cookieMeta.setDisplayName("Cookie");
        cookieMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        cookieMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        cookie.setItemMeta(cookieMeta);
    }

    public static void onItemClick(Player player, ItemStack clickedItem){
        if(clickedItem.getType() == Material.COOKIE){
            ItemMeta cookieMeta = clickedItem.getItemMeta();
            if(cookieMeta.hasEnchants()) return;
            cookieMeta.addEnchant(Enchantment.LUCK, 1, true);
            clickedItem.setItemMeta(cookieMeta);

            PigStops.getPitWindow().itemsToClick.put(player, PigStops.getPitWindow().itemsToClick.get(player) - 1);
            player.playSound(player, Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 0.5f, 1f);
        }

        if(PigStops.getPitWindow().isFinished(player)){
            player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 0.5f, 1f);
            PigStops.getPitWindow().finishPits(player);
        }

        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            PigStops.getPitWindow().shuffleItems(player);
        }
    }
}
