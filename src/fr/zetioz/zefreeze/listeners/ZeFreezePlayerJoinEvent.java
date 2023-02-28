package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezeMain;
import fr.zetioz.zefreeze.guis.AntiDisconnectionGUI;
import fr.zetioz.zefreeze.object.Freeze;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

public class ZeFreezePlayerJoinEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private final AntiDisconnectionGUI antiDisconnectionGUI;
	private Map<UUID, Freeze> playerFrozen;
	private YamlConfiguration config;

	public ZeFreezePlayerJoinEvent(ZeFreezeMain instance) throws FileNotFoundException
	{
		this.instance = instance;
		this.antiDisconnectionGUI = new AntiDisconnectionGUI(instance);
		playerFrozen = instance.getPlayerFrozen();
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		this.config = instance.getFilesManagerUtils().getSimpleYaml("config");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		final Player player = e.getPlayer();
		if(playerFrozen.containsKey(player.getUniqueId()))
		{
			if(config.getBoolean("anti-disconnection-gui.enabled")) player.openInventory(antiDisconnectionGUI.getInventory());
		}
	}
}
