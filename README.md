### What is "ZeFreeze"
ZeFreeze is a plugin that make possible to freeze a player in place to be able to check if he is hacking or even just for fun.

### Features
- Play a sound when freezing/unfreezing a player
- Add an action when the player leave the server while being frozen
- Remove the freezing state when the player leave the server
- Freeze offline players that have played at least once on the server
- Anti-disconnection GUI to avoid players from disconnecting using the game menu
- Block commands while the player is frozen with a possible whitelist
- Permission support
- Fully customizable with configs and messages files
- Pretty much every function can be toggled on or off
- Optimized and lag free 

### How to install
- Download the latest version of the plugin
- Place the downloaded .jar file in your server plugin folder
- Start your server
- Enjoy!

### Commands
- /zefreeze » Display help page of the plugin
- /zefreeze <player> » Freeze/Unfreeze a player (toggle)
- /zeunfreeze <player> » Freeze/Unfreeze a player (toggle)
- /zefreeze control <player> » Teleport a frozen player to the control point
- /zefreeze control set &c» Set the control point location"
- /zefreeze info <player> » Info about why a player is frozen
- /zefreeze help » Display help page of the plugin
- /zefreeze reload » Reload the plugin

### Messages and Configs Files
<details>
<summary>Messages</summary>

	prefix: "&c[&dZe&bFreeze&c] "
	
	no-reason: "&aNo reason"
	
	player-frozen:
	  - "&2You have &cfrozen &b{player}&2!"
	  - "&2Reason: {reason}"
	  - "&2He can't move &canymore!"
	
	player-unfrozen:
	  - "&2You have &cunfrozen &b{player}&2!"
	  - "&2He can now move &cfreely!"
	
	target-frozen:
	  - "&2You have been &cfrozen by &b{freezer}&2!"
	  - "&2Reason: {reason}"
	  - "&2You can't move &canymore!"
	
	target-unfrozen:
	  - "&2You have been &cunfrozen by &b{freezer}&2!"
	  - "&2You can now move &cfreely!"
	
	target-freeze-info:
	  - "&2&m=====&r &dOpsy&bFreeze &c- &d&lInfo &2&m=====&r"
	  - "&6Freezer &c» &b{freezer}"
	  - "&6Reason &c» &2{reason}"
	  - "&6Location &c» &2X: &e{loc_x}&2, Y: &e{loc_y}&2, Z: &e{loc_z}"
	  - "&2&m=====&r &dOpsy&bFreeze &c- &d&lInfo &2&m=====&r"
	
	plugin-reload:
	  - "&2Plugin reloaded!"
	
	unfreeze-disconnect:
	  - "&b{player} &2has been unfrozen because he left the server."
	
	staff-disconnect-alert:
	  - "&b{player} &chas left the server while being frozen!"
	
	help-page:
	  - "&d/zefreeze &c» &2Show the help page"
	  - "&d/zefreeze &b<player> &c» &2Freeze a player in place"
	  - "&d/zefreeze control &b<player> &c» &2Teleport a frozen player to the control point"
	  - "&d/zefreeze control set &c» &2Set the control point location"
	  - "&d/zefreeze info &b<player> &c» &2Info about why a player is frozen"
	  - "&d/zezfreeze &bhelp &c» &2Show the help page"
	  - "&d/zefreeze &breload &c» &2Reload the plugin"
	  - " "
	  - "&d/zeunfreeze &b<player> &c» &2Unfreeze a frozen player"
	
	errors:
	  self-freeze:
	    - "&cYou can't freeze yourself!"
	  close-anti-disconnection-gui:
	    - "&cYou can't close thi GUI because you are frozen!"
	  player-offline:
	    - "&cThis player is offline!"
	  player-already-frozen:
	    - "&b{player} &cis already frozen!"
	  player-not-frozen:
	    - "&b{player} &cis not frozen!"
	  damaged-a-frozen-player:
	    - "&cThis player is frozen, you can't damage him!"
	  damage-while-frozen:
	    - "&cYou can't damage an entity while being frozen!"
	  place-while-frozen:
	    - "&cYou can't place blocks while frozen!"
	  break-while-frozen:
	    - "&cYou can't break blocks while frozen!"
	  move-while-frozen:
	    - "&cSorry, but you can't move because you have been &4FROZEN &cby &b{freezer}!"
	    - "&cIf you think that's a mistake, feel free to contact a staff member!"
	  not-enough-permissions:
	    - "&cSorry, but you don't have enough permissions to do that!"
	  invalid-sound:
	    - "The sound {sound} doesn't exist! Please change it in the config file!"
	  control-location-not-set:
	    - "&cYou haven't set any control location yet!"
	  player-only:
	    - "&cYou need to be a player to perform that action!"
