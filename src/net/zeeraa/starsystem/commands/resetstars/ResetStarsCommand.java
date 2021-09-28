package net.zeeraa.starsystem.commands.resetstars;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.starsystem.StarSystem;
import net.zeeraa.zcommandlib.command.ZCommand;
import net.zeeraa.zcommandlib.command.utils.AllowedSenders;

public class ResetStarsCommand extends ZCommand {
	public ResetStarsCommand() {
		super("resetstars");
		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("starsystem.resetstars");
		setPermissionDefaultValue(PermissionDefault.TRUE);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!StarSystem.getInstance().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "This plugin in not enabled right now");
			return false;
		}
		
		Player player = (Player) sender;

		boolean confirmed = false;

		if (args.length == 1) {
			if (args[0].equals("confirm")) {
				confirmed = true;
			}
		}

		if (confirmed) {
			StarSystem.getInstance().setStars(player, 0);
			player.sendMessage(ChatColor.GREEN + "Your star count has been reset");
		} else {
			player.sendMessage(ChatColor.RED + "Warning: this command will reset your star count to 0");
			player.sendMessage(ChatColor.RED + "Please run " + ChatColor.AQUA + "/resetstars confirm " + ChatColor.RED + "to confirm this");
		}

		return true;
	}
}