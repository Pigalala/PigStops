package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.pigalala.pigstops.pit.PitManager.hasPitPlayer;

public class PitSTANDARD extends Pit {
    private final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    private final ItemStack paddle = new ItemStack(Material.WOODEN_SHOVEL);

    public PitSTANDARD(Player player, PitType pitType){
        setItemMetas();
        Pit.createWindow(player, pitType, setContents(), PitGame.STANDARD.name(), 27, 2);
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            items.add(background);
        }

        for (int i = 0; i < 2; i++) {
            try {
                int rand = new Random().nextInt(0, 27);
                items.add(rand, paddle);
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

        // PADDLES
        Damageable paddleDamage = (Damageable) paddle.getItemMeta();
        paddleDamage.setDamage(new Random().nextInt(30,59));
        paddleDamage.setDisplayName("Worn Paddle");
        paddleDamage.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paddleDamage.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        paddle.setItemMeta(paddleDamage);
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer slot){
        if(hasPitPlayer(player)) return;
        PitPlayer pp = PitManager.getPitPlayer(player);

        if(clickedItem.getType() == Material.WOODEN_SHOVEL){
            Damageable paddleMeta = (Damageable) clickedItem.getItemMeta();
            if(paddleMeta.hasEnchants()) return;
            paddleMeta.addEnchant(Enchantment.LUCK, 1, true);
            paddleMeta.setDisplayName("New Paddle");
            paddleMeta.setDamage(1);
            clickedItem.setItemMeta(paddleMeta);

            if(pp.getItemsToClick() == 2){
                player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.MASTER, 0.5f, 1f);
            }

            pp.setItemsToClick(pp.getItemsToClick() - 1);

            if(Pit.isFinished(player)){
                player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
                Pit.finishPits(player);
            }
        }
        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            Pit.shuffleItems(player);
        }
    }
}
