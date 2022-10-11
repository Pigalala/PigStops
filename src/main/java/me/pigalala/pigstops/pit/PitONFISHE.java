package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.pigalala.pigstops.pit.PitManager.hasPitPlayer;

public class PitONFISHE extends Pit {
    private static final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
    private static final ItemStack onFishe = new ItemStack(Material.PUFFERFISH);

    private static final Integer windowSize = 54;
    private static final Integer toClick = 10;

    public PitONFISHE(Player player, PitType pitType) {
        if(PitManager.hasPitPlayer(player)) return;
        setItemMetas();
        Pit.createWindow(player, pitType, setContents(), PitGame.ONFISHE.name(), windowSize, toClick);
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < windowSize; i++) {
            items.add(background);
        }

        int rand = new Random().nextInt(0, windowSize);
        items.set(rand, onFishe);

        return items.toArray(new ItemStack[0]);
    }

    private void setItemMetas(){
        // BACKGROUND ITEM
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // Fishe
        ItemMeta fisheMeta = onFishe.getItemMeta();
        fisheMeta.displayName(Component.text("§bOnFishe").decorate(TextDecoration.ITALIC));
        onFishe.setItemMeta(fisheMeta);
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer slot){
        if(hasPitPlayer(player)) return;
        PitPlayer pp = PitManager.getPitPlayer(player);

        if(clickedItem.getType() != background.getType()){
            Pit.shuffleItems(player, false);
            pp.setItemsToClick(pp.getItemsToClick() - 1);
            player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_FISH, SoundCategory.MASTER, 1f, 1f);
            if(Pit.isFinished(player)){
                Pit.finishPits(player);
                player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
            }
        } else {
            Pit.shuffleItems(player, true);
        }
    }
}