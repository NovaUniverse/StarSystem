package net.zeeraa.starsystem.commands.startop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.json.JSONObject;

import net.zeeraa.starsystem.StarSystem;
import net.zeeraa.zcommandlib.command.ZCommand;
import net.zeeraa.zcommandlib.command.utils.AllowedSenders;

public class StarTop extends ZCommand {

	public StarTop() {
		super("startop");
		setAliases(ZCommand.generateAliasList("topstar", "topstars"));
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("starsystem.startop");
		setPermissionDefaultValue(PermissionDefault.TRUE);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!StarSystem.getInstance().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "This plugin in not enabled right now");
			return false;
		}

		List<StarTopEntry> entries = new ArrayList<>();

		for (String key : StarSystem.getInstance().getPlayerData().keySet()) {
			JSONObject data = StarSystem.getInstance().getPlayerData().getJSONObject(key);

			entries.add(new StarTopEntry(data.getString("username"), data.getInt("stars")));
		}

		Collections.sort(entries, new StarTopComparator());

		String message = ChatColor.GOLD + "Top 10 players:\n";
		for (int i = 0; i < 10; i++) {
			if (entries.size() > 0) {
				StarTopEntry entry = entries.remove(0);
				message += ChatColor.GOLD + entry.getUsername() + " : " + ChatColor.AQUA + entry.getStars() + ChatColor.GOLD + " stars\n";
			}
		}

		sender.sendMessage(message);

		return true;
	}
}