package net.zeeraa.starsystem.commands.stars;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.starsystem.StarSystem;
import net.zeeraa.zcommandlib.command.ZCommand;
import net.zeeraa.zcommandlib.command.utils.AllowedSenders;

public class StarsCommand extends ZCommand {

	public StarsCommand() {
		super("stars");
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("starsystem.stars");
		setPermissionDefaultValue(PermissionDefault.TRUE);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!StarSystem.getInstance().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "This plugin in not enabled right now");
			return false;
		}

		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Please provide a player");
			} else {
				Player player = (Player) sender;
				int stars = StarSystem.getInstance().getPlayerStars(player);

				if (stars == -1) {
					sender.sendMessage(ChatColor.RED + "Something went wrong. Please try again later");
				} else {
					sender.sendMessage(ChatColor.GOLD + "You have " + ChatColor.AQUA + stars + ChatColor.GOLD + " stars");
				}
			}
		} else {
			int stars = StarSystem.getInstance().getPlayerStars(args[0]);

			if (stars == -1) {
				sender.sendMessage(ChatColor.RED + "Could not find any data for " + args[0] + ". Please check that the name is correct");
			} else {
				sender.sendMessage(ChatColor.AQUA + args[0] + ChatColor.GOLD + " has " + ChatColor.AQUA + stars + ChatColor.GOLD + " stars");
			}

		}
		return true;
	}
}