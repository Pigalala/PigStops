package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.pigalala.pigstops.pit.PitManager.hasPitPlayer;

public class PitMARIANA extends Pit {
    private static final ItemStack background = new ItemStack(Material.RED_STAINED_GLASS_PANE);
    private static final ItemStack marianaHead = new ItemStack(Material.PLAYER_HEAD, 9);
    private static final ItemStack fakeHead = new ItemStack(Material.PLAYER_HEAD, 9);

    private final Integer windowSize = 27;
    private final Integer toClick = 8;

    public PitMARIANA(Player player, PitType pitType){
        setItemMetas();
        Pit.createWindow(player, pitType, setContents(), PitGame.MARIANA.name(), windowSize, toClick);
    }

    private ItemStack[] setContents(){
        List<ItemStack> items = new ArrayList<>();
        List<Integer> usedSlots = new ArrayList<>();
        for (int i = 0; i < windowSize; i++) {
            items.add(background);
        }

        for (int i = 0; i < toClick - 1; i++) {
            int rand = new Random().nextInt(0, windowSize);
            if(usedSlots.contains(rand)) {
                i--;
            }
            items.set(rand, marianaHead);
            usedSlots.add(rand);
        }

        for (int i = 0; i < 1; i++) {
            int rand = new Random().nextInt(0, windowSize);
            if(usedSlots.contains(rand)) {
                i--;
            }
            items.set(rand, fakeHead);
            usedSlots.add(rand);
        }

        return items.toArray(new ItemStack[0]);
    }

    private void setItemMetas(){
        // BACKGROUND ITEM
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // Mariana Head
        SkullMeta marianaMeta = (SkullMeta) marianaHead.getItemMeta();
        marianaMeta.setDisplayName("§dMariana52");
        marianaMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Mariana52"));
        marianaHead.setItemMeta(marianaMeta);

        // FAKE HEAD
        SkullMeta fakeMeta = (SkullMeta) fakeHead.getItemMeta();
        fakeMeta.setDisplayName("§dMariana51");
        fakeMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Mariana52"));
        fakeHead.setItemMeta(fakeMeta);
    }

    public static void onItemClick(Player player, ItemStack clickedItem, Integer slot){
        if(hasPitPlayer(player)) return;
        PitPlayer pp = PitManager.getPitPlayer(player);

        if(clickedItem.getType() != background.getType()){
            if(clickedItem.getItemMeta().getDisplayName().equals("§dMariana51")){
                clickedItem.setAmount(clickedItem.getAmount() - 1);
                if(clickedItem.getAmount() != 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.MASTER, 1f, 1.5f);
                    return;
                }
            }
            pp.getPitWindow().setItem(slot, new ItemStack(Material.AIR));
            pp.setItemsToClick(pp.getItemsToClick() - 1);
            player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 1f, 1.5f);

            if(Pit.isFinished(player)){
                Pit.finishPits(player);
                player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
            }
        } else {
            Pit.shuffleItems(player);
        }
    }
}
