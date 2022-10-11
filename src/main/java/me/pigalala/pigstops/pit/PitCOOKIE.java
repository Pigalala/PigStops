package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
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

import static me.pigalala.pigstops.pit.PitManager.hasPitPlayer;

public class PitCOOKIE extends Pit {

    private final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    private final ItemStack cookie = new ItemStack(Material.COOKIE);

    public PitCOOKIE(Player player, PitType pitType){
        setItemMetas();
        Pit.createWindow(player, pitType, setContents(), PitGame.COOKIE.name(), 27, 10);
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

    public static void onItemClick(Player player, ItemStack clickedItem, Integer slot){
        if(hasPitPlayer(player)) return;
        PitPlayer pp = PitManager.getPitPlayer(player);

        if(clickedItem.getType() == Material.COOKIE){
            ItemMeta cookieMeta = clickedItem.getItemMeta();
            if(cookieMeta.hasEnchants()) return;
            cookieMeta.addEnchant(Enchantment.LUCK, 1, true);
            clickedItem.setItemMeta(cookieMeta);

            pp.setItemsToClick(pp.getItemsToClick() - 1);
            player.playSound(player, Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 0.5f, 1f);
        }

        if(Pit.isFinished(player)){
            player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
            Pit.finishPits(player);
        }

        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            Pit.shuffleItems(player, true);
        }
    }
}
