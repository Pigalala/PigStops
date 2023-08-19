package me.pigalala.pigstops.pit.management.pitmodes;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.Utils;
import me.pigalala.pigstops.pit.management.Modifications;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PitModeDefault extends Pit {

    public PitModeDefault(PitPlayer pp, Type pitType) {
        super(pp, pitType);
    }

    @Override
    protected ItemStack[] registerContents() {
        List<ItemStack> contentsLines = new ArrayList<>();
        int itc = 0;
        for(ItemStack item : pitGame.contents) {
            if(contentsLines.size() >= pitGame.inventorySize) break;
            if(item.getType() == Material.AIR) {
                contentsLines.add(defaultBackground);
            } else {
                contentsLines.add(item);
                itc++;
            }
        }
        this.itemsToClick = itc;
        this.maxItemsToClick = itemsToClick;

        if(itc == 0) {
            // No items setup
            return new ItemStack[]{defaultBackground};
        }

        return contentsLines.toArray(new ItemStack[0]);
    }

    @Override
    protected void createWindow(ItemStack[] contents, String windowName, Integer windowSize) {
        Inventory pitWindow = Bukkit.createInventory(null, windowSize, Component.text(Utils.pitNameBase + windowName));

        this.pitWindow = pitWindow;
        this.startTime = Instant.now();

        pitWindow.setContents(contents);
        if(pitGame.hasModification(Modifications.RANDOMISE_ON_START)) shuffleItems(false);

        pp.getPlayer().openInventory(pitWindow);
        spectators.forEach(pitPlayer -> pitPlayer.getPlayer().openInventory(pitWindow));
    }

    @Override
    protected void onItemClicked(Inventory inventory, InventoryView inventoryView, ItemStack clickedItem, int clickedSlot) {
        if(clickedItem.getType() != this.defaultBackground.getType()) {
            itemsToClick -= 1;
            pitWindow.setItem(clickedSlot, new ItemStack(Material.AIR));

            if(!isFinished()) {
                pp.playSound(Sound.BLOCK_BAMBOO_HIT);
                spectators.forEach(ppp -> ppp.playSound(Sound.BLOCK_BAMBOO_HIT));
            } else {
                pp.playSound(Sound.BLOCK_SMITHING_TABLE_USE);
                spectators.forEach(ppp -> ppp.playSound(Sound.BLOCK_SMITHING_TABLE_USE));
                finishPit();
            }
        } else {
            if(pitGame.hasModification(Modifications.RANDOMISE_ON_FAIL)) shuffleItems(true);
        }
    }

}
