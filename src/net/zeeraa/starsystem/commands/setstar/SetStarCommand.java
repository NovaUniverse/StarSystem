package net.zeeraa.starsystem.commands.setstar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.starsystem.StarSystem;
import net.zeeraa.zcommandlib.command.ZCommand;
import net.zeeraa.zcommandlib.command.utils.AllowedSenders;

public class SetStarCommand extends ZCommand {
	public SetStarCommand() {
		super("setstar");
		setAliases(ZCommand.generateAliasList("setstars"));
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("starsystem.setstar");
		setPermissionDefaultValue(PermissionDefault.OP);
		setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(!StarSystem.getInstance().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "This plugin in not enabled right now");
			return false;
		}
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide a player");
			return true;
		}

		Player player = Bukkit.getServer().getPlayer(args[0]);

		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Could not find a player named " + args[0]);
			return true;
		}

		if (!player.isOnline()) {
			sender.sendMessage(ChatColor.RED + "The player is not online");
			return true;
		}

		if (args.length == 1) {
			sender.sendMessage(ChatColor.RED + "Please provide the amount of stars to set");
			return true;
		}

		int stars = 0;

		try {
			stars = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Please provide a valid amount of stars");
			return true;
		}

		StarSystem.getInstance().setStars(player, stars);

		sender.sendMessage(ChatColor.GREEN + "Set " + player.getName() + "s star count to " + stars);

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<>();

		if (args.length == 0 || args.length == 1) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (sender instanceof Player) {
					if (!((Player) sender).canSee(player)) {
						continue;
					}
				}

				result.add(player.getName());
			}
		}

		return result;
	}
}