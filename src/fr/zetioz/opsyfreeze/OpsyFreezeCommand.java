package fr.zetioz.opsyfreeze;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.zetioz.opsyfreeze.object.Freeze;
import fr.zetioz.opsyfreeze.utils.ColorUtils;

public class OpsyFreezeCommand implements CommandExecutor {

	private OpsyFreezeMain main;
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private Map<UUID, Freeze> playerFrozen;
	private String prefix;

	public OpsyFreezeCommand(OpsyFreezeMain main)
	{
		this.main = main;
		this.messagesFile = main.getFilesManager().getMessagesFile();
		this.configsFile = main.getFilesManager().getConfigsFile();
		this.playerFrozen = main.getPlayerFrozen();
		this.prefix = ColorUtils.color(messagesFile.getString("prefix"));
	}
	
	public void setPlayerFrozen(Map<UUID, Freeze> playerFrozen)
	{
		this.playerFrozen = playerFrozen;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("opsyfreeze"))
		{
			if(args.length == 0)
			{
				ColorUtils.sendMessage(sender, messagesFile.getStringList("help-page"), prefix);
			}
			else if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("help"))
				{
					ColorUtils.sendMessage(sender, messagesFile.getStringList("help-page"), prefix);
				}
				else if(args[0].equalsIgnoreCase("info"))
				{
					if(sender.hasPermission("opsyfreeze.info"))
					{						
						UUID playerUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
						if(playerFrozen.containsKey(playerUUID))
						{
							DecimalFormat df = new DecimalFormat("#.##");
							ColorUtils.sendMessage(sender, messagesFile.getStringList("target-freeze-info"), prefix, "{freezer}", playerFrozen.get(playerUUID).getFreezer()
																												   , "{reason}", playerFrozen.get(playerUUID).getReason()
																												   , "{loc_x}", df.format(playerFrozen.get(playerUUID).getLocation().getX())
																												   , "{loc_y}", df.format(playerFrozen.get(playerUUID).getLocation().getY())
																												   , "{loc_z}", df.format(playerFrozen.get(playerUUID).getLocation().getZ()));
						}
						else
						{
							ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-not-frozen"), prefix, "{player}", args[1]);
						}
					}
					else
					{
						ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else if(args[0].equalsIgnoreCase("reload"))
				{
					if(sender.hasPermission("opsyfreeze.reload"))
					{						
						Bukkit.getPluginManager().disablePlugin(main);
						Bukkit.getPluginManager().enablePlugin(main);
						ColorUtils.sendMessage(sender, messagesFile.getStringList("plugin-reload"), prefix);
					}
					else
					{
						ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else if(args[0].equalsIgnoreCase("control"))
				{
					if(!(sender instanceof Player))
					{
						ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-only"), prefix);
						return true;
					}
					final Player senderPlayer = (Player) sender;
					if(sender.hasPermission("opsyfreeze.control.set"))
					{
						if(args[1] != null && args[1].equalsIgnoreCase("set"))
						{
							configsFile.set("control-location.world", senderPlayer.getLocation().getWorld().getName());
							configsFile.set("control-location.x", senderPlayer.getLocation().getBlockX());
							configsFile.set("control-location.y", senderPlayer.getLocation().getBlockY());
							configsFile.set("control-location.z", senderPlayer.getLocation().getBlockZ());
							try
							{
								configsFile.save(new File(main.getPlugin().getDataFolder(), "configs.yml"));
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
							return true;
						}
					}
					if(sender.hasPermission("opsyfreeze.control"))
					{
						if(!configsFile.isConfigurationSection("control-location"))
						{
							ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.control-location-not-set"), prefix);
							return true;
						}
						if(!Bukkit.getOfflinePlayer(args[0]).isOnline())
						{
							ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-offline"), prefix);
							return true;
						}
						final Player frozenPlayer = Bukkit.getPlayer(args[0]);
						if(!playerFrozen.containsKey(frozenPlayer.getUniqueId()))
						{
							ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-not-frozen"), prefix, "{player}", args[0]);
							return true;
						}
						final String freezer = playerFrozen.get(frozenPlayer.getUniqueId()).getFreezer();
						final String reason = playerFrozen.get(frozenPlayer.getUniqueId()).getReason();
						playerFrozen.remove(frozenPlayer.getUniqueId());
						final String controlWorld = configsFile.getString("control-location.world");
						final double controlX = configsFile.getDouble("control-location.x");
						final double controlY = configsFile.getDouble("control-location.y");
						final double controlZ = configsFile.getDouble("control-location.z");
						final Location controlLocation = new Location(Bukkit.getWorld(controlWorld), Math.round(controlX), Math.round(controlY), Math.round(controlZ));
						frozenPlayer.teleport(controlLocation);
						playerFrozen.put(frozenPlayer.getUniqueId(), new Freeze(freezer, reason, controlLocation));
						((Player) sender).teleport(controlLocation);
						return true;
					}
					ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.not-enough-permissions"), prefix);
					return true;
				}
				else if(Bukkit.getOfflinePlayer(args[0]).isOnline())
				{
					if(sender.hasPermission("opsyfreeze.freeze"))
					{	
						Player player = Bukkit.getPlayer(args[0]);
						if(!player.getName().equals(sender.getName()))
						{
							if(playerFrozen.containsKey(Bukkit.getPlayer(args[0]).getUniqueId()))
							{
								if(!configsFile.getBoolean("freeze-toggle"))
								{
									ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-already-frozen"), prefix, "{player}", player.getName());
									return false;
								}
								playerFrozen.remove(player.getUniqueId());
								try
								{
									player.playSound(player.getLocation(), Sound.valueOf(configsFile.getString("unfreeze-sound")), 5, 5);
								}
								catch(IllegalArgumentException ex)
								{
									for(String line : messagesFile.getStringList("errors.invalid-sound"))
									{
										line = line.replace("{sound}", configsFile.getString("unfreeze-sound"));
										line = ChatColor.translateAlternateColorCodes('&', line);
										main.getLogger().warning(line);
									}
								}
								ColorUtils.sendMessage(sender, messagesFile.getStringList("player-unfrozen"), prefix, "{player}", player.getName());
								ColorUtils.sendMessage(player, messagesFile.getStringList("target-unfrozen"), prefix, "{freezer}", sender.getName());
							}
							else
							{
								String reason;
								if(args.length >= 2)
								{
									StringBuilder reasonBuilder = new StringBuilder();
									String[] argsToKeep = Arrays.copyOfRange(args, 1, args.length);
									boolean i = true;
									for(String arg : argsToKeep)
									{
										if(i)
										{
											reasonBuilder.append(arg);
											i = false;
										}
										else
										{
											reasonBuilder.append(" " + arg);
										}
									}
									reason = reasonBuilder.toString();
								}
								else
								{
									reason =  messagesFile.getString("no-reason");
								}
								playerFrozen.put(player.getUniqueId(), new Freeze(sender.getName(), reason, player.getLocation()));
								try
								{
									player.playSound(player.getLocation(), Sound.valueOf(configsFile.getString("freeze-sound")), 5, 5);
								}
								catch(IllegalArgumentException ex)
								{
									for(String line : messagesFile.getStringList("errors.invalid-sound"))
									{
										line = line.replace("{sound}", configsFile.getString("freeze-sound"));
										line = ChatColor.translateAlternateColorCodes('&', line);
										main.getLogger().warning(line);
									}
								}
								ColorUtils.sendMessage(sender, messagesFile.getStringList("player-frozen"), prefix, "{player}", player.getName()
																												  , "{reason}", reason);
								ColorUtils.sendMessage(sender, messagesFile.getStringList("target-frozen"), prefix, "{freezer}", sender.getName()
																												  , "{reason}", reason);
							}
						}
						else
						{
							ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.self-freeze"), prefix);
						}
					}
					else
					{
						ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else
				{
					ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-offline"), prefix);
				}
			}
		}
		else if(cmd.getName().equalsIgnoreCase("opsyunfreeze"))
		{
			if(args.length == 0)
			{
				ColorUtils.sendMessage(sender, messagesFile.getStringList("help-page"), prefix);
			}
			else if(args.length >= 1)
			{
				if(Bukkit.getOfflinePlayer(args[0]).isOnline())
				{
					if(sender.hasPermission("opsyfreeze.unfreeze"))
					{	
						Player player = Bukkit.getPlayer(args[0]);
						if(!player.getName().equals(sender.getName()))
						{
							if(!playerFrozen.containsKey(Bukkit.getPlayer(args[0]).getUniqueId()))
							{
								if(!configsFile.getBoolean("freeze-toggle"))
								{
									ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-not-frozen"), prefix, "{player}", player.getName());
									return false;
								}
								String reason;
								if(args.length >= 2)
								{
									StringBuilder reasonBuilder = new StringBuilder();
									String[] argsToKeep = Arrays.copyOfRange(args, 1, args.length);
									boolean i = true;
									for(String arg : argsToKeep)
									{
										if(i)
										{
											reasonBuilder.append(arg);
											i = false;
										}
										else
										{
											reasonBuilder.append(" " + arg);
										}
									}
									reason = reasonBuilder.toString();
								}
								else
								{
									reason =  messagesFile.getString("no-reason");
								}
								playerFrozen.put(player.getUniqueId(), new Freeze(sender.getName(), reason, player.getLocation()));
								try
								{
									player.playSound(player.getLocation(), Sound.valueOf(configsFile.getString("freeze-sound")), 5, 5);
								}
								catch(IllegalArgumentException ex)
								{
									for(String line : messagesFile.getStringList("errors.invalid-sound"))
									{
										line = line.replace("{sound}", configsFile.getString("freeze-sound"));
										line = ChatColor.translateAlternateColorCodes('&', line);
										main.getLogger().warning(line);
									}
								}
								ColorUtils.sendMessage(sender, messagesFile.getStringList("player-frozen"), prefix, "{player}", player.getName()
																												  , "{reason}", reason);
								ColorUtils.sendMessage(sender, messagesFile.getStringList("target-frozen"), prefix, "{freezer}", sender.getName()
																												  , "{reason}", reason);
							}
							else
							{
								playerFrozen.remove(player.getUniqueId());
								try
								{
									player.playSound(player.getLocation(), Sound.valueOf(configsFile.getString("unfreeze-sound")), 5, 5);
								}
								catch(IllegalArgumentException ex)
								{
									for(String line : messagesFile.getStringList("errors.invalid-sound"))
									{
										line = line.replace("{sound}", configsFile.getString("unfreeze-sound"));
										line = ChatColor.translateAlternateColorCodes('&', line);
										main.getLogger().warning(line);
									}
								}
								ColorUtils.sendMessage(sender, messagesFile.getStringList("player-unfrozen"), prefix, "{player}", player.getName());
								ColorUtils.sendMessage(sender, messagesFile.getStringList("target-unfrozen"), prefix, "{freezer}", sender.getName());
							}
						}
						else
						{
							ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.self-freeze"), prefix);
						}
					}
					else
					{
						ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.not-enough-permissions"), prefix);
					}
				}
				else
				{
					ColorUtils.sendMessage(sender, messagesFile.getStringList("errors.player-offline"), prefix);
				}
			}
		}
		return false;
	}
}
