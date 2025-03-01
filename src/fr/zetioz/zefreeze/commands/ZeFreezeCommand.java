package fr.zetioz.zefreeze.commands;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.coreutils.SoundUtils;
import fr.zetioz.zefreeze.FreezeElement;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import fr.zetioz.zefreeze.guis.AntiDisconnectionGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeCommand implements TabExecutor, FilesManagerUtils.ReloadableFiles
{

	private final ZeFreezePlugin instance;
	private final AntiDisconnectionGUI antiDisconnectionGUI;
	private YamlConfiguration messages;
	private YamlConfiguration config;
	private Map<UUID, FreezeElement> playerFrozen;
	private String prefix;

	public ZeFreezeCommand(ZeFreezePlugin instance) throws FileNotFoundException
	{
		this.instance = instance;
		this.antiDisconnectionGUI = new AntiDisconnectionGUI(instance);
		this.playerFrozen = instance.getPlayerFrozen();
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		this.messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
		this.config = instance.getFilesManagerUtils().getSimpleYaml("config");
		this.prefix = messages.getString("prefix", "&c[&6ZeFreeze&c] &r");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!cmd.getName().equalsIgnoreCase("zefreeze") && !cmd.getName().equalsIgnoreCase("zeunfreeze"))
		{
			return false;
		}

		if(args.length == 0)
		{
			sendMessage(sender, messages.getStringList("help-page"), prefix);
			return true;
		}

		return switch(args[0].toLowerCase())
		{
			case "info" ->
			{
				handleInfoCommand(sender, args);
				yield true;
			}
			case "reload" ->
			{
				handleReloadCommand(sender);
				yield true;
			}
			case "control" ->
			{
				handleControlCommand(sender, args);
				yield true;
			}
			case "@a", "all" ->
			{
				handleFreezeAllCommand(sender, args);
				yield true;
			}
			default ->
				handleUnfreezeCommand(sender, args);
		};
	}

	private void handleInfoCommand(CommandSender sender, String[] args)
	{
		if(args.length < 2 || !sender.hasPermission("zefreeze.info"))
		{
			sendMessage(sender, messages.getStringList("help-page"), prefix);
			return;
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

		if(!offlinePlayer.hasPlayedBefore())
		{
			sendMessage(sender, messages.getStringList("errors.player-not-played-before"), prefix, "{player}", args[1]);
			return;
		}

		UUID playerUUID = offlinePlayer.getUniqueId();

		if(!playerFrozen.containsKey(playerUUID))
		{
			sendMessage(sender, messages.getStringList("errors.player-not-frozen"), prefix, "{player}", args[1]);
			return;
		}

		FreezeElement freeze = playerFrozen.get(playerUUID);
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		sendMessage(sender, messages.getStringList("target-freeze-info"), prefix, "{freezer}", freeze.getFreezer(), "{reason}", freeze.getReason(), "{loc_x}", decimalFormat.format(freeze.getLocation().getX()), "{loc_y}", decimalFormat.format(freeze.getLocation().getY()), "{loc_z}", decimalFormat.format(freeze.getLocation().getZ()));
	}

	private void handleReloadCommand(CommandSender sender)
	{
		if(!sender.hasPermission("zefreeze.reload"))
		{
			sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
			return;
		}

		sendMessage(sender, messages.getStringList("plugin-reload"), prefix);
		instance.getFilesManagerUtils().reloadAllSimpleYaml();
	}

	private void handleControlCommand(CommandSender sender, String[] args)
	{
		if(!(sender instanceof Player player))
		{
			sendMessage(sender, messages.getStringList("errors.player-only"), prefix);
			return;
		}

		if(args.length >= 2 && args[1].equalsIgnoreCase("set") && sender.hasPermission("zefreeze.control.set"))
		{
			saveControlLocation(player);
		}
		else if(sender.hasPermission("zefreeze.control"))
		{
			teleportToControlLocation(sender, args);
		}
		else
		{
			sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
		}
	}

	private void saveControlLocation(Player player)
	{
		config.set("control-location.world", player.getLocation().getWorld().getName());
		config.set("control-location.x", player.getLocation().getBlockX());
		config.set("control-location.y", player.getLocation().getBlockY());
		config.set("control-location.z", player.getLocation().getBlockZ());
		instance.getFilesManagerUtils().saveSimpleYaml("config");
	}

	private void teleportToControlLocation(CommandSender sender, String[] args)
	{
		if(!config.isConfigurationSection("control-location"))
		{
			sendMessage(sender, messages.getStringList("errors.control-location-not-set"), prefix);
			return;
		}

		Player frozenPlayer = Bukkit.getPlayer(args[0]);
		if(frozenPlayer == null || !playerFrozen.containsKey(frozenPlayer.getUniqueId()))
		{
			sendMessage(sender, messages.getStringList("errors.player-not-frozen"), prefix, "{player}", args[0]);
			return;
		}

		String world = config.getString("control-location.world");
		double x = config.getDouble("control-location.x");
		double y = config.getDouble("control-location.y");
		double z = config.getDouble("control-location.z");
		Location controlLocation = new Location(Bukkit.getWorld(world), x, y, z);

		playerFrozen.remove(frozenPlayer.getUniqueId());
		playerFrozen.computeIfPresent(frozenPlayer.getUniqueId(), (k, freeze) -> new FreezeElement(freeze.getFreezer(), freeze.getReason(), controlLocation));

		frozenPlayer.teleport(controlLocation);
		((Player) sender).teleport(controlLocation);
	}

	private void handleFreezeAllCommand(CommandSender sender, String[] args)
	{
		if(!sender.hasPermission("zefreeze.freeze"))
		{
			sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
			return;
		}

		Bukkit.getOnlinePlayers().stream().filter(player -> !player.getName().equals(sender.getName())).forEach(player -> toggleFreeze(sender, args, player));
	}

	private boolean handleUnfreezeCommand(CommandSender sender, String[] args)
	{
		if(!sender.hasPermission("zefreeze.freeze") && !sender.hasPermission("zefreeze.unfreeze"))
		{
			sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
			return true;
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

		if(!offlinePlayer.hasPlayedBefore())
		{
			sendMessage(sender, messages.getStringList("errors.player-not-played-before"), prefix, "{player}", args[0]);
			return true;
		}

		return toggleFreeze(sender, args, offlinePlayer);
	}

	private boolean toggleFreeze(CommandSender sender, String[] args, OfflinePlayer offlinePlayer)
	{
		if(offlinePlayer.getName().equals(sender.getName()))
		{
			sendMessage(sender, messages.getStringList("errors.self-freeze"), prefix);
			return true;
		}

		UUID playerUUID = offlinePlayer.getUniqueId();

		if(!playerFrozen.containsKey(playerUUID))
		{
			freezePlayer(sender, args, offlinePlayer);
			return true;
		}

		if(!config.getBoolean("freeze-toggle"))
		{
			sendMessage(sender, messages.getStringList("errors.player-already-frozen"), prefix, "{player}", offlinePlayer.getName());
			return true;
		}

		unfreezePlayer(sender, offlinePlayer);
		return true;
	}

	private void unfreezePlayer(CommandSender sender, OfflinePlayer offlinePlayer)
	{
		playerFrozen.remove(offlinePlayer.getUniqueId());
		sendMessage(sender, messages.getStringList("player-unfrozen"), prefix, "{player}", offlinePlayer.getName());

		if(!offlinePlayer.isOnline())
		{
			return;
		}

		Player player = (Player) offlinePlayer;
		player.closeInventory();
		SoundUtils.playPlayerSound(instance, player, player.getLocation(), config.getString("unfreeze-sound"), 1, 1);
		sendMessage(player, messages.getStringList("target-unfrozen"), prefix, "{freezer}", sender.getName());
	}

	private void freezePlayer(CommandSender sender, String[] args, OfflinePlayer offlinePlayer)
	{
		String reason = args.length >= 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : messages.getString("no-reason", "No reason");
		Location location = offlinePlayer.isOnline() ? offlinePlayer.getPlayer().getLocation() : config.getLocation("control-location");

		playerFrozen.put(offlinePlayer.getUniqueId(), new FreezeElement(sender.getName(), reason, location));
		sendMessage(sender, messages.getStringList("player-frozen"), prefix, "{player}", offlinePlayer.getName(), "{reason}", reason);

		if(!offlinePlayer.isOnline())
		{
			return;
		}

		Player player = (Player) offlinePlayer;

		if(config.getBoolean("anti-disconnection-gui.enabled"))
		{
			player.openInventory(antiDisconnectionGUI.buildInventory());
		}

		SoundUtils.playPlayerSound(instance, player, location, config.getString("freeze-sound"), 1, 1);
		sendMessage(player, messages.getStringList("target-frozen"), prefix, "{freezer}", sender.getName(), "{reason}", reason);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		List<String> completions = new ArrayList<>();
		List<String> options = new ArrayList<>(List.of("help", "info"));

		if(sender.hasPermission("zefreeze.reload"))
		{
			options.addAll(List.of("reload", "control", "@a", "all"));
			options.addAll(instance.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
		}

		if(args.length == 1)
		{
			StringUtil.copyPartialMatches(args[0], options, completions);
		}
		else if(args.length == 2 && args[0].equalsIgnoreCase("info"))
		{
			completions.addAll(playerFrozen.keySet().stream().map(Bukkit::getOfflinePlayer).map(OfflinePlayer::getName).toList());
		}
		else if(args.length == 2 && args[0].equalsIgnoreCase("control"))
		{
			StringUtil.copyPartialMatches(args[1], List.of("set"), completions);
		}

		Collections.sort(completions);
		return completions;
	}
}
