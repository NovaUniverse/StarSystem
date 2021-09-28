package net.zeeraa.starsystem.commands.startop;

import java.util.Comparator;

public class StarTopComparator implements Comparator<StarTopEntry>{
	@Override
	public int compare(StarTopEntry o1, StarTopEntry o2) {
		if (o1.getStars() == o2.getStars()) {
			return 0;
		} else if (o1.getStars() < o2.getStars()) {
			return 1;
		} else {
			return -1;
		}
	}
}