package fr.zetioz.zefreeze;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.commands.ZeFreezeCommand;
import fr.zetioz.zefreeze.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZeFreezePlugin extends JavaPlugin {
	private FilesManagerUtils filesManagerUtils;
	private Map<UUID, FreezeElement> playerFrozen;

	@Override
	public void onEnable() {
		this.filesManagerUtils = new FilesManagerUtils(this);
		filesManagerUtils.createSimpleYaml("config");
		filesManagerUtils.createSimpleYaml("messages");

		playerFrozen = new HashMap<>();

		try {
			final ZeFreezeCommand zeFreezeCommand = new ZeFreezeCommand(this);
			registerEvents(this,
					new ZeFreezePlayerMoveListener(this),
					new ZeFreezePlayerQuitListener(this),
					new ZeFreezeBlockBreakListener(this),
					new ZeFreezeBlockPlaceListener(this),
					new ZeFreezePlayerInteractListener(this),
					new ZeFreezeCommandPreProcessListener(this),
					new ZeFreezeEntityDamageByEntityListener(this),
					new ZeFreezeInventoryClickListener(this),
					new ZeFreezeInventoryCloseListener(this),
					new ZeFreezePlayerJoinListener(this),
					new ZeFreezeChatListener(this)
			);

			getCommand("zefreeze").setExecutor(zeFreezeCommand);
			getCommand("zeunfreeze").setExecutor(zeFreezeCommand);
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
	}

	public FilesManagerUtils getFilesManagerUtils() {
		return this.filesManagerUtils;
	}

	private void registerEvents(Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		}
	}

	public Map<UUID, FreezeElement> getPlayerFrozen() {
		return playerFrozen;
	}
}
