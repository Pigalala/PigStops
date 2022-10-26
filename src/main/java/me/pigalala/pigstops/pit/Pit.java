package me.pigalala.pigstops.pit;

import me.makkuusen.timing.system.Database;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;

public class Pit {

    private final PitPlayer pp;
    private final ItemStack defaultBackground = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

    public Pit(PitPlayer pp, PitType pitType) throws IOException {
        this.pp = pp;
        setItemMetas();

        File pitGame = PigStops.getPlugin().getDefaultPitGame();
        List<String> lines = Files.readAllLines(pitGame.toPath());
        List<ItemStack> contentsLines = new ArrayList<>();
        for(int i = 3; i < 57; i++) {
            if(i > Integer.parseInt(lines.get(1)) + 2) break;
            if(lines.get(i).equals("null")) {
                contentsLines.add(defaultBackground);
                continue;
            }
            contentsLines.add(new ItemStack(Material.valueOf(lines.get(i))));
        }
        createWindow(pitType, contentsLines.toArray(new ItemStack[0]), lines.get(0), Integer.parseInt(lines.get(1)), Integer.parseInt(lines.get(2)));
    }

    private void setItemMetas() {
        ItemMeta backgroundMeta = defaultBackground.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        defaultBackground.setItemMeta(backgroundMeta);
    }

    public Boolean isFinished(){
        return pp.itemsToClick <= 0;
    }

    /** Creates a pigstop inventory and displays it to the player. Also includes all setup needed **/
    public void createWindow(PitType pitType, ItemStack[] contents, String windowName, Integer windowSize, Integer toClick){

        Inventory pitWindow = Bukkit.createInventory(pp.player.getPlayer(), windowSize, Component.text(PitManager.pitNameBase + windowName));

        pp.pitWindow = pitWindow;
        pp.startingTime = Instant.now();
        pp.pitType = pitType;
        pp.itemsToClick = toClick;

        pitWindow.setContents(contents);
        shuffleItems(false);
        pp.player.openInventory(pitWindow);
    }

    /** Finishes a player's PigStop. Includes closing inventory, displaying finish time, passing pits and resetting player for next time **/
    public void finishPit(){
        pp.reset();
        pp.player.closeInventory();

        String finalTime = formatAsTime(Duration.between(pp.startingTime, Instant.now()).toMillis());

        if(pp.pitType != PitType.REAL) {
            pp.player.sendMessage(Utils.getCustomMessage("&aYou finished in &d%TIME%&a.",
                    "%TIME%", finalTime));
            return;
        }

        TPlayer p = Database.getPlayer(pp.player.getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;
        Driver d = driver.get();
        Heat heat = driver.get().getHeat();

        if (!d.getCurrentLap().isPitted()) {
            d.setPits(d.getPits() + 1);
            d.getCurrentLap().setPitted(true);
            heat.updatePositions();

            Utils.broadcastMessage(Utils.getCustomMessage("&d%PLAYER% &ahas completed pigstop &d%PITS% &ain &d%TIME%&a.",
                    "%PLAYER%", d.getTPlayer().getName(),
                    "%PITS%", String.valueOf(d.getPits()),
                    "%TIME%", finalTime),
                    heat);
        }
    }

    public void onItemClicked(ItemStack clickedItem, Integer slot) {
        if(clickedItem.getType() != defaultBackground.getType()) {
            pp.itemsToClick -= 1;
            pp.pitWindow.setItem(slot, new ItemStack(Material.AIR));

            if(!isFinished()) {
                pp.playSound(Sound.BLOCK_BAMBOO_HIT);
            } else {
                pp.playSound(Sound.BLOCK_SMITHING_TABLE_USE);
                finishPit();
            }
        } else {
            shuffleItems(true);
        }
    }

    public void shuffleItems(Boolean playFailSound){
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pp.pitWindow.getContents()).toList());
        Collections.shuffle(shuffled);

        pp.pitWindow.setContents(shuffled.toArray(new ItemStack[0]));
        pp.player.openInventory(pp.pitWindow);
        if(playFailSound) pp.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }
}
