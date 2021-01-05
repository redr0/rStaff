package us.rojo.staffmode.util;

import java.util.List;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StringUtils {

    public static String color(String s) {
        return s.replaceAll("&", "§");
    }

    public static ItemStack setItemTitle(ItemStack item, String title) {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(title);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(title);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack setItemLore(ItemStack item, List<String> lore) {
        Preconditions.checkNotNull(item, lore);
        Preconditions.checkNotNull(lore);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }
}
