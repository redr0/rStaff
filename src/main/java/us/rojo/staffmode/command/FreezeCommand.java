package us.rojo.staffmode.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.rojo.staffmode.Permissions;
import us.rojo.staffmode.StaffModePlugin;

public class FreezeCommand {

    private StaffModePlugin plugin;

    public FreezeCommand(StaffModePlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = {"freeze", "ss"}, desc = "Freeze a player", min = 0, max = 1)
    @CommandPermissions(Permissions.FREEZE_COMMAND)
    public void vanishCommand(CommandContext args, CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
    }
}
