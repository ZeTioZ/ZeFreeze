package fr.zetioz.opsyfreeze;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.zetioz.opsyfreeze.object.Freeze;

public class OpsyFreezeEvent implements Listener
{
	private OpsyFreezeMain main;
	private Map<UUID, Freeze> playerFrozen;
	private Map<UUID, Long> playerErrorCooldown;
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private String prefix;
	
	public OpsyFreezeEvent(OpsyFreezeMain main)
	{
		this.main = main;
		this.playerFrozen = new HashMap<>();
		this.playerErrorCooldown = new HashMap<>();
		this.messagesFile = this.main.getFilesManager().getMessagesFile();
		this.configsFile = this.main.getFilesManager().getConfigsFile();
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.messagesFile.getString("prefix"));
	}
	
	public void setPlayerFrozen(Map<UUID, Freeze> playerFrozen)
	{
		this.playerFrozen = playerFrozen;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		if(playerFrozen.containsKey(player.getUniqueId()))
		{
			Freeze playerFreeze = playerFrozen.get(player.getUniqueId());
			if(player.getLocation().getX() != playerFreeze.getLocation().getX()
				|| player.getLocation().getZ() != playerFreeze.getLocation().getZ()
				|| (configsFile.getBoolean("block-y-axis")
					&& player.getLocation().getY() != playerFreeze.getLocation().getY()))
			{
				player.teleport(playerFreeze.getLocation());
				if(!playerErrorCooldown.containsKey(player.getUniqueId())
					|| playerErrorCooldown.get(player.getUniqueId()) - System.currentTimeMillis() < 0)
				{					
					playerErrorCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (configsFile.getLong("freeze-message-cooldown") * 1000));
					for(String line : messagesFile.getStringList("errors.move-while-frozen"))
					{
						line = line.replace("{freezer}", playerFrozen.get(player.getUniqueId()).getFreezer());
						line = ChatColor.translateAlternateColorCodes('&', line);
						player.sendMessage(prefix + line);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		if(playerFrozen.containsKey(player.getUniqueId()))
		{
			if(configsFile.getBoolean("disconnect-action.enabled"))
			{
				for(String command : configsFile.getStringList("disconnect-action.commands"))
				{					
					command = command.replace("{player}", player.getName());
					command = command.replace("{freezer}", playerFrozen.get(player.getUniqueId()).getFreezer());
					command = command.replace("{reason}", playerFrozen.get(player.getUniqueId()).getReason());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}
			}
			if(configsFile.getBoolean("unfreeze-on-disconnect"))
			{
				playerFrozen.remove(player.getUniqueId());
				for(String line : messagesFile.getStringList("unfreeze-disconnect"))
				{
					line = line.replace("{player}", e.getPlayer().getName());
					line = ChatColor.translateAlternateColorCodes('&', line);
					main.getLogger().info(line);
				}
				main.getOFC().setPlayerFrozen(playerFrozen);
			}
			for(Player online : Bukkit.getServer().getOnlinePlayers())
			{
				if(online.hasPermission("opsyfreeze.disconnect.alert"))
				{
					for(String line : messagesFile.getStringList("staff-disconnect-alert"))
					{
						line = line.replace("{player}", player.getName());
						line = ChatColor.translateAlternateColorCodes('&', line);
						online.sendMessage(prefix + line);
					}
				}
			}
			for(String line : messagesFile.getStringList("staff-disconnect-alert"))
			{
				line = line.replace("{player}", player.getName());
				line = ChatColor.translateAlternateColorCodes('&', line);
				main.getLogger().info(prefix + line);
			}
		}
	}
}
