package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * The command delegator for the '/ar add' command.
 */
public class AddCommand extends AutorankCommand {

    private final Autorank plugin;

    public AddCommand(final Autorank instance) {
        this.setUsage("/ar add [player] [value]");
        this.setDesc("Add [value] to [player]'s time");
        this.setPermission("autorank.add");

        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission("autorank.add", sender)) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar add <player> <value>"));
            return true;
        }

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

        if (uuid == null) {
            sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
            return true;
        }

        int value = -1;

        if (args.length > 2) {

            final StringBuilder builder = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                builder.append(args[i]);
            }
            
            int changeValue = 0;

            if (!builder.toString().contains("m") && !builder.toString().contains("h")
                    && !builder.toString().contains("d")) {
                changeValue = AutorankTools.stringtoInt(builder.toString().trim());
            } else {
                changeValue = AutorankTools.stringToTime(builder.toString(), Time.MINUTES);   
            }
            
            if (changeValue < 0) {
                value = -1;
            } else {
                value += plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid) + changeValue;
            }
        }

        if (value >= 0) {

            if (plugin.getUUIDStorage().hasRealName(uuid)) {
                args[1] = plugin.getUUIDStorage().getRealName(uuid);
            }

            plugin.getFlatFileManager().setLocalTime(TimeType.TOTAL_TIME, value, uuid);
            AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + ""));
        } else {
            AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue("/ar add [player] [value]"));
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(
     * org.bukkit.command.CommandSender, org.bukkit.command.Command,
     * java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
            final String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

}
