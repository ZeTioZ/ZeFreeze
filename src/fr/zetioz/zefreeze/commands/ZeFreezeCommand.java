package fr.zetioz.zefreeze.commands;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.coreutils.SoundUtils;
import fr.zetioz.zefreeze.ZeFreezeMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import fr.zetioz.zefreeze.object.Freeze;
import org.bukkit.util.StringUtil;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeCommand implements TabExecutor, FilesManagerUtils.ReloadableFiles
{

	private ZeFreezeMain instance;
	private YamlConfiguration messages;
	private YamlConfiguration config;
	private Map<UUID, Freeze> playerFrozen;
	private String prefix;

	public ZeFreezeCommand(ZeFreezeMain instance)
	{
		this.instance = instance;
		this.playerFrozen = instance.getPlayerFrozen();
	}
	
	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		this.messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
		this.config = instance.getFilesManagerUtils().getSimpleYaml("config");
		this.prefix = messages.getString("prefix");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("zefreeze"))
		{
			if(args.length == 0)
			{
				sendMessage(sender, messages.getStringList("help-page"), prefix);
			}
			else if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("help"))
				{
					sendMessage(sender, messages.getStringList("help-page"), prefix);
				}
				else if(args[0].equalsIgnoreCase("info"))
				{
					if(sender.hasPermission("zefreeze.info"))
					{						
						final UUID playerUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
						if(playerFrozen.containsKey(playerUUID))
						{
							DecimalFormat df = new DecimalFormat("#.##");
							sendMessage(sender, messages.getStringList("target-freeze-info"), prefix, "{freezer}", playerFrozen.get(playerUUID).getFreezer()
																												   , "{reason}", playerFrozen.get(playerUUID).getReason()
																												   , "{loc_x}", df.format(playerFrozen.get(playerUUID).getLocation().getX())
																												   , "{loc_y}", df.format(playerFrozen.get(playerUUID).getLocation().getY())
																												   , "{loc_z}", df.format(playerFrozen.get(playerUUID).getLocation().getZ()));
						}
						else
						{
							sendMessage(sender, messages.getStringList("errors.player-not-frozen"), prefix, "{player}", args[1]);
						}
					}
					else
					{
						sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else if(args[0].equalsIgnoreCase("reload"))
				{
					if(sender.hasPermission("zefreeze.reload"))
					{
						sendMessage(sender, messages.getStringList("plugin-reload"), prefix);
						instance.getFilesManagerUtils().reloadAllSimpleYaml();
					}
					else
					{
						sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else if(args[0].equalsIgnoreCase("control"))
				{
					if(!(sender instanceof Player))
					{
						sendMessage(sender, messages.getStringList("errors.player-only"), prefix);
						return true;
					}
					final Player senderPlayer = (Player) sender;
					if(sender.hasPermission("zefreeze.control.set"))
					{
						if(args[1] != null && args[1].equalsIgnoreCase("set"))
						{
							config.set("control-location.world", senderPlayer.getLocation().getWorld().getName());
							config.set("control-location.x", senderPlayer.getLocation().getBlockX());
							config.set("control-location.y", senderPlayer.getLocation().getBlockY());
							config.set("control-location.z", senderPlayer.getLocation().getBlockZ());
							instance.getFilesManagerUtils().saveSimpleYaml("config");
							return true;
						}
					}
					if(sender.hasPermission("zefreeze.control"))
					{
						if(!config.isConfigurationSection("control-location"))
						{
							sendMessage(sender, messages.getStringList("errors.control-location-not-set"), prefix);
							return true;
						}
						if(!Bukkit.getOfflinePlayer(args[0]).isOnline())
						{
							sendMessage(sender, messages.getStringList("errors.player-offline"), prefix);
							return true;
						}
						final Player frozenPlayer = Bukkit.getPlayer(args[0]);
						if(!playerFrozen.containsKey(frozenPlayer.getUniqueId()))
						{
							sendMessage(sender, messages.getStringList("errors.player-not-frozen"), prefix, "{player}", args[0]);
							return true;
						}
						final Freeze freeze = playerFrozen.get(frozenPlayer.getUniqueId());
						final String freezer = freeze.getFreezer();
						final String reason = freeze.getReason();
						playerFrozen.remove(frozenPlayer.getUniqueId());
						final String controlWorld = config.getString("control-location.world");
						final double controlX = config.getDouble("control-location.x");
						final double controlY = config.getDouble("control-location.y");
						final double controlZ = config.getDouble("control-location.z");
						final Location controlLocation = new Location(Bukkit.getWorld(controlWorld), Math.round(controlX), Math.round(controlY), Math.round(controlZ));
						frozenPlayer.teleport(controlLocation);
						playerFrozen.put(frozenPlayer.getUniqueId(), new Freeze(freezer, reason, controlLocation));
						((Player) sender).teleport(controlLocation);
						return true;
					}
					sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
					return true;
				}
				else if(Bukkit.getOfflinePlayer(args[0]).isOnline())
				{
					if(sender.hasPermission("zefreeze.freeze"))
					{	
						final Player player = Bukkit.getPlayer(args[0]);
						if(!player.getName().equals(sender.getName()))
						{
							if(playerFrozen.containsKey(player.getUniqueId()))
							{
								if(!config.getBoolean("freeze-toggle"))
								{
									sendMessage(sender, messages.getStringList("errors.player-already-frozen"), prefix, "{player}", player.getName());
									return false;
								}
								playerFrozen.remove(player.getUniqueId());
								SoundUtils.playPlayerSound(instance, player, player.getLocation(), config.getString("unfreeze-sound"), 1, 1);
								sendMessage(sender, messages.getStringList("player-unfrozen"), prefix, "{player}", player.getName());
								sendMessage(player, messages.getStringList("target-unfrozen"), prefix, "{freezer}", sender.getName());
							}
							else
							{
								String reason = "";
								if(args.length >= 2)
								{
									reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
								}
								else
								{
									reason =  messages.getString("no-reason", "No reason");
								}
								playerFrozen.put(player.getUniqueId(), new Freeze(sender.getName(), reason, player.getLocation()));
								SoundUtils.playPlayerSound(instance, player, player.getLocation(), config.getString("freeze-sound"), 1, 1);
								sendMessage(sender, messages.getStringList("player-frozen"), prefix, "{player}", player.getName()
																													, "{reason}", reason);
								sendMessage(sender, messages.getStringList("target-frozen"), prefix, "{freezer}", sender.getName()
																												    , "{reason}", reason);
							}
						}
						else
						{
							sendMessage(sender, messages.getStringList("errors.self-freeze"), prefix);
						}
					}
					else
					{
						sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else
				{
					sendMessage(sender, messages.getStringList("errors.player-offline"), prefix);
				}
			}
		}
		else if(cmd.getName().equalsIgnoreCase("zeunfreeze"))
		{
			if(args.length == 0)
			{
				sendMessage(sender, messages.getStringList("help-page"), prefix);
			}
			else
			{
				if(Bukkit.getOfflinePlayer(args[0]).isOnline())
				{
					if(sender.hasPermission("zefreeze.unfreeze"))
					{	
						final Player player = Bukkit.getPlayer(args[0]);
						if(!player.getName().equals(sender.getName()))
						{
							if(!playerFrozen.containsKey(player.getUniqueId()))
							{
								if(!config.getBoolean("freeze-toggle"))
								{
									sendMessage(sender, messages.getStringList("errors.player-not-frozen"), prefix, "{player}", player.getName());
									return false;
								}
								String reason;
								if(args.length >= 2)
								{
									reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
								}
								else
								{
									reason =  messages.getString("no-reason", "No reason");
								}
								playerFrozen.put(player.getUniqueId(), new Freeze(sender.getName(), reason, player.getLocation()));
								SoundUtils.playPlayerSound(instance, player, player.getLocation(), config.getString("freeze-sound"), 1, 1);
								sendMessage(sender, messages.getStringList("player-frozen"), prefix, "{player}", player.getName()
																												  , "{reason}", reason);
								sendMessage(sender, messages.getStringList("target-frozen"), prefix, "{freezer}", sender.getName()
																												  , "{reason}", reason);
							}
							else
							{
								playerFrozen.remove(player.getUniqueId());
								SoundUtils.playPlayerSound(instance, player, player.getLocation(), config.getString("unfreeze-sound"), 1, 1);
								sendMessage(sender, messages.getStringList("player-unfrozen"), prefix, "{player}", player.getName());
								sendMessage(sender, messages.getStringList("target-unfrozen"), prefix, "{freezer}", sender.getName());
							}
						}
						else
						{
							sendMessage(sender, messages.getStringList("errors.self-freeze"), prefix);
						}
					}
					else
					{
						sendMessage(sender, messages.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else
				{
					sendMessage(sender, messages.getStringList("errors.player-offline"), prefix);
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args)
	{
		if (command.getName().equalsIgnoreCase("zefreeze"))
		{
			final List<String> firstArgList = new ArrayList<>(List.of("help", "info"));
			final List<String> completions = new ArrayList<>();

			if(sender.hasPermission("zefreeze.reload")) firstArgList.addAll(List.of("reload", "control", "freeze"));

			if (args.length == 1)
			{
				StringUtil.copyPartialMatches(args[0], firstArgList, completions);
			}
			else if (args.length == 2 && args[0].equalsIgnoreCase("control"))
			{
				StringUtil.copyPartialMatches(args[1], List.of("set"), completions);
			}
			Collections.sort(completions);
			return completions;
		}
		return new ArrayList<>();
	}
}
