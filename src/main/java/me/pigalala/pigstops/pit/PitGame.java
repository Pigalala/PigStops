package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.pigalala.pigstops.PigStops.pitGames;

public class PitGame {

    private String path;

    public String name;
    public int inventorySize;
    public int itemsToClick;
    public ItemStack backgroundItem;
    public List<ItemStack> contents = new ArrayList<>();

    public PitGame(File f) {
        path = f.getPath();
        update();
        pitGames.put(name, this);
    }

    public PitGame(String path, String name, Integer invSize) {
        Utils.createNewPitFile(path, name, invSize);
        new PitGame(new File(path));
    }

    public void delete() {
        if(new File(path).exists()) {
            new File(path).delete();
        }
        pitGames.remove(name, this);
    }

    public String getPath() {
        return path;
    }

    public void update() {
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(new File(path));
        name = yamlConfig.getString("name");
        inventorySize = yamlConfig.getInt("invsize");
        itemsToClick = yamlConfig.getInt("itc");
        backgroundItem = yamlConfig.getItemStack("background");

        contents = new ArrayList<>();
        for(int i = 0; i < 54; i++) {
            contents.add(yamlConfig.getItemStack("item" + i));
        }

        try {
            yamlConfig.save(new File(path));
        } catch (IOException e) {
        }
    }
}
