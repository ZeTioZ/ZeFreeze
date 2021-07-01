package fr.zetioz.opsyfreeze.events;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.zetioz.opsyfreeze.OpsyFreezeMain;
import fr.zetioz.opsyfreeze.object.Freeze;
import fr.zetioz.opsyfreeze.utils.ColorUtils;

public class OpsyFreezePlayerQuitEvent implements Listener
{
	private final OpsyFreezeMain main;
	private final YamlConfiguration messagesFile;
	private final YamlConfiguration configsFile;
	private Map<UUID, Freeze> playerFrozen;
	private final String prefix;
	
	public OpsyFreezePlayerQuitEvent(OpsyFreezeMain main)
	{
		this.main = main;
		messagesFile = main.getFilesManager().getMessagesFile();
		configsFile = main.getFilesManager().getConfigsFile();
		playerFrozen = main.getPlayerFrozen();
		prefix = ColorUtils.color(messagesFile.getString("prefix"));
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
			}
			for(Player online : Bukkit.getServer().getOnlinePlayers())
			{
				if(online.hasPermission("opsyfreeze.disconnect.alert"))
				{
					ColorUtils.sendMessage(online, messagesFile.getStringList("staff-disconnect-alert"), prefix, "{player}", player.getName());
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
