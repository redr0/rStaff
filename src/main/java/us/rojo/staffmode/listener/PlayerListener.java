package us.rojo.staffmode.listener;

import java.util.UUID;
import us.rojo.staffmode.Permissions;
import us.rojo.staffmode.StaffModePlugin;
import us.rojo.staffmode.util.ItemStackUtil;
import static us.rojo.staffmode.util.StringUtils.color;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.InventoryHolder;

public class PlayerListener implements Listener {

    private StaffModePlugin plugin;

    public PlayerListener(StaffModePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(Permissions.MOD_FORCED)) {
            plugin.getModManager().enable(player);
        }

        if (!player.hasPermission(Permissions.VANISH_SEE)) {
            for (UUID playerUUID : plugin.getVanishManager().getVanishedPlayers()) {
                player.hidePlayer(Bukkit.getPlayer(playerUUID));
            }
        } else {
            for (UUID playerUUID : plugin.getVanishManager().getPlayersHidingStaff()) {
                Player players = Bukkit.getPlayer(playerUUID);
                if (players != null) {
                    players.hidePlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            plugin.getModManager().disable(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            plugin.getModManager().enable(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Inventory inv = null;
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.getItemInHand().getType().equals(Material.INK_SACK) && player.getItemInHand().hasItemMeta()) {
                    if (plugin.getVanishManager().isVanished(player)) {
                        player.setItemInHand(ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 10, color("&6Become Invisible"), color("&7Right click to toggle your vanish")));
                        plugin.getVanishManager().setVanished(player, false);
                    } else {
                        plugin.getVanishManager().setVanished(player, true);
                        player.setItemInHand(ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 8, color("&6Become Visible"), color("&7Right click to toggle your vanish")));
                    }
                    event.setCancelled(true);
                } else if (player.getItemInHand().getType().equals(Material.SKULL_ITEM) && player.getItemInHand().hasItemMeta()) {
                    plugin.getModManager().toggleOtherStaffVisible(player);
                    event.setCancelled(true);
                } else if (event.getPlayer().getItemInHand().getType().equals(Material.RECORD_12)) {
                    plugin.getModManager().teleportToRandom(player);
                    event.setCancelled(true);
                }
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof InventoryHolder) {
                if (event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
                    Inventory raw_inv = ((InventoryHolder) event.getClickedBlock().getState()).getInventory();
                    inv = plugin.getServer().createInventory(player, raw_inv.getSize());
                    inv.setContents(raw_inv.getContents());
                    player.openInventory(inv);
                    player.sendMessage(color("&aOpened a fake version of this chest, you cannot edit."));
                    event.setCancelled(true);
                } else {
                    player.openInventory(((InventoryHolder) event.getClickedBlock().getState()).getInventory());
                    event.setCancelled(true);
                }
            } else if (player.hasPermission("worldedit.wand")) {
                if (player.getItemInHand().getType().equals(Material.WOOD_AXE) || player.getItemInHand().getType().equals(Material.COMPASS)) {
                    return;
                }
            } else {
                if (player.getItemInHand().getType().equals(Material.COMPASS)) {
                    return;
                }
            }

            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            if (plugin.getModManager().getModdedUpPlayers().contains(attacker.getUniqueId())) {
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            if (plugin.getModManager().getModdedUpPlayers().contains(attacker.getUniqueId())) {
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof ItemFrame) {
            Player attacker = (Player) event.getDamager();
            if (plugin.getModManager().getModdedUpPlayers().contains(attacker.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            if (event.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
                event.setCancelled(true);
            }

            if ((player.getItemInHand().getType().equals(Material.BOOK) && event.getPlayer().getItemInHand().hasItemMeta() && event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(color("&6Inspector"))) && (event.getRightClicked() instanceof Player)) {
                Player clicked = (Player) event.getRightClicked();
                if (clicked.isOnline()) {
                    plugin.getModManager().inspectInventory(player, clicked, false);
                }
            } else if ((player.getItemInHand().getType().equals(Material.BLAZE_ROD) && event.getPlayer().getItemInHand().hasItemMeta() && event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(color("&6Freeze"))) && (event.getRightClicked() instanceof Player)) {
                Player clicked = (Player) event.getRightClicked();
                Bukkit.getServer().dispatchCommand(player, "freeze %player%".replace("%player%", clicked.getName()));
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();
        if (player.isOp()) {
            for (int i = 0; i < lines.length; ++i) {
                event.setLine(i, color(lines[i]));
            }
        }
    }
}
