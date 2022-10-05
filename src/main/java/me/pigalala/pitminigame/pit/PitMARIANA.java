package me.pigalala.pitminigame.pit;

import me.pigalala.pitminigame.enums.PitGame;
import me.pigalala.pitminigame.enums.PitType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PitMARIANA implements Pit {

    private final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

    private final Material[] flowers = {Material.PINK_TULIP, Material.RED_TULIP, Material.WHITE_TULIP, Material.AZURE_BLUET, Material.ORANGE_TULIP, Material.PEONY, Material.SUNFLOWER};
    private final String[] flowerNames = {"Funny Flower", "Flower", "XD Flower"};

    public PitMARIANA(Player player, PitType pitType){
        setItemMetas();
        Pit.createWindow(player, pitType, setContents(), PitGame.MARIANA.name(), 54, 52);
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            items.add(background);
        }

        for(int i = 0; i < 52; i++) {
            ItemStack flower = new ItemStack(flowers[new Random().nextInt(0, flowers.length)]);

            ItemMeta flowerMeta = flower.getItemMeta();
            flowerMeta.setDisplayName(flowerNames[new Random().nextInt(0, flowerNames.length)]);
            flowerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            flowerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            flower.setItemMeta(flowerMeta);

            int randPos = new Random().nextInt(0, 52);

            try {
                items.add(randPos, flower);
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

        // flower meta within setContents()
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer... slot){
        if(clickedItem.getType() != Material.LIGHT_BLUE_STAINED_GLASS_PANE){
            Inventory inv = Pit.getPitWindows().get(player);
            inv.setItem(slot[0], new ItemStack(Material.AIR));

            Pit.getItemsToClick().put(player, Pit.getItemsToClick().get(player) - 1);
            player.playSound(player, Sound.BLOCK_GRASS_BREAK, SoundCategory.MASTER, 0.5f, 1f);
        } else {
            Pit.shuffleItems(player);
        }

        if(Pit.isFinished(player)){
            player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 0.5f, 1f);
            Pit.finishPits(player);
        }
    }
}
