package net.zeeraa.starsystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONObject;

import net.zeeraa.starsystem.commands.addstar.AddStarCommand;
import net.zeeraa.starsystem.commands.resetstars.ResetStarsCommand;
import net.zeeraa.starsystem.commands.setstar.SetStarCommand;
import net.zeeraa.starsystem.commands.stars.StarsCommand;
import net.zeeraa.starsystem.commands.startop.StarTop;
import net.zeeraa.starsystem.utils.JSONFileType;
import net.zeeraa.starsystem.utils.JSONFileUtils;
import net.zeeraa.zcommandlib.command.registrator.ZCommandRegistrator;

/**
 * A plugin developed for StardustSMP that gives players stars for playing
 * 
 * @author Zeeraa
 */
public class StarSystem extends JavaPlugin implements Listener {
	private static StarSystem instance = null;

	private JSONObject playerData;
	private File playerDataFile;

	private int maxLoginRewardStars;
	private int timeBetweenStars;
	private boolean nameColorEnabled;
	private boolean commandsRegisterd = false;

	private Map<UUID, BukkitTask> playerTasks;

	/**
	 * Get the instance of the {@link StarSystem} api
	 * 
	 * @return {@link StarSystem} instance
	 */
	public static StarSystem getInstance() {
		return instance;
	}

	/**
	 * Get the amount of stars a player has. This will return -1 if the player has
	 * not yet joined
	 * 
	 * @param player The player to check
	 * @return The amount of stars or -1 if the player is not found
	 */
	public int getPlayerStars(Player player) {
		return this.getPlayerStars(player.getUniqueId());
	}

	/**
	 * Get the amount of stars a player has. This will return -1 if the player has
	 * not yet joined
	 * 
	 * @param uuid The uuid of the player
	 * @return The amount of stars or -1 if the player is not found
	 */
	public int getPlayerStars(UUID uuid) {
		if (playerData.has(uuid.toString())) {
			JSONObject data = playerData.getJSONObject(uuid.toString());

			return data.getInt("stars");
		}

		return -1;
	}

	/**
	 * Get the amount of stars a player has. This will return -1 if the player has
	 * not yet joined
	 * 
	 * @param username The username of the player
	 * @return The amount of stars or -1 if the player is not found
	 */
	public int getPlayerStars(String username) {
		for (String key : playerData.keySet()) {
			JSONObject data = playerData.getJSONObject(key);
			if (data.getString("username").equalsIgnoreCase(username)) {
				return data.getInt("stars");
			}
		}

		return -1;
	}

	/**
	 * Set the amount of stars a player has
	 * 
	 * @param player The player
	 * @param stars  The new amount of stars
	 * @return <code>true</code> if the player was found
	 */
	public boolean setStars(Player player, int stars) {
		return this.setStars(player.getUniqueId(), stars);
	}

	/**
	 * Set the amount of stars a player has
	 * 
	 * @param uuid  the uuid of the player
	 * @param stars The new amount of stars
	 * @return <code>true</code> if the player was found
	 */
	public boolean setStars(UUID uuid, int stars) {
		if (playerData.has(uuid.toString())) {
			JSONObject data = playerData.getJSONObject(uuid.toString());

			data.put("stars", stars);

			playerData.put(uuid.toString(), data);

			this.saveData();

			Player player = Bukkit.getServer().getPlayer(uuid);
			if (player != null) {
				if (player.isOnline()) {
					this.updatePlayerNameColor(player);
				}
			}
		}

		return false;
	}

	/**
	 * Save the active player data to the json file
	 * 
	 * @return <code>true</code> on success
	 */
	public boolean saveData() {
		try {
			JSONFileUtils.saveJson(playerDataFile, playerData);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().warning("Failed to save player data");
		}
		return false;
	}

	/**
	 * Add the defined amount of stars to the player
	 * 
	 * @param player       The player to add the stars to
	 * @param stars        The amount of stars to add
	 * @param messageColor The color of the message to use. Set this to null to
	 *                     disable the message
	 * @return <code>true</code> if the player was found and the stars was added
	 */
	public boolean addStars(Player player, int stars, ChatColor messageColor) {
		int oldStars = this.getPlayerStars(player);

		if (oldStars == -1) {
			return false;
		}

		this.setStars(player, oldStars + stars);

		if (messageColor != null) {
			player.sendMessage(messageColor + "You received " + stars + " star " + (stars == 1 ? "" : "s"));
		}

		return true;
	}

