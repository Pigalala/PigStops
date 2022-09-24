package me.pigalala.pitminigame.pit;

import me.makkuusen.timing.system.Database;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.pigalala.pitminigame.PitGame;
import me.pigalala.pitminigame.PitType;
import me.pigalala.pitminigame.PigStops;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PitWindow {

    private final HashMap<Player, Integer> itemsToClick = new HashMap<>();
    private final HashMap<Player, Boolean> hasStarted = new HashMap<>();
    private final HashMap<Player, PitType> pitTypes = new HashMap<>();
    private final HashMap<Player, Inventory> pitWindows = new HashMap<>();

    private void setHashMaps(Player player, PitType pitType){
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.NORMAL) itemsToClick.put(player, 2);
        if(PigStops.getPlugin().getDefaultPitGame() == PitGame.COOKIE) itemsToClick.put(player, 3);
        hasStarted.put(player, true);
        pitTypes.put(player, pitType);
        pitWindows.put(player, null);
    }

    public Boolean isFinished(Player player){
        return itemsToClick.get(player) <= 0;
    }

    public void reset(Player player){
        hasStarted.put(player, false);
        pitWindows.put(player, null);
    }

    public void createWindow(Player player, PitType pitType, ItemStack[] contents){
        if(hasStarted.get(player) != null) {
            if(hasStarted.get(player)) return;
        }

        setHashMaps(player, pitType);
        Inventory pitWindow = Bukkit.createInventory(player, 27, "§6§lPIG STOP!");

        pitWindow.setContents(contents);
        player.openInventory(pitWindow);
        pitWindows.put(player, pitWindow);
    }

    private void finishPits(Player player){
        player.closeInventory();
        hasStarted.put(player, false);
        pitWindows.put(player, null);

        if(pitTypes.get(player) != PitType.REAL) return;

        TPlayer p = Database.getPlayer(player.getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;
        Heat heat = driver.get().getHeat();

        if (driver.get().passPit()) {
            heat.updatePositions();
        }
    }

    private void shufflePlayer(Player player){
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pitWindows.get(player).getContents()).toList());
        Collections.shuffle(shuffled);

        pitWindows.get(player).setContents(shuffled.toArray(new ItemStack[0]));
        player.openInventory(pitWindows.get(player));
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }

    public void normalItemClick(Player player, ItemStack clickedItem){
        if(clickedItem.getType() == Material.WOODEN_SHOVEL){
            ItemMeta paddleMeta = clickedItem.getItemMeta();
            if(paddleMeta.hasEnchants()) return;
            paddleMeta.addEnchant(Enchantment.LUCK, 1, true);
            paddleMeta.setDisplayName("New Paddle");
            clickedItem.setItemMeta(paddleMeta);

            if(itemsToClick.get(player) == 2){
                player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.MASTER, 0.5f, 1f);
            }

            itemsToClick.put(player, itemsToClick.get(player) - 1);

            if(isFinished(player)){
                for (int i = 0; i < 3; i++) {
                    Bukkit.getScheduler().runTaskLater(PigStops.getPlugin(), () -> {
                        player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 0.5f, 1f);
                    },1);
                }
                finishPits(player);
            }
        }
        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            shufflePlayer(player);
        }
    }

    public void cookieItemClick(Player player, ItemStack clickedItem){
        if(clickedItem.getType() == Material.WHEAT){
            if((itemsToClick.get(player) != 3 && itemsToClick.get(player) != 1)) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
                return;
            }

            ItemMeta wheatMeta = clickedItem.getItemMeta();
            if(wheatMeta.hasEnchants()) return;
            wheatMeta.addEnchant(Enchantment.LUCK, 1, true);
            clickedItem.setItemMeta(wheatMeta);

            itemsToClick.put(player, itemsToClick.get(player) - 1);
            player.playSound(player, Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 0.5f, 1f);
        }
        if(clickedItem.getType() == Material.COCOA_BEANS) {
            if(itemsToClick.get(player) != 2) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
                return;
            }

            ItemMeta cocoaMeta = clickedItem.getItemMeta();
            if(cocoaMeta.hasEnchants()) return;
            cocoaMeta.addEnchant(Enchantment.LUCK, 1, true);
            clickedItem.setItemMeta(cocoaMeta);

            itemsToClick.put(player, itemsToClick.get(player) - 1);
            player.playSound(player, Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 0.5f, 1f);
        }

        if(isFinished(player)){
            player.playSound(player, Sound.BLOCK_FIRE_AMBIENT, SoundCategory.MASTER, 0.5f, 1f);
            finishPits(player);
        }

        if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
            shufflePlayer(player);
        }
    }
}