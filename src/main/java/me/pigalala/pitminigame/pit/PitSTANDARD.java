package me.pigalala.pitminigame.pit;

import me.pigalala.pitminigame.PigStops;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import org.bukkit.Bukkit;
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

public class PitSTANDARD {
    private final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    private final ItemStack paddle = new ItemStack(Material.WOODEN_SHOVEL);

    public PitSTANDARD(Player player, PitType pitType){
        setItemMetas();
        PigStops.getPitWindow().createWindow(player, pitType, setContents(), PitGame.STANDARD.name(), 27);
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            items.add(background);
        }

        int rand1 = (int) Math.floor(Math.random() * 26);
        int rand2 = (int) Math.floor(Math.random() * 26);
        items.add(rand1, paddle);
        items.add(rand2, paddle);

        return items.toArray(new ItemStack[0]);
    }

    private void setItemMetas(){
        // BACKGROUND ITEM
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // PADDLES
        ItemMeta paddleMeta = paddle.getItemMeta();
        paddleMeta.setDisplayName("Worn Paddle");
        paddleMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paddleMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        paddle.setItemMeta(paddleMeta);
    }

    public static void onItemClick(Player player, ItemStack clickedItem){
        if(clickedItem.getType() == Material.WOODEN_SHOVEL){
            ItemMeta paddleMeta = clickedItem.getItemMeta();
            if(paddleMeta.hasEnchants()) return;
            paddleMeta.addEnchant(Enchantment.LUCK, 1, true);
            paddleMeta.setDisplayName("New Paddle");
            clickedItem.setItemMeta(paddleMeta);

            if(PigStops.getPitWindow().itemsToClick.get(player) == 2){
                player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.MASTER, 0.5f, 1f);
            }

            PigStops.getPitWindow().itemsToClick.put(player, PigStops.getPitWindow().itemsToClick.get(player) - 1);

            if(PigStops.getPitWindow().isFinished(player)){
                for (int i = 0; i < 3; i++) {
                    Bukkit.getScheduler().runTaskLater(PigStops.getPlugin(), () -> {
                        player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 0.5f, 1f);
                    },1);
                }
                PigStops.getPitWindow().finishPits(player);
            }
        }
        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            PigStops.getPitWindow().shufflePlayer(player);
        }
    }
}
