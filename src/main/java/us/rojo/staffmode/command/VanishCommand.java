package us.rojo.staffmode.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import us.rojo.staffmode.Permissions;
import us.rojo.staffmode.StaffModePlugin;
import us.rojo.staffmode.util.ItemStackUtil;
import static us.rojo.staffmode.util.StringUtils.color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.rojo.staffmode.StaffModePlugin;

public class VanishCommand {

    private StaffModePlugin plugin;

    public VanishCommand(StaffModePlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = {"v", "vanish"}, desc = "Hide yourself from other players", min = 0, max = 1)
    @CommandPermissions(Permissions.VANISH_COMMAND)
    public void vanishCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        if (args.argsLength() == 0) {
            if (plugin.getVanishManager().toggle(player)) {
                player.sendMessage(color("&eYour vanish has been: &aenabled!"));
                if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
                    player.getInventory().setItem(7, ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 8, color("&6Become Visible"), color("&7Right click to toggle your vanish")));
                }
            } else {
                player.sendMessage(color("&eYour vanish has been: &4disabled!"));
                if (plugin.getModManager().getModdedUpPlayers().contains(player.getUniqueId())) {
                    player.getInventory().setItem(7, ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 10, color("&6Become Invisible"), color("&7Right click to toggle your vanish")));
                }
            }
        } else if (player.hasPermission(Permissions.VANISH_COMMAND_OTHERS)) {
            Player target = CommandValidation.targetPlayer(args.getString(0));
            if (plugin.getVanishManager().toggle(target)) {
                player.sendMessage(color("&c%target%&e's vanish: &aenabled!").replace("%target%", target.getDisplayName()));
                target.sendMessage(color("&eYour vanish has been: &aenabled!"));
                if (plugin.getModManager().getModdedUpPlayers().contains(target.getUniqueId())) {
                    target.getInventory().setItem(7, ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 8, color("&6Become Visible"), color("&7Right click to toggle your vanish")));
                }
            } else {
                player.sendMessage(color("&c%target%&e's vanish: &4disabled!").replace("%target%", target.getDisplayName()));
                target.sendMessage(color("&eYour vanish has been: &4disabled!"));
                if (plugin.getModManager().getModdedUpPlayers().contains(target.getUniqueId())) {
                    target.getInventory().setItem(7, ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 10, color("&6Become Invisible"), color("&7Right click to toggle your vanish")));
                }
            }
        } else {
            player.sendMessage(color("&cNo permission."));
        }
    }
}
