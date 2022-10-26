package me.pigalala.pigstops.pit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.pigalala.pigstops.ConfigManager;
import me.pigalala.pigstops.PigStops;
import me.pigalala.pigstops.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@CommandAlias("pigstop|pit")
public class CommandPit extends BaseCommand {

    @Default
    public static void practiseDefaultPit(Player player) throws IOException {
        if(!PigStops.getPlugin().getDefaultPitGame().exists()) {
            player.sendMessage("§cThere is no pit game available. Please contact a server admin.");
            return;
        }

        PitManager.getPitPlayer(player).pit = new Pit(PitManager.getPitPlayer(player), PitType.FAKE);
    }

    @Subcommand("setgame")
    @CommandCompletion("@pits")
    @CommandPermission("pigstop.admin")
    public static void setNewGame(Player player, String game) throws IOException {
        File f = new File(ConfigManager.customPSPath + File.separator + game + ".pigstop");
        if(!f.exists()) {
            player.sendMessage("§cThat pit game does not exist");
            return;
        }
        PigStops.getPlugin().setDefaultPitGame(f);
        player.sendMessage("§aSet new pit game to " + Files.readAllLines(f.toPath()).get(0));
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

        PigStops.getPlugin().setPitBlock(block);
        player.sendMessage("§aSuccessfully set pit block to " + block.toString().toLowerCase());
    }

    @Subcommand("editor create")
    @CommandCompletion("gameName inventorySize itemsToClick")
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
        File f = new File(ConfigManager.customPSPath + File.separator + name + ".pigstop");

        try {
            Utils.createNewPitFile(f.getPath(), name, iSize);
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
    public static void deletePit(Player player, String game) {
        File f = new File(ConfigManager.customPSPath + File.separator + game + ".pigstop");

        if(!f.exists()) {
            player.sendMessage("§cThat game does not exist");
            return;
        }

        f.delete();
        player.sendMessage("§a" + game + " has been deleted");
    }

    @Subcommand("editor design")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits")
    public static void designPit(Player player, String game) throws IOException {
        File f = new File(ConfigManager.customPSPath + File.separator + game + ".pigstop");

        if(!f.exists()) {
            player.sendMessage("§cThat pit game does not exist");
            return;
        }
        PitManager.getPitPlayer(player).isEditing = true;

        List<String> lines = Files.readAllLines(f.toPath());
        List<ItemStack> contents = new ArrayList<>();

        for (int i = 3; i < 57; i++) {
            if(i > Integer.parseInt(lines.get(1)) + 2) break;
            if(lines.get(i).equals("null")) continue;
            contents.add(new ItemStack(Material.valueOf(lines.get(i))));
        }

        Inventory editInventory = Bukkit.createInventory(player, Integer.parseInt(lines.get(1)), Component.text("§6" + lines.get(0)));
        editInventory.setContents(contents.toArray(new ItemStack[0]));

        player.openInventory(editInventory);
    }

    @Subcommand("editor set inventorysize")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits 9|18|27|36|45|54")
    public static void setInventorySize(Player player, String game, Integer size) {
        File f = new File(ConfigManager.customPSPath + File.separator + game + ".pigstop");

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
            FileWriter writer = new FileWriter(f);

            writer.write(lines.get(0) + "\n");
            writer.write(size + "\n");

            for(int i = 2; i < lines.size(); i++) {
                writer.write(lines.get(i) + "\n");
            }

            writer.close();
        } catch (IOException e) {
            player.sendMessage("§cAn error occurred processing this command");
            e.printStackTrace();
            return;
        }

        player.sendMessage("§aSuccessfully change pit inventory size to " + size);
    }

    @Subcommand("editor set name")
    @CommandPermission("pigstop.editor")
    @CommandCompletion("@pits name")
    public static void setName(Player player, String game, String newName) {
        File f = new File(ConfigManager.customPSPath + File.separator + game + ".pigstop");
        boolean isDefault = false;

        if(!f.exists()) {
            player.sendMessage("§cThat game does not exist");
            return;
        }

        if(isIllegalName(player, newName)) return;

        if(PigStops.getPlugin().getDefaultPitGame().equals(f)) isDefault = true;

        try {
            f.renameTo(new File(ConfigManager.customPSPath + File.separator + newName + ".pigstop"));
            f = new File(ConfigManager.customPSPath + File.separator + newName + ".pigstop");
            List<String> lines = Files.readAllLines(f.toPath());
            FileWriter writer = new FileWriter(f);

            writer.write(newName + "\n");

            for(int i = 1; i < lines.size(); i++) {
                writer.write(lines.get(i) + "\n");
            }

            writer.close();

            if(isDefault) PigStops.getPlugin().setDefaultPitGame(f);
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

        File f = new File(ConfigManager.customPSPath + File.separator + name + ".pigstop");
        if(f.exists()) {
            player.sendMessage("§cThis game already exists");
            return true;
        }

        return false;
    }
}
