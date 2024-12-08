package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.FreezeElement;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import fr.zetioz.zefreeze.guis.AntiDisconnectionGUI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

public class ZeFreezePlayerJoinListener implements Listener, FilesManagerUtils.ReloadableFiles {
	private final ZeFreezePlugin instance;
	private final AntiDisconnectionGUI antiDisconnectionGUI;
	private Map<UUID, FreezeElement> playerFrozen;
	private YamlConfiguration config;

	public ZeFreezePlayerJoinListener(ZeFreezePlugin instance) throws FileNotFoundException {
		this.instance = instance;
		this.antiDisconnectionGUI = new AntiDisconnectionGUI(instance);
		playerFrozen = instance.getPlayerFrozen();
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException {
		this.config = instance.getFilesManagerUtils().getSimpleYaml("config");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (!playerFrozen.containsKey(player.getUniqueId())) return;
		if (!config.getBoolean("anti-disconnection-gui.enabled")) return;

		player.openInventory(antiDisconnectionGUI.getInventory());
	}
}
