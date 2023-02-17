package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezeMain;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeBlockPlaceEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private YamlConfiguration messages;
	private YamlConfiguration config;
	private String prefix;

	public ZeFreezeBlockPlaceEvent(final ZeFreezeMain instance) throws FileNotFoundException
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
	public void onBlockPlace(BlockPlaceEvent e)
	{
		final Player player = e.getPlayer();
		if(instance.getPlayerFrozen().containsKey(player.getUniqueId())
			&& config.getBoolean("disable-block-place", false))
		{
			e.setCancelled(true);
			sendMessage(player, messages.getStringList("errors.place-while-frozen"), prefix);
		}
	}
}
