package fr.zetioz.zefreeze.listeners;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

import fr.zetioz.coreutils.FilesManagerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.zetioz.zefreeze.ZeFreezeMain;
import fr.zetioz.zefreeze.object.Freeze;

import static fr.zetioz.coreutils.ColorUtils.*;

public class ZeFreezePlayerQuitEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private Map<UUID, Freeze> playerFrozen;
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private String prefix;
	
	public ZeFreezePlayerQuitEvent(ZeFreezeMain instance) throws FileNotFoundException
	{
		this.instance = instance;
		playerFrozen = instance.getPlayerFrozen();
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		this.messagesFile = instance.getFilesManagerUtils().getSimpleYaml("messages");
		this.configsFile = instance.getFilesManagerUtils().getSimpleYaml("config");
		this.prefix = messagesFile.getString("prefix");
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e)
	{
		final Player player = e.getPlayer();
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
				parsePlaceholders(color(messagesFile.getStringList("unfreeze-disconnect")), "{player}", player.getName()).forEach(instance.getLogger()::info);
			}
			for(Player online : Bukkit.getServer().getOnlinePlayers())
			{
				if(online.hasPermission("zefreeze.disconnect.alert"))
				{
					sendMessage(online, messagesFile.getStringList("staff-disconnect-alert"), prefix, "{player}", player.getName());
				}
			}
			parsePlaceholders(color(messagesFile.getStringList("staff-disconnect-alert")), "{player}", player.getName()).forEach(instance.getLogger()::info);
		}
	}
}
