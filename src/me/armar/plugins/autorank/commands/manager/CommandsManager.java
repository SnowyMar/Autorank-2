package me.armar.plugins.autorank.commands.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.google.common.collect.Lists;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.AddCommand;
import me.armar.plugins.autorank.commands.ArchiveCommand;
import me.armar.plugins.autorank.commands.CheckCommand;
import me.armar.plugins.autorank.commands.ChooseCommand;
import me.armar.plugins.autorank.commands.CompleteCommand;
import me.armar.plugins.autorank.commands.ConvertUUIDCommand;
import me.armar.plugins.autorank.commands.DebugCommand;
import me.armar.plugins.autorank.commands.ForceCheckCommand;
import me.armar.plugins.autorank.commands.GlobalAddCommand;
import me.armar.plugins.autorank.commands.GlobalCheckCommand;
import me.armar.plugins.autorank.commands.GlobalSetCommand;
import me.armar.plugins.autorank.commands.HelpCommand;
import me.armar.plugins.autorank.commands.HooksCommand;
import me.armar.plugins.autorank.commands.ImportCommand;
import me.armar.plugins.autorank.commands.LeaderboardCommand;
import me.armar.plugins.autorank.commands.ReloadCommand;
import me.armar.plugins.autorank.commands.RemoveCommand;
import me.armar.plugins.autorank.commands.ResetCommand;
import me.armar.plugins.autorank.commands.SetCommand;
import me.armar.plugins.autorank.commands.SyncCommand;
import me.armar.plugins.autorank.commands.SyncStatsCommand;
import me.armar.plugins.autorank.commands.TimesCommand;
import me.armar.plugins.autorank.commands.TrackCommand;
import me.armar.plugins.autorank.commands.ViewCommand;
import me.armar.plugins.autorank.language.Lang;

/**
 * This class will manage all incoming command requests.
 * Commands are not performed here, they are only send to the correct place.
 * A specific {@linkplain AutorankCommand} class handles the task of performing the command.
 * 
 */
public class CommandsManager implements TabExecutor {

	private final Autorank plugin;

	// Use linked hashmap so that input order is kept
	private final LinkedHashMap<List<String>, AutorankCommand> registeredCommands = new LinkedHashMap<List<String>, AutorankCommand>();

	/**
	 * All command aliases are set up in here.
	 */
	public CommandsManager(final Autorank plugin) {
		this.plugin = plugin;

		// Register command classes
		registeredCommands.put(Arrays.asList("add"), new AddCommand(plugin));
		registeredCommands.put(Arrays.asList("help"), new HelpCommand(plugin));
		registeredCommands.put(Arrays.asList("set"), new SetCommand(plugin));
		registeredCommands.put(Arrays.asList("leaderboard", "leaderboards", "top"),
				new LeaderboardCommand(plugin));
		registeredCommands.put(Arrays.asList("remove", "rem"),
				new RemoveCommand(plugin));
		registeredCommands
				.put(Arrays.asList("debug"), new DebugCommand(plugin));
		registeredCommands.put(Arrays.asList("sync"), new SyncCommand(plugin));
		registeredCommands.put(Arrays.asList("syncstats"),
				new SyncStatsCommand(plugin));
		registeredCommands.put(Arrays.asList("reload"), new ReloadCommand(
				plugin));
		registeredCommands.put(Arrays.asList("import"), new ImportCommand(
				plugin));
		registeredCommands.put(Arrays.asList("complete"), new CompleteCommand(
				plugin));
		registeredCommands
				.put(Arrays.asList("check"), new CheckCommand(plugin));
		registeredCommands.put(Arrays.asList("archive", "arch"),
				new ArchiveCommand(plugin));
		registeredCommands.put(Arrays.asList("gcheck", "globalcheck"),
				new GlobalCheckCommand(plugin));
		registeredCommands.put(Arrays.asList("fcheck", "forcecheck"),
				new ForceCheckCommand(plugin));
		registeredCommands.put(Arrays.asList("convert"),
				new ConvertUUIDCommand(plugin));
		registeredCommands
				.put(Arrays.asList("track"), new TrackCommand(plugin));
		registeredCommands.put(Arrays.asList("gset", "globalset"),
				new GlobalSetCommand(plugin));
		registeredCommands.put(Arrays.asList("hooks", "hook"),
				new HooksCommand(plugin));
		registeredCommands.put(Arrays.asList("gadd", "globaladd"),
				new GlobalAddCommand(plugin));
		registeredCommands.put(Arrays.asList("view", "preview"),
				new ViewCommand(plugin));
		registeredCommands.put(Arrays.asList("choose"), new ChooseCommand(
				plugin));
		registeredCommands.put(Arrays.asList("times", "time"), new TimesCommand(
				plugin));
		registeredCommands.put(Arrays.asList("reset"), new ResetCommand(
				plugin));
	}

	public HashMap<List<String>, AutorankCommand> getRegisteredCommands() {
		return registeredCommands;
	}

	/**
	 * Gets whether the sender has the given permission.
	 * <br>Will also send a 'you don't have this permission' message if the sender does not have the given permission.
	 * @param permission Permission to check.
	 * @param sender Sender to check.
	 * @return true if this sender has the given permission, false otherwise.
	 */
	public boolean hasPermission(final String permission,
			final CommandSender sender) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ Lang.NO_PERMISSION
							.getConfigValue(new String[] { permission }));
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (args.length == 0) {
			sender.sendMessage(ChatColor.BLUE
					+ "-----------------------------------------------------");
			sender.sendMessage(ChatColor.GOLD + "Developed by: "
					+ ChatColor.GRAY + plugin.getDescription().getAuthors());
			sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.GRAY
					+ plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.YELLOW
					+ "Type /ar help for a list of commands.");
			return true;
		}

		final String action = args[0];

		// Go through every list and check if that action is in there.
		for (final Entry<List<String>, AutorankCommand> entry : registeredCommands
				.entrySet()) {

			for (final String actionString : entry.getKey()) {
				if (actionString.equalsIgnoreCase(action)) {
					return entry.getValue().onCommand(sender, cmd, label, args);
				}
			}
		}

		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW
				+ "Use '/ar help' for a list of commands.");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender,
			final Command cmd, final String commandLabel, final String[] args) {

		if (args.length <= 1) {
			// Show a list of commands if needed

			final List<String> commands = new ArrayList<String>();

			for (final Entry<List<String>, AutorankCommand> entry : registeredCommands
					.entrySet()) {
				final List<String> list = entry.getKey();

				commands.add(list.get(0));
			}

			return commands;
		}

		final String subCommand = args[0].trim();

		if (subCommand.equalsIgnoreCase("set")
				|| subCommand.equalsIgnoreCase("add")
				|| subCommand.equalsIgnoreCase("remove")
				|| subCommand.equalsIgnoreCase("rem")
				|| subCommand.equalsIgnoreCase("gadd")
				|| subCommand.equalsIgnoreCase("gset")) {

			if (args.length > 2) {

				final String arg = args[2];

				int count = 0;

				try {
					count = Integer.parseInt(arg);
				} catch (final NumberFormatException e) {
					count = 0;
				}

				return Lists.newArrayList("" + (count + 5));

			}

			return null;

		}

		// Return on tab complete of sub command
		for (final Entry<List<String>, AutorankCommand> entry : registeredCommands
				.entrySet()) {

			for (final String alias : entry.getKey()) {
				if (subCommand.trim().equalsIgnoreCase(alias)) {
					return entry.getValue().onTabComplete(sender, cmd,
							commandLabel, args);
				}
			}

		}

		return null;
	}
}
