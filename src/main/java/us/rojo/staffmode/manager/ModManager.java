package us.rojo.staffmode.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import us.rojo.staffmode.StaffModePlugin;
import us.rojo.staffmode.task.ExaminationInventoryTask;
import us.rojo.staffmode.util.ItemStackUtil;
import us.rojo.staffmode.util.StringUtils;
import static us.rojo.staffmode.util.StringUtils.color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.enchantments.Enchantment;

public class ModManager {

    private StaffModePlugin plugin;

    @Getter
    private List<UUID> moddedUpPlayers = new ArrayList<>();

    @Getter
    private Map<UUID, ItemStack[]> playerInventories = new HashMap<>();

    @Getter
    private Map<UUID, ItemStack[]> playerArmor = new HashMap<>();

    public ModManager(StaffModePlugin plugin) {
        this.plugin = plugin;
    }

    private ItemStack createSpacerItem(boolean online) {
        if (online) {
            return StringUtils.setItemTitle(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14), "Empty");
        } else {
            return StringUtils.setItemTitle(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "Player is offline.");
        }
    }

    private List<ItemStack> getInfoItems(Player player) {
        boolean online = player.isOnline();
        List<ItemStack> infoItems = new ArrayList<>();
        ItemStack item;
        infoItems.add(player.getItemInHand().clone());
        infoItems.add(createSpacerItem(online));
        infoItems.add(createSpacerItem(online));
        infoItems.add(createSpacerItem(online));
        infoItems.add(createSpacerItem(online));

        // Health
        item = new ItemStack(Material.SPECKLED_MELON, (int) player.getHealth());
        StringUtils.setItemTitle(item, ChatColor.GOLD + String.format("Heatlh: %.1f", player.getHealth() / 2));
        infoItems.add(item);

        // Hunger
        item = new ItemStack(Material.COOKED_BEEF, player.getFoodLevel());
        StringUtils.setItemTitle(item, ChatColor.GOLD + String.format("Food: %d", player.getFoodLevel() / 2));
        infoItems.add(item);

        // Saturation
        item = new ItemStack(Material.ROTTEN_FLESH, (int) player.getSaturation());
        StringUtils.setItemTitle(item, ChatColor.GOLD + String.format("Saturation: %.1f", player.getSaturation()));
        infoItems.add(item);

        //Horse Health
        if (player.isInsideVehicle() && player.getVehicle() instanceof Horse) {
            Horse horse = (Horse) player.getVehicle();
            item = new ItemStack(Material.DIAMOND_BARDING, (int) horse.getHealth());
            StringUtils.setItemTitle(item, ChatColor.GOLD + "Horse Health");
        } else {
            item = new ItemStack(Material.DIAMOND_BARDING);
            StringUtils.setItemTitle(item, ChatColor.GOLD + "Not Riding a Horse");
        }
        infoItems.add(item);

        infoItems.add(createSpacerItem(online));

        // Effects
        List<String> potionEffects = new ArrayList<>();
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            potionEffects.add(ChatColor.GRAY + String.format("%s %s for %s", potionEffect.getType().getName(), potionEffect.getAmplifier() + 1, (potionEffect.getDuration() > 10000 ? "**:**" : String.format("%02d:%02d", (potionEffect.getDuration() / 20) / 60, (potionEffect.getDuration() / 20) % 60))));
        }

        item = new ItemStack(Material.BREWING_STAND_ITEM);
        if (potionEffects.isEmpty()) {
            StringUtils.setItemTitle(item, ChatColor.GOLD + "No Potion Effects");
        } else {
            StringUtils.setItemTitle(item, ChatColor.GOLD + "Potion Effects");
            StringUtils.setItemLore(item, potionEffects);
        }
        infoItems.add(item);

        // Experience
        if (player.getLevel() >= 1) {
            item = new ItemStack(Material.EXP_BOTTLE, player.getLevel());
            StringUtils.setItemTitle(item, ChatColor.GOLD + "Experience: " + player.getLevel() + (player.getLevel() == 1 ? " level" : " levels"));
        } else {
            item = new ItemStack(Material.GLASS_BOTTLE);
            StringUtils.setItemTitle(item, ChatColor.GOLD + "No Experience Levels");
        }
        infoItems.add(item);

        // Location
        List<String> coords = new ArrayList<>();

        coords.add(ChatColor.GRAY + "World: " + player.getWorld().getName());
        coords.add(ChatColor.GRAY + "X: " + player.getLocation().getBlockX() + ", Y: " + player.getLocation().getBlockY() + ", Z: " + player.getLocation().getBlockZ());
        item = new ItemStack(Material.COMPASS);
        StringUtils.setItemTitle(item, ChatColor.GOLD + "Location");
        StringUtils.setItemLore(item, coords);
        infoItems.add(item);

        return infoItems;
    }

    public void enable(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (!moddedUpPlayers.contains(playerUUID)) {
            moddedUpPlayers.add(playerUUID);

            player.setGameMode(GameMode.CREATIVE);

            plugin.getVanishManager().setVanished(player, true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true));
            playerInventories.put(player.getUniqueId(), player.getInventory().getContents());
            playerArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            ItemStack pants = new ItemStack(Material.CHAINMAIL_LEGGINGS);
            pants.addEnchantment(Enchantment.DURABILITY, 1);
            player.getInventory().setLeggings(pants);

            player.getInventory().addItem(ItemStackUtil.createItem(Material.COMPASS, color("&6Teleporter"), color("&7Right click block: Move through"), color("&7Left click: Move to block in line of sight")));
            player.getInventory().addItem(ItemStackUtil.createItem(Material.BOOK, color("&6Inspector"), color("&7Right click player to inspect inventory and vitals")));
            player.getInventory().addItem(ItemStackUtil.createItem(Material.WOOD_AXE, color("&6WorldEdit Wand"), color("&7(Requieres WorldEdit perms, sold separately)")));

            player.getInventory().addItem(ItemStackUtil.createItem(Material.BLAZE_ROD, color("&6Freeze")));
            player.getInventory().setItem(6, ItemStackUtil.createItem(Material.SKULL_ITEM, 1, (short) 3, color("&6Hide Staff"), color("&7Right click to hide all staff online")));
            player.getInventory().setItem(7, ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 8, color("&6Become Visible"), color("&7Right click to toggle your vanish")));
            player.getInventory().setItem(8, ItemStackUtil.createItem(Material.RECORD_12, color("&6Random TP"), color("&7Right click to teleport to a random player")));
            player.updateInventory();
        }
    }

    public void disable(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (moddedUpPlayers.contains(playerUUID)) {
            moddedUpPlayers.remove(playerUUID);

            player.setGameMode(GameMode.SURVIVAL);

            ItemStack[] inventory = getSavedInventory(playerUUID);
            ItemStack[] armor = getSavedArmor(playerUUID);

            playerInventories.remove(playerUUID);
            playerArmor.remove(playerUUID);
            plugin.getVanishManager().setVanished(player, false);

            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.getInventory().setContents(inventory);
            player.getInventory().setArmorContents(armor);
            player.updateInventory();
        }
    }

    public boolean toggle(Player player) {
        if (moddedUpPlayers.contains(player.getUniqueId())) {
            disable(player);
            return false;
        } else {
            enable(player);
            return true;
        }
    }

    public void teleportToRandom(Player player) {
        Random random = new Random();
        List<Player> players = (List<Player>) getOnlinePlayers();
        Player randomPlayer = players.get(random.nextInt(players.size()));
        if (randomPlayer.equals(player)) {
            randomPlayer = players.get(random.nextInt(players.size()));
        }
        player.teleport(randomPlayer);
        player.sendMessage(ChatColor.BLUE + "Teleported to: " + randomPlayer.getDisplayName());
    }

    public void toggleOtherStaffVisible(Player player) {
        if (moddedUpPlayers.contains(player.getUniqueId())) {
            if (plugin.getVanishManager().getPlayersHidingStaff().contains(player.getUniqueId())) {
                plugin.getVanishManager().showOtherStaff(player);
            } else {
                plugin.getVanishManager().hideOtherStaff(player);
            }
        }
    }

    public void inspectInventory(Player viewer, Player viewed, boolean offline) {
        viewer.openInventory(new ExaminationInventoryTask(plugin, viewer, viewed, offline).getInventory());
    }

    public ItemStack getInfoItem(Player player, int itemIndex) {
        List<ItemStack> infoItems = getInfoItems(player);
        int infoItemIndex = itemIndex - 4;

        if ((infoItemIndex >= 0) && (infoItems.size() > infoItemIndex)) {
            return infoItems.get(infoItemIndex);
        } else {
            return new ItemStack(Material.AIR, 1);
        }
    }

    public Collection<Player> getOnlinePlayers() {
        Collection<Player> list = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player);
        }

        return list;
    }

    public ItemStack[] getSavedInventory(UUID playerUUID) {
        if (playerInventories.containsKey(playerUUID)) {
            return playerInventories.get(playerUUID);
        }

        return null;
    }

    public ItemStack[] getSavedArmor(UUID playerUUID) {
        if (playerArmor.containsKey(playerUUID)) {
            return playerArmor.get(playerUUID);
        }

        return null;
    }
}
