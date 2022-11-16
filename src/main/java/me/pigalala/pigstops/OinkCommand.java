package me.pigalala.pigstops;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import co.aikar.commands.contexts.ContextResolver;
import me.pigalala.pigstops.pit.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

@CommandAlias("pigstop|pit")
public class OinkCommand extends BaseCommand {

    @Default
    public static void practiseDefaultPit(Player player) {
        if(PigStops.defaultPitGame == null) {
            player.sendMessage("§cThere is no pit game available. Please contact a server admin.");
            return;
        }

        PigStops.pitPlayers.get(player).pit = new Pit(PigStops.pitPlayers.get(player), Pit.Type.FAKE);
    }

    @Subcommand("setgame")
    @CommandCompletion("@pits")
    @CommandPermission("pigstop.admin")
    public static void setNewGame(Player player, PitGame game) {
        Utils.setDefaultPitGame(game);
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

        Utils.setPitBlock(block);
        player.sendMessage("§aSuccessfully set pit block to " + block.toString().toLowerCase());
    }

    @Subcommand("debugmode")
    @CommandCompletion("pigstop.admin")
    public static void toggleDebugMode(Player player) {
        player.sendMessage("§aDebugMode has been " + (PitPlayer.of(player).toggleDebugMode() ? "enabled" : "disabled"));
    }

    @Subcommand("editor")
    @CommandPermission("pigstop.editor")
    public class Editor extends BaseCommand {
        @Subcommand("create")
        @CommandCompletion("name inventorySize")
        public static void createPitGame (Player player, String name, @Optional Integer invSize){
            int iSize;

            if (invSize == null) iSize = 27;
            else if (invSize % 9 == 0) {
                iSize = invSize;
            } else {
                player.sendMessage("§cInventory Size must be a multiple of 9");
                return;
            }

            if (isIllegalName(player, name)) return;

            new PitGame(OinkConfig.customPSPath + File.separator + name + ".pigstop", name, iSize);
            player.sendMessage("§aSuccessfully created game: " + name);
        }

        @Subcommand("delete")
        @CommandCompletion("@pits")
        public static void deletePit (Player player, PitGame game){
            game.delete();
            player.sendMessage("§aSuccessfully deleted " + game.name);
        }

        @Subcommand("design")
        @CommandCompletion("@pits")
        public static void designPit (Player player, PitGame game){
            PigStops.pitPlayers.get(player).newEditor(game);
        }

        @Subcommand("set")
        public class Set extends BaseCommand {

            @Subcommand("name")
            @CommandCompletion("@pits newname")
            public static void updatePitName(Player player, PitGame game, String newName) {
                if(isIllegalName(player, newName)) return;

                game.setName(newName);
                player.sendMessage("§aPitGame has been updated");
            }

            @Subcommand("inventorysize")
            @CommandCompletion("@pits 9|18|27|36|45|54")
            public static void setInventorySize(Player player, PitGame game, Integer size) {
                if(size > 54) {
                    player.sendMessage("§cInventory size must be lower than 54");
                    return;
                }

                if(size % 9 != 0) {
                    player.sendMessage("§cInventory size must be a multiple of 9");
                    return;
                }

                game.setInventorySize(size);
                player.sendMessage("§aSuccessfully change pit inventory size to " + size);
            }

            @Subcommand("background")
            @CommandCompletion("@pits @items")
            public static void setBackgroundItem(Player player, PitGame game, Material itemMat) {
                game.setBackgroundItem(new ItemStack(itemMat));
                player.sendMessage("§aSuccessfully change background item to " + itemMat.toString().toLowerCase());
            }
        }
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

    public static ContextResolver<Material, BukkitCommandExecutionContext> getMaterialContextResolver() {
        return c -> {
            try {
                return Material.valueOf(c.popFirstArg().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument();
            }
        };
    }

    public static ContextResolver<Sound, BukkitCommandExecutionContext> getSoundContextResolver() {
        return c -> {
            try {
                return Sound.valueOf(c.popFirstArg().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument();
            }
        };
    }
}
