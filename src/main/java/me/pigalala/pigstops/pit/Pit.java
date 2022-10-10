package me.pigalala.pigstops.pit;

import me.makkuusen.timing.system.Database;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.enums.PitType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;
import static me.pigalala.pigstops.pit.PitManager.hasPitPlayer;
import static me.pigalala.pigstops.pit.PitManager.pitNameBase;

public abstract class Pit {
    public static Boolean isFinished(Player player){
        if(hasPitPlayer(player)) return false;
        return PitManager.getPitPlayer(player).getItemsToClick() <= 0;
    }

    public static void reset(Player player){
        if(hasPitPlayer(player)) return;
        PitManager.getPitPlayer(player).setHasStarted(false);
    }

    /** Creates a pigstop inventory and displays it to the player. Also includes all setup needed **/
    public static void createWindow(Player player, PitType pitType, ItemStack[] contents, String windowName, Integer windowSize, Integer toClick){
        if(hasPitPlayer(player)) return;
        PitPlayer pp = PitManager.getPitPlayer(player);

        if(pp.hasStarted()) {
            return;
        }

        Inventory pitWindow = Bukkit.createInventory(player, windowSize, pitNameBase + windowName);

        pp.setPitWindow(pitWindow);
        pp.setHasStarted(true);
        pp.setStartingTime(Instant.now());
        pp.setPitType(pitType);
        pp.setItemsToClick(toClick);

        pitWindow.setContents(contents);
        player.openInventory(pitWindow);
    }

    /** Finishes a player's pigstop. Includes closing inventory, displaying finish time, passing pits and resetting player for next time **/
    public static void finishPits(Player player){
        if(hasPitPlayer(player)) return;
        PitPlayer pp = PitManager.getPitPlayer(player);
        pp.reset();
        player.closeInventory();

        String finalTime = formatAsTime(Duration.between(pp.getStartingTime(), Instant.now()).toMillis());

        if(pp.getPitType() != PitType.REAL) {
            player.sendMessage("§aYou finished in §d" + finalTime + "§a.");
            return;
        }

        TPlayer p = Database.getPlayer(player.getUniqueId());
        var driver = EventDatabase.getDriverFromRunningHeat(p.getUniqueId());
        if(!driver.isPresent()) return;
        Driver d = driver.get();
        Heat heat = driver.get().getHeat();

        if (!d.getCurrentLap().isPitted()) {
            d.setPits(d.getPits() + 1);
            d.getCurrentLap().setPitted(true);
            heat.updatePositions();

            heat.getParticipants().forEach(participant -> {
                if(participant.getTPlayer().getPlayer() == null) return;
                participant.getTPlayer().getPlayer().sendMessage("§d" + d.getTPlayer().getName() + "§a has completed pigstop §d" + d.getPits() + "§a in §d" + finalTime + "§a.");
            });
        }
    }

    public static void shuffleItems(Player player){
        if(hasPitPlayer(player)) {return;}
        PitPlayer pp = PitManager.getPitPlayer(player);
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pp.getPitWindow().getContents()).toList());
        Collections.shuffle(shuffled);

        pp.getPitWindow().setContents(shuffled.toArray(new ItemStack[0]));
        player.openInventory(pp.getPitWindow());
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }
}
