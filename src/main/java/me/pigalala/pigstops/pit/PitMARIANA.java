package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PigStops;
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

public class PitMARIANA implements Pit {
    private static final ItemStack background = new ItemStack(Material.RED_STAINED_GLASS_PANE);

    private final Material[] flowers = {Material.PEONY, Material.POPPY};
    private final String[] flowerNames = {"Flower", "XD Flower", "Poopoo Flower", "Funny Flower"};

    private final Integer windowSize = 54;
    private final Integer toClick = 52;
    private final Player player;

    public PitMARIANA(Player player, PitType pitType){
        this.player = player;
        setItemMetas();
        Pit.createWindow(player, pitType, setContents(), PitGame.MARIANA.name(), windowSize, toClick);
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();
        for (int i = 0; i < windowSize; i++) {
            items.add(background);
        }

        for (int i = 0; i < toClick; i++) {
            int rand = new Random().nextInt(0, windowSize);
            if(usedSlots.contains(rand)) {
                i--;
            }
            ItemStack flower = new ItemStack(flowers[new Random().nextInt(0, flowers.length)]);
            ItemMeta flowerMeta = flower.getItemMeta();
            flowerMeta.setDisplayName(flowerNames[new Random().nextInt(0, flowerNames.length)]);
            flower.setItemMeta(flowerMeta);
            items.set(rand, flower);
            usedSlots.add(rand);
        }

        return items.toArray(new ItemStack[0]);
    }

    private void setItemMetas(){
        // BACKGROUND ITEM
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // Flower meta set in setContents()
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer slot){
        if(clickedItem.getType() != background.getType()){
            Pit.getPitWindows().get(player).setItem(slot, new ItemStack(Material.AIR));

            player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 1f, 1f);
            Pit.getItemsToClick().put(player, Pit.getItemsToClick().get(player) - 1);

            if(Pit.isFinished(player)){
                for (int i = 0; i < 3; i++) {
                    Bukkit.getScheduler().runTaskLater(PigStops.getPlugin(), () -> {
                        player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 0.5f, 1f);
                    },1);
                }
                Pit.finishPits(player);
            }
        } else {
            Pit.shuffleItems(player);
        }
    }
}
