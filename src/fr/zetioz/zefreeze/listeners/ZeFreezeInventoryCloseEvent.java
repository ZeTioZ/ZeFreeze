package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezeMain;
import fr.zetioz.zefreeze.guis.AntiDisconnectionGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.FileNotFoundException;
import java.util.UUID;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeInventoryCloseEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private final AntiDisconnectionGUI antiDisconnectionGUI;
	private YamlConfiguration messages;
	private String prefix;

	public ZeFreezeInventoryCloseEvent(ZeFreezeMain instance) throws FileNotFoundException
	{
		this.instance = instance;
		this.antiDisconnectionGUI = new AntiDisconnectionGUI(instance);
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
		prefix = messages.getString("prefix");
	}

	@EventHandler
	public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event)
	{
		if(!(event.getPlayer() instanceof final Player player)) return;

		final UUID playerUUID = player.getUniqueId();
		if(event.getView().getTopInventory().getHolder() instanceof AntiDisconnectionGUI
			&& instance.getPlayerFrozen().containsKey(playerUUID))
		{
			sendMessage(player, messages.getStringList("errors.close-anti-disconnection-gui"), prefix);
			Bukkit.getScheduler().runTaskLater(instance, () -> player.openInventory(antiDisconnectionGUI.buildInventory()), 2L);
		}
	}
}
