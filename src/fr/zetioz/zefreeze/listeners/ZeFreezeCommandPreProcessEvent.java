package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezeMain;
import fr.zetioz.zefreeze.object.Freeze;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeCommandPreProcessEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private YamlConfiguration config;
	private YamlConfiguration messages;
	private String prefix;

	public ZeFreezeCommandPreProcessEvent(ZeFreezeMain instance) throws FileNotFoundException
	{
		this.instance = instance;
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		config = instance.getFilesManagerUtils().getSimpleYaml("config");
		messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
		prefix = messages.getString("prefix", "&c[&6ZeFreeze&c] &r");
	}

	@EventHandler
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event)
	{
		final String fullCommand = event.getMessage();
		final String[] args = fullCommand.split(" ");
		final Player player = event.getPlayer();

		if (args.length > 0
			&& config.getBoolean("block-commands.enabled")
			&& !config.getStringList("block-commands.whitelist").contains(args[0])
			&& instance.getPlayerFrozen().containsKey(player.getUniqueId()))
		{
			event.setCancelled(true);
			final Freeze freeze = instance.getPlayerFrozen().get(player.getUniqueId());
			sendMessage(player, messages.getStringList("errors.blocked-command"), prefix, "{player}", player.getName(),
					"{reason}", freeze.getReason(),
					"{freezer}", freeze.getFreezer(),
					"{command}", fullCommand);
		}
	}
}
