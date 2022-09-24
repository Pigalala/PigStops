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

public class PitNormal {
    private final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    private final ItemStack paddle = new ItemStack(Material.WOODEN_SHOVEL);

    public PitNormal(Player player, PitType pitType){
        setItemMetas();
        PigStops.getPlugin().getPitWindow().createWindow(player, pitType, setContents());
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
}
