package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezeMain;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeBlockBreakEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private YamlConfiguration messages;
	private YamlConfiguration config;
	private String prefix;

	public ZeFreezeBlockBreakEvent(final ZeFreezeMain instance) throws FileNotFoundException
	{
		this.instance = instance;
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
		config = instance.getFilesManagerUtils().getSimpleYaml("config");
		prefix = messages.getString("prefix", "&c[&6ZeFreeze&c] &r");
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		final Player player = e.getPlayer();
		if(instance.getPlayerFrozen().containsKey(player.getUniqueId())
			&& config.getBoolean("disable-block-break", false))
		{
			e.setCancelled(true);
			sendMessage(player, messages.getStringList("errors.break-while-frozen"), prefix);
		}
	}
}
