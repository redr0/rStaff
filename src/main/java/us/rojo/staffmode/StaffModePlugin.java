package us.rojo.staffmode;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.SimpleInjector;
import us.rojo.staffmode.command.ModCommand;
import us.rojo.staffmode.command.VanishCommand;
import lombok.Getter;
import us.rojo.staffmode.listener.PlayerListener;
import us.rojo.staffmode.manager.ModManager;
import us.rojo.staffmode.manager.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StaffModePlugin extends JavaPlugin {

    @Getter
    private static StaffModePlugin plugin;

    private ModManager modManager;

    private VanishManager vanishManager;

    private CommandsManager<CommandSender> commands;

    @Override
    public void onEnable() {
        plugin = this;

        modManager = new ModManager(this);

        vanishManager = new VanishManager(this);

        setupCommands();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void setupCommands() {
        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return (sender instanceof Player) && (sender.hasPermission(perm) || sender.isOp());
            }
        };

        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);

        commands.setInjector(new SimpleInjector(this));
        cmdRegister.register(VanishCommand.class);
        cmdRegister.register(ModCommand.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException ex) {
            sender.sendMessage(ChatColor.RED + "No permission.");
        } catch (MissingNestedCommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getUsage());
        } catch (CommandUsageException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            sender.sendMessage(ChatColor.RED + ex.getUsage());
        } catch (CommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }

        return true;
    }
}

