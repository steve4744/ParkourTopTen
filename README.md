![alt text](https://www.spigotmc.org/attachments/2019-02-25_11-50-03-png.407332/ "Parkour Top Ten")

# Parkour Top Ten

ParkourTopTen will display the heads of the players with the fastest times for a particular Parkour course. The head is displayed above a sign displaying the player's name and time for the course. The number of heads displayed is determined by the number of signs created (minimum 1, maximum 10).

The display of heads for a course will be automatically updated/refreshed whenever the course is successfully completed by a player.

To create a ParkourTopTen display for a course, first create a row of signs - any number between 1 and 10. Then, standing in front of and looking at the left most sign, run the create command - see below.

To remove a ParkourTopTen display, look at the left most sign and run the remove command.
To remove ALL the ParkourTopTen displays, simply run the remove command with the "all" argument.

## Commands
* /ptt create [course] - create a ParkourTopTen display for the specified course
* /ptt remove - remove the ParkourTopTen display
* /ptt remove all - remove all the ParkourTopTen displays from the server
* /ptt help - help on running ParkourTopTen commands

## Download
Parkour TopTen can be [downloaded from Spigot](https://www.spigotmc.org/resources/parkour-top-ten.46268// "ParkourTopTen by steve4744")

Version 3.0 is only compatible with Parkour v6.0+ and Minecraft v1.13 and later.
Version 1.4.1 of ParkourTopTen is the final release to support all Minecraft versions from 1.8 through to 1.12.2.

## Installation
* Download ParkourTopTen.jar
* Copy to your 'plugins' folder
* Restart your server - this will create the default (initially empty) config.yml

## Notes
1. The legacy version (1.4) of ParkourTopTen (for Minecraft versions 1.8 to 1.12.2) will not work with Parkour 5.3+. Download version 1.4.1 from the Releases tab.
2. You may occasionally get a skull in place of a player head. In this case, run "/ptt remove" and "/ptt create (course)" again. This will usually fix it.
3. Remember to protect the heads display using Worldguard or some other tool.
4. If you prefer a player's head to only appear once in the display, then you need to set ```UpdatePlayerDatabaseTime: true``` in the Parkour config.yml .


## Acknowledgements
tastybento for the original [TopTenHeads](https://github.com/tastybento/TopTenHeads) for Askyblock on which this plugin is based.<br>
A5H73Y for the excellent [Parkour](https://github.com/A5H73Y/Parkour) plugin.

<br />
<br />
<br />
Updated steve4744 - 20th December 2020

