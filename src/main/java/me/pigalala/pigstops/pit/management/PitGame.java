package me.pigalala.pigstops.pit.management;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.pigalala.pigstops.OinkConfig;
import me.pigalala.pigstops.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.pigalala.pigstops.PigStops.defaultPitGame;
import static me.pigalala.pigstops.PigStops.pitGames;

public class PitGame {

    private File file;
    private YamlConfiguration pitFile;

    public String name;
    public int inventorySize;
    public ItemStack backgroundItem;
    public String modifications;
    public List<ItemStack> contents = new ArrayList<>();

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
        saveModifications();
    }

    public void setBackgroundItem(ItemStack backgroundItem) {
        pitFile.set("background", backgroundItem);
        saveModifications();
        this.backgroundItem = backgroundItem;
    }

    public void setInventorySize(Integer size) {
        pitFile.set("invsize", size);
        saveModifications();
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
        saveModifications();
    }

    public void setModification(char change, Modifications modification) {
        if(change == '+') {
            if(hasModification(modification)) return;
            pitFile.set("modifications", pitFile.getString("modifications").concat(modification.getId()));
            this.modifications = this.modifications.concat(modification.getId());
        } else if(change == '-') {
            pitFile.set("modifications", pitFile.getString("modifications").replace(modification.getId(), ""));
            this.modifications = this.modifications.replace(modification.getId(), "");
        } else throw new RuntimeException("Pigalala was stupid");

        saveModifications();
    }

    public boolean hasModification(Modifications modification) {
        return modifications.contains(modification.getId());
    }

    public void update() {
        if(!pitFile.isSet("modifications")) {
            pitFile.set("modifications", "ab");
        }

        saveModifications();
    }

    private void saveModifications() {
        try {
            pitFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void firstTimeSetup() {
        this.name = pitFile.getString("name");
        this.inventorySize = pitFile.getInt("invsize");
        this.backgroundItem = pitFile.getItemStack("background");
        this.modifications = pitFile.getString("modifications");

        for(int i = 0; i < 54; i++) {
            contents.add(pitFile.getItemStack("item" + i));
        }
    }

    public static PitGame of(File f) {
        for(PitGame pg : pitGames) {
            if(pg.file == f) return pg;
        }

        return null;
    }

    public static ContextResolver<PitGame, BukkitCommandExecutionContext> getPitGameContextResolver() {
        return c -> {
            for(PitGame pg : pitGames) {
                if(pg.name.equals(c.popFirstArg())) return pg;
            }
            throw new InvalidCommandArgument("§cThat pit game was not recognised.", false);
        };
    }
}
