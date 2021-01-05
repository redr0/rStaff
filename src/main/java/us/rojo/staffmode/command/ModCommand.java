package us.rojo.staffmode.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import us.rojo.staffmode.Permissions;
import us.rojo.staffmode.StaffModePlugin;
import static us.rojo.staffmode.util.StringUtils.color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModCommand {

    private StaffModePlugin plugin;

    public ModCommand(StaffModePlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = {"mod", "helper", "h", "mm", "v", "dev"}, desc = "Toggles moderator mode on/off.", min = 0, max = 1)
    @CommandPermissions(Permissions.MOD_COMMAND)
    public void modCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        if (args.argsLength() == 0) {
            if (plugin.getModManager().toggle(player)) {
                player.sendMessage(color("&eYour staff mode has been: &aenabled!"));
            } else {
                player.sendMessage(color("&eYour staff mode has been: &4disabled!"));
            }
        } else {
            if (player.hasPermission(Permissions.MOD_COMMAND_OTHERS)) {
                Player target = CommandValidation.targetPlayer(args.getString(0));
                if (plugin.getModManager().toggle(target)) {
                    player.sendMessage(color("&c%target%&e's staff mode: &aenabled!").replace("%target%", target.getDisplayName()));
                    target.sendMessage(color("&eYour staff mode has been: &aenabled!"));
                } else {
                    player.sendMessage(color("&c%target%&e's staff mode: &4disabled!").replace("%target%", target.getDisplayName()));
                    target.sendMessage(color("&eYour staff mode has been: &4disabled!"));
                }
            } else {
                player.sendMessage(color("&cNo permission."));
            }
        }
    }
}

