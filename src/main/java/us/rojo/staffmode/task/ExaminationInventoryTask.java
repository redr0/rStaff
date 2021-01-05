package us.rojo.staffmode.task;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.rojo.staffmode.StaffModePlugin;

public class ExaminationInventoryTask implements InventoryHolder {

    private StaffModePlugin plugin;

    private Inventory inventory;

    private int inventorySize;

    private Player owner;

    private Player viewer;

    private boolean offlineInv;

    private BukkitTask updateTask;

    public ExaminationInventoryTask(StaffModePlugin plugin, Player viewer, Player owner, boolean offline) {
        this.plugin = plugin;
        this.inventorySize = 54;
        this.owner = owner;
        this.viewer = viewer;
        String title = getInventoryName();
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        this.inventory = plugin.getServer().createInventory(this, inventorySize, title);
        this.offlineInv = offline;
        this.updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (inventory.getViewers().isEmpty()) {
                    cancel();
                    return;
                }
                update();
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    public void update() {
        for (int i = 0; i < inventorySize; i++) {
            inventory.setItem(i, getItem(i));
        }
    }

    public String getInventoryName() {
        return ChatColor.DARK_GRAY + owner.getName();
    }

    public ItemStack getItem(int itemIndex) {
        if (!owner.isOnline() && !offlineInv) {
            viewer.closeInventory();
            return null;
        }

        if (owner.isOnline() && offlineInv) {
            viewer.closeInventory();
            return null;
        }

        if (itemIndex >= owner.getInventory().getContents().length) {
            // Return custom item
            return getCustomItem(itemIndex - owner.getInventory().getContents().length);
        } else {
            if (owner.getInventory().getContents()[itemIndex] != null) {
                return owner.getInventory().getContents()[itemIndex].clone();
            } else {
                return new ItemStack(Material.AIR);
            }
        }
    }

    private ItemStack getCustomItem(int itemIndex) {
        if (itemIndex < 4) {
            return owner.getInventory().getArmorContents()[3 - itemIndex].clone();
        } else {
            return plugin.getModManager().getInfoItem(owner, itemIndex);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
