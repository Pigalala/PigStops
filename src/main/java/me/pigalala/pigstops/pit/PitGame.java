package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static me.pigalala.pigstops.PigStops.pitGames;

public class PitGame {

    private String path;

    public String name;
    public int inventorySize;
    public int itemsToClick;
    public int backgroundItem;
    public List<PitItem> contents = new ArrayList<>();

    public PitGame(File f) {
        try {
            List<String> lines = Files.readAllLines(f.toPath());
            name = lines.get(0);
            inventorySize = Integer.parseInt(lines.get(1));
            itemsToClick = Integer.parseInt(lines.get(2));
            backgroundItem = Integer.parseInt(lines.get(3));

            for(int i = 4; i < 58; i++) {
                String[] content = lines.get(i).split(" +");
                contents.add(new PitItem(Integer.parseInt(content[0]), content[1].equals("t")));
            }

            path = f.getPath();

            pitGames.put(name, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PitGame(String path, String name, Integer invSize) throws IOException {
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
}
