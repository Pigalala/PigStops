package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.PitPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class PitVariants {

    public static class STANDARD {
        private static final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        private static final ItemStack paddle = new ItemStack(Material.WOODEN_SHOVEL);

        public static final int windowSize = 27;
        public static final int itemsToClick = 2;

        public static ItemStack[] getContents(){
            setItemMetas();
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < windowSize - itemsToClick; i++) {
                items.add(background);
            }

            for (int i = 0; i < itemsToClick; i++) {
                try {
                    int rand = new Random().nextInt(0, 27);
                    items.add(rand, paddle);
                } catch (IndexOutOfBoundsException e) {
                    i--;
                }
            }

            return items.toArray(new ItemStack[0]);
        }

        private static void setItemMetas(){
            // BACKGROUND ITEM
            ItemMeta backgroundMeta = background.getItemMeta();
            backgroundMeta.setDisplayName(" ");
            background.setItemMeta(backgroundMeta);

            // PADDLES
            Damageable paddleDamage = (Damageable) paddle.getItemMeta();
            paddleDamage.setDamage(new Random().nextInt(30,59));
            paddleDamage.setDisplayName("Worn Paddle");
            paddleDamage.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            paddleDamage.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            paddle.setItemMeta(paddleDamage);
        }

        public static void onItemClick(PitPlayer pp, ItemStack clickedItem, Integer slot){
            Player player = pp.getPlayer();

            if(clickedItem.getType() == Material.WOODEN_SHOVEL){
                Damageable paddleMeta = (Damageable) clickedItem.getItemMeta();
                if(paddleMeta.hasEnchants()) return;
                paddleMeta.addEnchant(Enchantment.LUCK, 1, true);
                paddleMeta.setDisplayName("New Paddle");
                paddleMeta.setDamage(1);
                clickedItem.setItemMeta(paddleMeta);

                if(pp.getItemsToClick() == 2){
                    player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.MASTER, 0.5f, 1f);
                }

                pp.setItemsToClick(pp.getItemsToClick() - 1);

                if(pp.getAttachedPit().isFinished()){
                    player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
                    pp.getAttachedPit().finishPit();
                }
            }
            if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                pp.getAttachedPit().shuffleItems(true);
            }
        }
    }

    public static class COOKIE {
        private static final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        private static final ItemStack cookie = new ItemStack(Material.COOKIE);

        public static final int windowSize = 27;
        public static final int toClick = 10;

        public static ItemStack[] getContents(){
            setItemMetas();
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < windowSize - toClick; i++) {
                items.add(background);
            }
            for (int i = 0; i < toClick; i++) {
                try {
                    int rand = new Random().nextInt(0, 27);
                    items.add(rand, cookie);
                } catch (IndexOutOfBoundsException e) {
                    i--;
                }
            }

            return items.toArray(new ItemStack[0]);
        }

        private static void setItemMetas(){
            // BACKGROUND ITEM
            ItemMeta backgroundMeta = background.getItemMeta();
            backgroundMeta.setDisplayName(" ");
            background.setItemMeta(backgroundMeta);

            // COOKIE
            ItemMeta cookieMeta = cookie.getItemMeta();
            cookieMeta.setDisplayName("Cookie");
            cookieMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            cookieMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            cookie.setItemMeta(cookieMeta);
        }

        public static void onItemClick(PitPlayer pp, ItemStack clickedItem, Integer slot){
            Player player = pp.getPlayer();
            if(clickedItem.getType() == Material.COOKIE){
                ItemMeta cookieMeta = clickedItem.getItemMeta();
                if(cookieMeta.hasEnchants()) return;
                cookieMeta.addEnchant(Enchantment.LUCK, 1, true);
                clickedItem.setItemMeta(cookieMeta);

                pp.setItemsToClick(pp.getItemsToClick() - 1);
                player.playSound(player, Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 0.5f, 1f);
            }

            if(pp.getAttachedPit().isFinished()){
                player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
                pp.getAttachedPit().finishPit();
            }

            if(clickedItem.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                pp.getAttachedPit().shuffleItems(true);
            }
        }
    }

    public static class MARIANA {
        private static final ItemStack background = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        private static final ItemStack marianaHead = new ItemStack(Material.PLAYER_HEAD, 9);
        private static final ItemStack fakeHead = new ItemStack(Material.PLAYER_HEAD, 9);

        public static final Integer windowSize = 27;
        public static final Integer toClick = 8;

        public static ItemStack[] getContents(){
            setItemMetas();
            List<ItemStack> items = new ArrayList<>();
            List<Integer> usedSlots = new ArrayList<>();
            for (int i = 0; i < windowSize; i++) {
                items.add(background);
            }

            for (int i = 0; i < toClick - 1; i++) {
                int rand = new Random().nextInt(0, windowSize);
                if(usedSlots.contains(rand)) {
                    i--;
                }
                items.set(rand, marianaHead);
                usedSlots.add(rand);
            }

            for (int i = 0; i < 1; i++) {
                int rand = new Random().nextInt(0, windowSize);
                if(usedSlots.contains(rand)) {
                    i--;
                }
                items.set(rand, fakeHead);
                usedSlots.add(rand);
            }

            return items.toArray(new ItemStack[0]);
        }

        private static void setItemMetas(){
            // BACKGROUND ITEM
            ItemMeta backgroundMeta = background.getItemMeta();
            backgroundMeta.setDisplayName(" ");
            background.setItemMeta(backgroundMeta);

            // Mariana Head
            SkullMeta marianaMeta = (SkullMeta) marianaHead.getItemMeta();
            marianaMeta.setDisplayName("§dMariana52");
            marianaMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Mariana52"));
            marianaHead.setItemMeta(marianaMeta);

            // FAKE HEAD
            SkullMeta fakeMeta = (SkullMeta) fakeHead.getItemMeta();
            fakeMeta.setDisplayName("§dMariana51");
            fakeMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Mariana52"));
            fakeHead.setItemMeta(fakeMeta);
        }

        public static void onItemClick(PitPlayer pp, ItemStack clickedItem, Integer slot){
            Player player = pp.getPlayer();

            if(clickedItem.getType() != background.getType()){
                if(clickedItem.getItemMeta().getDisplayName().equals("§dMariana51")){
                    clickedItem.setAmount(clickedItem.getAmount() - 1);
                    if(clickedItem.getAmount() != 0) {
                        player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.MASTER, 1f, 1.5f);
                        return;
                    }
                }
                pp.getPitWindow().setItem(slot, new ItemStack(Material.AIR));
                pp.setItemsToClick(pp.getItemsToClick() - 1);
                player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_HIT, SoundCategory.MASTER, 1f, 1.5f);

                if(pp.getAttachedPit().isFinished()){
                    pp.getAttachedPit().finishPit();
                    player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
                }
            } else {
                pp.getAttachedPit().shuffleItems(true);
            }
        }
    }

    public static class ONFISHE {
        private static final ItemStack background = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        private static final ItemStack onFishe = new ItemStack(Material.PUFFERFISH_BUCKET);

        public static final Integer windowSize = 54;
        public static final Integer toClick = 10;

        public static ItemStack[] getContents(){
            setItemMetas();
            List<ItemStack> items = new ArrayList<>();

            for (int i = 0; i < windowSize; i++) {
                items.add(background);
            }

            int rand = new Random().nextInt(0, windowSize);
            items.set(rand, onFishe);

            return items.toArray(new ItemStack[0]);
        }

        private static void setItemMetas(){
            // BACKGROUND ITEM
            ItemMeta backgroundMeta = background.getItemMeta();
            backgroundMeta.setDisplayName(" ");
            background.setItemMeta(backgroundMeta);

            // Fishe
            ItemMeta fisheMeta = onFishe.getItemMeta();
            fisheMeta.displayName(Component.text("§bOnFishe").decorate(TextDecoration.ITALIC));
            onFishe.setItemMeta(fisheMeta);
        }

        public static void onItemClick(PitPlayer pp, ItemStack clickedItem, Integer slot){
            Player player = pp.getPlayer();

            if(clickedItem.getType() != background.getType()){
                pp.getAttachedPit().shuffleItems(false);
                pp.setItemsToClick(pp.getItemsToClick() - 1);
                player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_FISH, SoundCategory.MASTER, 1f, 1f);
                if(pp.getAttachedPit().isFinished()){
                    pp.getAttachedPit().finishPit();
                    player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.MASTER, 1f, 1f);
                }
            } else {
                pp.getAttachedPit().shuffleItems(true);
            }
        }
    }
}
