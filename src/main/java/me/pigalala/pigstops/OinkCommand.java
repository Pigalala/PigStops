package me.pigalala.pigstops;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.pigalala.pigstops.pit.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

        new PitGame(OinkConfig.customPSPath + File.separator + name + ".pigstop", name, iSize);
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
            return;
        }
        PitManager.getPitPlayer(player).newEditor(pitGames.get(game));
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

        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);
        yamlConfig.set("invsize", size);

        try {
            yamlConfig.save(f);
            pitGames.get(game).update();
        } catch (IOException e) {
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

        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(f);

        yamlConfig.set("background", new ItemStack(mat));

        try {
            yamlConfig.save(f);
            pitGames.get(game).update();
        } catch (IOException e) {
        }
        player.sendMessage("§aSuccessfully change background item to " + item);
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
