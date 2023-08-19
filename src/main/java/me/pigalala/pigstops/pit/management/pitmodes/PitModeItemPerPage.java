package me.pigalala.pigstops.pit.management.pitmodes;

import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.Utils;
import me.pigalala.pigstops.pit.management.Modifications;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PitModeItemPerPage extends Pit {

    private List<ItemStack[]> pages;
    private int page = 0;
    private String pitInvName;

    public PitModeItemPerPage(PitPlayer pp, Type pitType) {
        super(pp, pitType);
    }

    @Override
    protected ItemStack[] registerContents() {
        pages = new ArrayList<>();
        int itc = 0;
        for(ItemStack item : pitGame.contents) {
            List<ItemStack> contentsLines = new ArrayList<>();
            if(item.getType() == Material.AIR) continue;
            contentsLines.add(item);
            itc++;
            for (int j = 0; j < pitGame.inventorySize - 1; j++) {
                contentsLines.add(defaultBackground);
            }

            Collections.shuffle(contentsLines);
            pages.add(contentsLines.toArray(new ItemStack[]{}));
        }
        this.itemsToClick = itc;
        this.maxItemsToClick = itemsToClick;

        return null;
    }

    @Override
    protected void createWindow(ItemStack[] contents, String windowName, Integer windowSize) {
        pitInvName = Utils.pitNameBase + windowName;
        Inventory pitWindow = Bukkit.createInventory(null, windowSize, pitInvName + " §7§l- §d1");

        this.pitWindow = pitWindow;
        this.startTime = Instant.now();

        pitWindow.setContents(pages.get(0));
        if(pitGame.hasModification(Modifications.RANDOMISE_ON_START)) shuffleItems(false);

        pp.getPlayer().openInventory(pitWindow);
        spectators.forEach(pitPlayer -> pitPlayer.getPlayer().openInventory(pitWindow));
    }

    @Override
    protected void onItemClicked(Inventory inventory, InventoryView inventoryView, ItemStack clickedItem, int clickedSlot) {
        if(clickedItem.getType() != this.defaultBackground.getType()) {
            page++;

            if(pages.size() <= page) {
                pp.playSound(Sound.BLOCK_SMITHING_TABLE_USE);
                spectators.forEach(pp -> pp.playSound(Sound.BLOCK_SMITHING_TABLE_USE));
                finishPit();
                return;
            }

            inventory.setContents(pages.get(page));
            inventoryView.setTitle(pitInvName + " §7§l- §d" + (page + 1));

            pp.playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
            spectators.forEach(pp -> pp.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f));
        } else {
            shuffleItems(true);
        }
    }

}