	/**
	 * Update the display name of the provided player so that they get their name
	 * color.<br><br>
	 * This wont update if {@link StarSystem#isNameColorEnabled()} is <code>false</code>
	 * 
	 * @param player The player to update
	 */
	public void updatePlayerNameColor(Player player) {
		// 5 Stars - Gray ( &7 )
		// 10 Stars - Dark Gray ( &8 )
		// 15 Stars - Dark Blue ( &1 )
		// 20 Stars - Aqua ( &b )
		// 25 Stars - Dark Aqua ( &3 )
		// 35 Stars - Indago ( &9 )
		// 50 Stars - Dark Green ( &2 )
		// 75 Stars - Lime ( &a )
		// 100 Stars - Red ( &c )
		// 150 Stars - Dark Red ( &4 )
		// 200 Stars - Yellow ( &e )
		// 250 Stars - Gold ( &6 )
		// 350 Stars - Pink ( &d )
		// 500 Stars - Purple ( &5 )

		if (!this.isNameColorEnabled()) {
			return;
		}

		int stars = this.getPlayerStars(player);

		if (stars == -1) {
			return;
		}

		ChatColor color = ChatColor.WHITE;

		if (stars >= 500) {
			color = ChatColor.DARK_PURPLE;
		} else if (stars >= 350) {
			color = ChatColor.LIGHT_PURPLE;
		} else if (stars >= 250) {
			color = ChatColor.GOLD;
		} else if (stars >= 200) {
			color = ChatColor.YELLOW;
		} else if (stars >= 150) {
			color = ChatColor.DARK_RED;
		} else if (stars >= 100) {
			color = ChatColor.RED;
		} else if (stars >= 75) {
			color = ChatColor.GREEN;
		} else if (stars >= 50) {
			color = ChatColor.DARK_GREEN;
		} else if (stars >= 35) {
			color = ChatColor.BLUE;
		} else if (stars >= 25) {
			color = ChatColor.DARK_AQUA;
		} else if (stars >= 20) {
			color = ChatColor.AQUA;
		} else if (stars >= 15) {
			color = ChatColor.DARK_BLUE;
		} else if (stars >= 10) {
			color = ChatColor.DARK_GRAY;
		} else if (stars >= 5) {
			color = ChatColor.GRAY;
		}

		player.setDisplayName(color + player.getName() + ChatColor.RESET);
	}

	/**
	 * Get all player data
	 * 
	 * @return player data json object
	 */
	public JSONObject getPlayerData() {
		return playerData;
	}

	/**
	 * Get the max amount of stars the player can receive from being logged in
	 * 
	 * @return max login reward stars
	 */
	public int getMaxLoginRewardStars() {
		return maxLoginRewardStars;
	}

	/**
	 * Get the time in minutes between earning stars
	 * 
	 * @return time between stars
	 */
	public int getTimeBetweenStars() {
		return timeBetweenStars;
	}

	/**
	 * Check if name color is enabled
	 * 
	 * @return <code>true</code> if name color is enabled
	 */
	public boolean isNameColorEnabled() {
		return nameColorEnabled;
	}

	@Override
	public void onEnable() {
		StarSystem.instance = this;
		playerDataFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "playerdata.json");
		playerTasks = new HashMap<>();

		saveDefaultConfig();

		maxLoginRewardStars = getConfig().getInt("max_stars");
		timeBetweenStars = getConfig().getInt("time_between_stars");
		nameColorEnabled = getConfig().getBoolean("enable_name_color");

		try {
			if (!playerDataFile.exists()) {
				JSONFileUtils.createEmpty(playerDataFile, JSONFileType.JSONObject);
			}
			playerData = JSONFileUtils.readJSONObjectFromFile(playerDataFile);
		} catch (IOException e) {
			getLogger().warning("Failed to init player data file. Check that the file at " + playerDataFile.getAbsolutePath() + " is not broken");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			e.printStackTrace();
			return;
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		if (!commandsRegisterd) {
			commandsRegisterd = true;
			ZCommandRegistrator.registerCommand(this, new StarsCommand());
			ZCommandRegistrator.registerCommand(this, new SetStarCommand());
			ZCommandRegistrator.registerCommand(this, new AddStarCommand());
			ZCommandRegistrator.registerCommand(this, new StarTop());
			ZCommandRegistrator.registerCommand(this, new ResetStarsCommand());
		}
	}

	@Override
	public void onDisable() {
		StarSystem.instance = null;
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);
		playerTasks.clear();

		this.saveData();

		playerData = null;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (playerData.has(player.getUniqueId().toString())) {
			JSONObject data = playerData.getJSONObject(player.getUniqueId().toString());

			if (!data.getString("username").equals(player.getName())) {
				data.put("username", player.getName());
				playerData.put(player.getUniqueId().toString(), data);
				this.saveData();
			}
		} else {
			getLogger().info("Setting up intial star data for player " + player.getName());
			JSONObject data = new JSONObject();
			data.put("username", player.getName());
			data.put("stars", 0);
			data.put("time_counter", 0);
			playerData.put(player.getUniqueId().toString(), data);
			this.saveData();
		}

		if (playerTasks.containsKey(player.getUniqueId())) {
			playerTasks.get(player.getUniqueId()).cancel();
		}

		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				JSONObject data = playerData.getJSONObject(player.getUniqueId().toString());

				if (data != null) {
					if (getPlayerStars(player) >= getMaxLoginRewardStars()) {
						return;
					}

					int timeCounter = data.getInt("time_counter");

					if (timeCounter >= getTimeBetweenStars() - 1) {
						data.put("time_counter", 0);
						playerData.put(player.getUniqueId().toString(), data);
						addStars(player, 1, ChatColor.YELLOW); // this also calls saveData() so no need to do it
					} else {
						data.put("time_counter", timeCounter + 1);
						playerData.put(player.getUniqueId().toString(), data);

						saveData();
					}
				}
			}
		}.runTaskTimer(this, 20 * 60, 20 * 60);

		playerTasks.put(player.getUniqueId(), task);

		this.updatePlayerNameColor(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (playerTasks.containsKey(player.getUniqueId())) {
			playerTasks.get(player.getUniqueId()).cancel();
			playerTasks.remove(player.getUniqueId());
		}
	}
}