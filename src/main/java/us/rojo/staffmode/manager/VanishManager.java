package us.rojo.staffmode.manager;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import us.rojo.staffmode.Permissions;
import us.rojo.staffmode.StaffModePlugin;
import static us.rojo.staffmode.util.StringUtils.color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VanishManager {

    private StaffModePlugin plugin;

    @Getter
    private Set<UUID> vanishedPlayers = new LinkedHashSet<>();

    @Getter
    private Set<UUID> playersHidingStaff = new LinkedHashSet<>();

    public VanishManager(StaffModePlugin plugin) {
        this.plugin = plugin;
    }

    public void setVanished(Player player, boolean shouldVanish) {
        UUID playerUUID = player.getUniqueId();

        if (shouldVanish && !vanishedPlayers.contains(playerUUID)) {
            vanishedPlayers.add(playerUUID);
            player.spigot().setCollidesWithEntities(false);
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!players.hasPermission(Permissions.VANISH_SEE)) {
                    players.hidePlayer(player);
                }
            }
        } else if (!shouldVanish && vanishedPlayers.contains(playerUUID)) {
            vanishedPlayers.remove(playerUUID);
            player.spigot().setCollidesWithEntities(true);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.showPlayer(player);
            }
        }
    }

    public boolean toggle(Player player) {
        if (isVanished(player)) {
            plugin.getVanishManager().setVanished(player, false);
            return false;
        } else {
            plugin.getVanishManager().setVanished(player, true);
            return true;
        }
    }

    public void hideOtherStaff(Player player) {
        if (!playersHidingStaff.contains(player.getUniqueId())) {
            playersHidingStaff.add(player.getUniqueId());
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.equals(player)) {
                    continue;
                }

                if (players.hasPermission(Permissions.VANISH_SEE)) {
                    player.hidePlayer(players);
                }
            }
            player.sendMessage(color("&cAll staff members is now hidden for you."));
        }
    }

    public void showOtherStaff(Player player) {
        if (playersHidingStaff.contains(player.getUniqueId())) {
            playersHidingStaff.remove(player.getUniqueId());
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.equals(player)) {
                    continue;
                }

                if (players.hasPermission(Permissions.VANISH_SEE)) {
                    player.showPlayer(players);
                }
            }
            player.sendMessage(color("&aAll staff members is now unhidden for you."));
        }
    }

    public Boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }
}