</details>
<details>
	<summary>Configuration</summary>

	# Allow the console to make a command when a player leave the server while he is frozen
	disconnect-action:
	  enabled: true
	  commands:
	    - "money set {player} 0"
	    - "ban {player} {reason}"
	
	# Block the player from making commands when he is frozen
	block-commands:
	  enabled: true
	  whitelist: []
	
	# Unfreeze the player if he disconnect while being frozen
	unfreeze-on-disconnect: true
	
	# Enable the toggle function so when you type /freeze or /unfreeze it can freeze and unfreeze the player
	freeze-toggle: true
	
	# Block the Y axe of movement while frozen avoiding players to jump or flying up and down
	block-y-axis: true
	
	# Set the freeze message cooldown in seconds to not spam the player chat while the player moves
	freeze-message-cooldown: 5
	
	# You can find all the available sound here https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html
	# Sound played when the target is freezed
	freeze-sound: ENTITY_GHAST_SCREAM
	
	# Sound played when the target is unfreezed
	unfreeze-sound: ENTITY_PIG_DEATH
	
	# Disable the interaction of a frozen player
	disable-interaction: true
	
	# Disable the damages of a frozen player
	disable-damages: true
	
	# Disable the possibility to place blocks from a frozen player
	disable-block-place: true
	
	# Disable the possibility to break block from a frozen player
	disable-block-break: true
	
	anti-disconnection-gui:
	  enabled: true
	  size: 27
	  title: "&c&lYou are frozen!"
	  background:
	    enabled: true
	    material: BARRIER
	    name: "&c&lYou are frozen!"
	    lore: []
	    model: 0
	  border:
	    enabled: true
	    material: BLACK_STAINED_GLASS_PANE
	    name: "&c&lYou are frozen!"
	    lore: []
	    model: 0
      items:
	    first-item:
	      slot: 11
	      material: REDSTONE_BLOCK
	      name: "&c&lYou are frozen!"
	      lore: []
	      model: 0
        second-item:
	      slot: 15
          material: REDSTONE_BLOCK
	      name: "&c&lYou are frozen!"
	      lore: []
	      model: 0

	control-location:
</details>

### Permissions
- zefreeze.freeze (/zefreeze <player>)
- zefreeze.unfreeze (/zeunfreeze <player>)
- zefreeze.info (/zefreeze info <player>)
- zefreeze.control.set (/zefreeze control set)
- zefreeze.control (/zefreeze control <player>)
- zefreeze.reload (/zefreeze reload)

<details>
	<summary>Changelogs</summary>

	- Add a multiple command action support (Added - 0.0.2)
	- Add toggle option for the command "/freeze" (Added - 0.0.3)
	- Add an independent "/unfreeze" command (Added - 0.0.3)
	- Add an option to block the Y axis while frozen (Added - 0.1.1)
	- Add an option to set the freeze message cooldown (Added - 0.1.1)
	- Add a "/freeze info" command to know the reason of the player's freeze (Added - 0.1.2)
	- Add a location to teleport both the player and the staff member to a specific spot for control (0.2.0)
	- Add No PvP and No Block Break on freeze (0.3.0)
	- Tab completion support for the commands (1.0.0)
	- Block commands while frozen with a possible whitelist (1.1.0)
	- Add an anti-disconnection GUI and an item saying what the player has to do (1.2.0)
</details>

Need support ? Feel free to contact me on discord! [Click here to join the discord](https://discord.gg/93yXste)!