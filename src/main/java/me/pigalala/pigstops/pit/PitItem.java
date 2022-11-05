package me.pigalala.pigstops.pit;

import me.pigalala.pigstops.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PitItem {

    public final int itemType;
    public final boolean isEnchanted;

    public PitItem(int itemType, boolean isEnchanted) {
        this.itemType = itemType;
        this.isEnchanted = isEnchanted;
    }

    public String toFileLine() {
        return itemType + " " + (isEnchanted ? "t" : "f");
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Utils.getMaterialFromI(itemType), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a!");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if(isEnchanted) meta.addEnchant(Enchantment.LUCK, 1, true);
        item.setItemMeta(meta);

        return item;
    }
}
