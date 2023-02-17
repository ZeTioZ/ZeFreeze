package fr.zetioz.zefreeze;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.commands.ZeFreezeCommand;
import fr.zetioz.zefreeze.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.zetioz.zefreeze.object.Freeze;

public class ZeFreezeMain extends JavaPlugin
{
	private Plugin plugin;
	private FilesManagerUtils filesManagerUtils;
	private Map<UUID, Freeze> playerFrozen;
	
	@Override
	public void onEnable()
	{
		this.plugin = this;
		this.filesManagerUtils = new FilesManagerUtils(this);
		filesManagerUtils.createSimpleYaml("config");
		filesManagerUtils.createSimpleYaml("messages");

		playerFrozen = new HashMap<>();

		try
		{
			final ZeFreezeCommand zeFreezeCommand = new ZeFreezeCommand(this);
			registerEvents(this, new ZeFreezePlayerMoveEvent(this)
									, new ZeFreezePlayerQuitEvent(this)
									, new ZeFreezeBlockBreakEvent(this)
									, new ZeFreezeBlockPlaceEvent(this)
									, new ZeFreezePlayerInteractEvent(this)
									, new ZeFreezeEntityDamageByEntityEvent(this));
			getCommand("zefreeze").setExecutor(zeFreezeCommand);
			getCommand("zeunfreeze").setExecutor(zeFreezeCommand);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable()
	{
		this.plugin = null;
	}
	
	public Plugin getPlugin()
	{
		return this.plugin;
	}
	
	public FilesManagerUtils getFilesManagerUtils()
	{
		return this.filesManagerUtils;
	}
	
	private void registerEvents(Plugin plugin, Listener... listeners)
	{
		for(Listener listener : listeners)
		{
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		}
	}

	public Map<UUID, Freeze> getPlayerFrozen()
	{
		return playerFrozen;
	}
}
