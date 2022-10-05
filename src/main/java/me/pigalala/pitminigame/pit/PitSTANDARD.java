package me.pigalala.pitminigame.pit;

import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.enums.PitGame;
import me.pigalala.pitminigame.enums.PitType;
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

public class PitSTANDARD implements Pit {
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
        paddleDamage.setDamage(new Random().nextInt(10,59));
        paddleDamage.setDisplayName("Worn Paddle");
        paddleDamage.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paddleDamage.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        paddle.setItemMeta(paddleDamage);
    }

    public static void onItemClick(Player player, ItemStack clickedItem){
        if(clickedItem.getType() == Material.WOODEN_SHOVEL){
            Damageable paddleMeta = (Damageable) clickedItem.getItemMeta();
            if(paddleMeta.hasEnchants()) return;
            paddleMeta.addEnchant(Enchantment.LUCK, 1, true);
            paddleMeta.setDisplayName("New Paddle");
            paddleMeta.setDamage(1);
            clickedItem.setItemMeta(paddleMeta);

            if(Pit.getItemsToClick().get(player) == 2){
                player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.MASTER, 0.5f, 1f);
            }

            Pit.getItemsToClick().put(player, Pit.getItemsToClick().get(player) - 1);

            if(Pit.isFinished(player)){
                for (int i = 0; i < 3; i++) {
                    Bukkit.getScheduler().runTaskLater(PigStops.getPlugin(), () -> {
                        player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 0.5f, 1f);
                    },1);
                }
                Pit.finishPits(player);
            }
        }
        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            Pit.shuffleItems(player);
        }
    }
}
