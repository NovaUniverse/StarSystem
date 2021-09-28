package net.zeeraa.starsystem.commands.startop;

public class StarTopEntry {
	private String username;
	private int stars;

	public StarTopEntry(String username, int stars) {
		this.username = username;
		this.stars = stars;
	}

	public String getUsername() {
		return username;
	}

	public int getStars() {
		return stars;
	}
}