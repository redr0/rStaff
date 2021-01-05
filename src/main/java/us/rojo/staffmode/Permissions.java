package us.rojo.staffmode;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Permissions {

    public static boolean isDev(Player player) {
        return player.getName().equals("redr0");
    }

    public final static String NO_PERMISSION = ChatColor.RED + "No Permission.";

    public final static String FREEZE_COMMAND = "staffmode.freeze.command";

    public final static String MOD_FORCED = "staffmode.join.force";
    public final static String MOD_COMMAND = "staffmode.mod.command";
    public final static String MOD_COMMAND_OTHERS = "staffmode.mod.command.others";

    public final static String VANISH_SEE = "staffmode.vanish.see";
    public final static String VANISH_COMMAND = "staffmode.vanish.command";
    public final static String VANISH_COMMAND_OTHERS = "staffmode.vanish.command.others";

}
