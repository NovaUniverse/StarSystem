# Starsystem
Starsystem is a plugin developed for StardustSMP

This plugin was developed for minecraft 1.16.5 but it should work with most versions of the game

## How it works
Players receive stars by being online and by earning stars you both get a higher placement on the leaderboard and you also get a name color depending on the amount of stars you have

## Config
You can edit the plugin config in plugins/StarSystem/config.yml
* time_between_stars This is the time between earning stars in minutes
* max_stars This is the max amount of stars players can earn by being online
* enable_name_color Set to false to disable name color

## Commands
* /addstars (Player) (Stars)
description: adds the provided amount of stars to the player
permission: starsystem.addstar
default permission level: op

* /setstar (Player) (Stars)
description: set the star count of a player
aliases: setstars
permission: starsystem.setstar
default permission level: op

* /resetstars
description: resets the star count of the player that runs the command
permission: starsystem.resetstars
default permission level: everyone

* /startop
description: show the top 10 players
aliases: topstar, topstars
permission: starsystem.startop
default permission level: everyone

* /stars (Player)
description: show the amount of stars you or the target player has
permission: starsystem.stars
default permission level: everyone

## API
If you want to develop a plugin that uses the star system add the following to your pom.xml file (Or if you are not using maven just add the jar to your libraries)

javadocs can be found here https://zeeraa.net/javadocs/StarSystem/

```
<repository>
	<id>zeeraas-repo</id>
	<url>https://nexus.zeeraa.net/repository/maven-group/</url>
</repository>
```

```
<dependency>
	<groupId>net.zeeraa</groupId>
	<artifactId>StarSystem</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<scope>provided</scope>
</dependency>
```

## Other info
This plugin uses [ZCommandLib](https://github.com/AntonUden/ZCommandLib) for registering commands
