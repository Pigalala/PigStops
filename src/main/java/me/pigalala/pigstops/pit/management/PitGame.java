package me.pigalala.pigstops.pit.management;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.pigalala.pigstops.OinkConfig;
import me.pigalala.pigstops.Utils;
import me.pigalala.pigstops.pit.management.pitmodes.Pit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static me.pigalala.pigstops.PigStops.*;

public class PitGame {

    private File file;
    private YamlConfiguration pitFile;

    public String name;
    public int inventorySize;
    public ItemStack backgroundItem;
    public List<ItemStack> contents = new ArrayList<>();
    public Pit.PitMode pitMode;

    public PitGame(File f) {
        file = f;
        pitFile = YamlConfiguration.loadConfiguration(file);
        firstTimeSetup();
        pitGames.add(this);
    }

    public PitGame(String path, String name, Integer invSize) {
        Utils.createNewPitFile(path, name, invSize);
        new PitGame(new File(path));
    }

    public void delete() {
        if(file.exists()) file.delete();
        if(defaultPitGame == this) defaultPitGame = null;
        pitGames.remove(this);
    }

    public String getPath() {
        return file.getPath();
    }

    public void setContents(List<ItemStack> contents) {
        this.contents = contents;
        for(int i = 0; i < 54; i++) {
            if(contents.get(i).getType().equals(Material.AIR)) {
                pitFile.set("item" + i, new ItemStack(Material.AIR));
                continue;
            }
            pitFile.set("item" + i, contents.get(i));
        }
        save();
    }

    public void setBackgroundItem(ItemStack backgroundItem) {
        pitFile.set("background", backgroundItem);
        save();
        this.backgroundItem = backgroundItem;
    }

    public void setInventorySize(Integer size) {
        pitFile.set("invsize", size);
        save();
        this.inventorySize = size;
    }

    public void setName(String newName) {
        String oldName = this.name;
        boolean b = defaultPitGame == this;
        pitFile.set("name", newName);

        file.renameTo(new File(OinkConfig.customPSPath + File.separator + newName + ".pigstop"));
        new File(OinkConfig.customPSPath + File.separator + oldName + ".pigstop").delete();

        this.file = new File(OinkConfig.customPSPath + File.separator + newName + ".pigstop");
        this.pitFile = YamlConfiguration.loadConfiguration(file);

        pitGames.remove(oldName);
        this.name = newName;
        pitGames.add(this);

        if(b) Utils.setDefaultPitGame(this);
        save();
    }

    public void setPitMode(Pit.PitMode pitMode) {
        pitFile.set("pitmode", pitMode.toString());
        save();
        this.pitMode = pitMode;
    }

    private void save() {
        try {
            pitFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void firstTimeSetup() {
        try {
            validateFile();
            this.name = pitFile.getString("name");
            this.inventorySize = pitFile.getInt("invsize");
            this.backgroundItem = pitFile.getItemStack("background");
            this.pitMode = Pit.PitMode.of(pitFile.getString("pitmode"));

            for(int i = 0; i < 54; i++) {
                contents.add(pitFile.getItemStack("item" + i));
            }
        } catch (NullPointerException e) {
            getPlugin().getLogger().log(Level.SEVERE, "Error loading " + file.getName() + ", please send the above PigStop to Pigalala for maintenance, or delete it");
        }
    }

    private void validateFile() {
        if(!pitFile.isSet("name")) throw new NullPointerException();
        if(!pitFile.isSet("invsize")) pitFile.set("invsize", 27);
        if(!pitFile.isSet("background")) pitFile.set("background", new ItemStack(Material.BARRIER));
        if(!pitFile.isSet("pitmode")) pitFile.set("pitmode", Pit.PitMode.DEFAULT.toString());
    }

    public static PitGame of(String name) {
        for(PitGame pg : pitGames) {
            if(pg.name.equals(name)) return pg;
        }

        return null;
    }

    public static PitGame of(File f) {
        for(PitGame pg : pitGames) {
            if(pg.file == f) return pg;
        }

        return null;
    }

    public static ContextResolver<PitGame, BukkitCommandExecutionContext> getPitGameContextResolver() {
        return c -> {
            try {
                return PitGame.of(c.popFirstArg());
            } catch (Exception e) {
                throw new InvalidCommandArgument("§cThat pit game was not recognised.", false);
            }
        };
    }
}
