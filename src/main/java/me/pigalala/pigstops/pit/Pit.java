package me.pigalala.pigstops.pit;

import me.makkuusen.timing.system.Database;
import me.makkuusen.timing.system.TPlayer;
import me.makkuusen.timing.system.event.EventDatabase;
import me.makkuusen.timing.system.heat.Heat;
import me.makkuusen.timing.system.participant.Driver;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.PitPlayer;
import me.pigalala.pigstops.Utils;
import me.pigalala.pigstops.enums.PitGame;
import me.pigalala.pigstops.enums.PitType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static me.makkuusen.timing.system.ApiUtilities.formatAsTime;

public class Pit {

    private final PitPlayer pp;
    public boolean hasStarted;

    public Pit(PitPlayer pp, PitType pitType) {
        this.pp = pp;
        this.hasStarted = false;

        PitGame pitGame = PigStops.getPlugin().getDefaultPitGame();
        createWindow(pitType, PitManager.chooseContents(pitGame), pitGame.name(), PitManager.chooseSizes(pitGame)[0], PitManager.chooseSizes(pitGame)[1]);
    }

    public Boolean isFinished(){
        return pp.getItemsToClick() <= 0;
    }

    public void reset(){
        hasStarted = false;
    }

    /** Creates a pigstop inventory and displays it to the player. Also includes all setup needed **/
    public void createWindow(PitType pitType, ItemStack[] contents, String windowName, Integer windowSize, Integer toClick){
        if(hasStarted) {
            return;
        }

        Inventory pitWindow = Bukkit.createInventory(pp.getPlayer().getPlayer(), windowSize, Component.text(PitManager.pitNameBase + windowName));

        hasStarted = true;

        pp.setPitWindow(pitWindow);
        pp.setStartingTime(Instant.now());
        pp.setPitType(pitType);
        pp.setItemsToClick(toClick);

        pitWindow.setContents(contents);
        pp.getPlayer().openInventory(pitWindow);
    }

    /** Finishes a player's PigStop. Includes closing inventory, displaying finish time, passing pits and resetting player for next time **/
    public void finishPit(){
        hasStarted = false;
        pp.reset();
        pp.getPlayer().closeInventory();

        String finalTime = formatAsTime(Duration.between(pp.getStartingTime(), Instant.now()).toMillis());

        if(pp.getPitType() != PitType.REAL) {
            pp.getPlayer().sendMessage(Utils.getCustomMessage("&aYou finished in &d%TIME%&a.",
                    "%TIME%", finalTime));
            return;
        }

        TPlayer p = Database.getPlayer(pp.getPlayer().getUniqueId());
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

    public void shuffleItems(Boolean playFailSound){
        List<ItemStack> shuffled = new ArrayList<>(Arrays.stream(pp.getPitWindow().getContents()).toList());
        Collections.shuffle(shuffled);

        pp.getPitWindow().setContents(shuffled.toArray(new ItemStack[0]));
        pp.getPlayer().openInventory(pp.getPitWindow());
        if(playFailSound) pp.getPlayer().playSound(pp.getPlayer(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
    }
}