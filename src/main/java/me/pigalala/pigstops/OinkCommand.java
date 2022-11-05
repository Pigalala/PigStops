package me.pigalala.pigstops;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.pigalala.pigstops.pit.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static me.pigalala.pigstops.PigStops.pitGames;

@CommandAlias("pigstop|pit")
public class OinkCommand extends BaseCommand {

    @Default
    public static void practiseDefaultPit(Player player) {
        if(PitManager.getDefaultPitGame() == null) {
            player.sendMessage("§cThere is no pit game available. Please contact a server admin.");
            return;
        }

        PitManager.getPitPlayer(player).pit = new Pit(PitManager.getPitPlayer(player), PitType.FAKE);
    }

    @Subcommand("setgame")
    @CommandCompletion("@pits")
    @CommandPermission("pigstop.admin")
    public static void setNewGame(Player player, String game) {
        if(!pitGames.containsKey(game)) {
            player.sendMessage("§cThis pitgame does not exist");
            return;
        }

        PitManager.setDefaultPitGame(pitGames.get(game));
        player.sendMessage("§aSuccessfully updated pitgame");
    }

    @Subcommand("setpitblock")
    @CommandCompletion("@blocks")
    @CommandPermission("pigstop.admin")
    public static void setPitBlock(Player player, String blocke) {
        Material block;
        try {
            block = Material.valueOf(blocke.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cThat is not a valid block");
            return;
        }

        PitManager.setPitBlock(block);
        player.sendMessage("§aSuccessfully set pit block to " + block.toString().toLowerCase());
    }

    @Subcommand("editor create")
    @CommandCompletion("name inventorySize")
    @CommandPermission("pigstop.editor")
    public static void createPitGame(Player player, String name, @Optional Integer invSize) {
        int iSize;

        if(invSize == null) iSize = 27;
        else if(invSize % 9 == 0) {
            iSize = invSize;
        } else {
            player.sendMessage("§cInventory Size must be a multiple of 9");
            return;
        }

        if(isIllegalName(player, name)) return;

        try {
            new PitGame(OinkConfig.customPSPath + File.separator + name + ".pigstop", name, iSize);
        } catch (IOException e) {
            player.sendMessage("§cAn error occurred when running this command");
            e.printStackTrace();
            return;
        }

        player.sendMessage("§aSuccessfully created game: " + name);
    }

    @Subcommand("editor delete")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits")
    public static void deletePit(Player player, String name) {
        if(!pitGames.containsKey(name)) {
            player.sendMessage("§cThis pitgame does not exist");
            return;
        }

        PitGame game = pitGames.get(name);

        game.delete();
        player.sendMessage("§aSuccessfully deleted " + game.name);
    }

    @Subcommand("editor design")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits")
    public static void designPit(Player player, String game) {
        if(!pitGames.containsKey(game)) {
            player.sendMessage("§cThis pitgame does not exist");
        }
        PitGame pitGame = pitGames.get(game);

        PitManager.getPitPlayer(player).isEditing = true;

        List<PitItem> contents = pitGame.contents;
        List<ItemStack> processedContents = new ArrayList<>();

        for(PitItem item : contents) {
            if(item.itemType == Integer.parseInt("000")) continue;

            ItemStack itemStack = item.toItemStack();
            ItemMeta e = itemStack.getItemMeta();
            e.setDisplayName(new ItemStack(Utils.getMaterialFromI(item.itemType)).getItemMeta().getDisplayName());
            itemStack.setItemMeta(e);
            processedContents.add(itemStack);
        }

        Inventory editInventory = Bukkit.createInventory(player, pitGame.inventorySize, Component.text("§6" + pitGame.name));
        editInventory.setContents(processedContents.toArray(new ItemStack[0]));

        player.openInventory(editInventory);
    }

    @Subcommand("editor set inventorysize")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits 9|18|27|36|45|54")
    public static void setInventorySize(Player player, String game, Integer size) {
        File f = new File(OinkConfig.customPSPath + File.separator + game + ".pigstop");

        if(!f.exists()) {
            player.sendMessage("§cThat game does not exist");
            return;
        }

        if(size > 54) {
            player.sendMessage("§cInventory size must be lower than 54");
            return;
        }

        if(size % 9 != 0) {
            player.sendMessage("§cInventory size must be a multiple of 9");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(f.toPath());
            List<String> contents = new ArrayList<>();

            for (int i = 4; i < lines.size(); i++) {
                contents.add(lines.get(i));
            }

            Utils.updateContents(f, lines.get(0), String.valueOf(size), lines.get(2), Integer.valueOf(lines.get(3)), contents);
        } catch (IOException e) {
            player.sendMessage("§cAn error occurred processing this command");
            e.printStackTrace();
            return;
        }

        player.sendMessage("§aSuccessfully change pit inventory size to " + size);
    }

    @Subcommand("editor set background")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits @items")
    public static void setBackgroundItem(Player player, String game, String item) {
        File f = new File(OinkConfig.customPSPath + File.separator + game + ".pigstop");

        if(!f.exists()) {
            player.sendMessage("§cThat game does not exist");
            return;
        }

        Material mat;
        try {
            mat = Material.valueOf(item.toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            player.sendMessage("§cThat item doesn't exist");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(f.toPath());
            List<String> contents = new ArrayList<>();

            for (int i = 4; i < lines.size(); i++) {
                contents.add(lines.get(i));
            }

            Utils.updateContents(f, lines.get(0), lines.get(1), lines.get(2), mat.ordinal(), contents);
        } catch (IOException e) {
            player.sendMessage("§cAn error occurred processing this command");
            e.printStackTrace();
            return;
        }

        player.sendMessage("§aSuccessfully change background item to " + item);
    }

    @Subcommand("editor set name")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits name")
    public static void setName(Player player, String game, String newName) {
        File f = new File(OinkConfig.customPSPath + File.separator + game + ".pigstop");
        boolean isDefault = false;

        if(!f.exists()) {
            player.sendMessage("§cThat game does not exist");
            return;
        }

        if(isIllegalName(player, newName)) return;

        if(PitManager.getDefaultPitGame().equals(pitGames.get(game))) isDefault = true;

        try {
            File newFile = Utils.renameFile(f, newName);

            pitGames.get(game).delete();
            PitGame gamee = new PitGame(newFile);

            List<String> lines = Utils.readFile(newFile);
            List<String> contents = new ArrayList<>();

            for (int i = 4; i < lines.size(); i++) {
                contents.add(lines.get(i));
            }

            Utils.updateContents(newFile, newName, lines.get(1), lines.get(2), Integer.valueOf(lines.get(3)), contents);

            if(isDefault) PitManager.setDefaultPitGame(gamee);
        } catch (IOException e) {
            player.sendMessage("§cAn error occurred processing this command");
            e.printStackTrace();
            return;
        }

        player.sendMessage("§aSuccessfully changed name to " + newName);
    }

    private static boolean isIllegalName(Player player, String name) {
        final int maxNameLength = 15;

        if(!name.matches("[a-zA-Z0-9]+")){
            player.sendMessage("§cThat name is not allowed");
            return true;
        }

        if(name.length() > maxNameLength) {
            player.sendMessage("§cThat name is too long (" + maxNameLength + ")");
            return true;
        }

        File f = new File(OinkConfig.customPSPath + File.separator + name + ".pigstop");
        if(f.exists()) {
            player.sendMessage("§cThis game already exists");
            return true;
        }

        return false;
    }
}
