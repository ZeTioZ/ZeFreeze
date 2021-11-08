package fr.zetioz.opsyfreeze;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.zetioz.opsyfreeze.events.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.zetioz.opsyfreeze.object.Freeze;
import fr.zetioz.opsyfreeze.utils.FilesManager;

public class OpsyFreezeMain extends JavaPlugin
{
	private Plugin plugin;
	private FilesManager filesManager;
	private Map<UUID, Freeze> playerFrozen;
	
	@Override
	public void onEnable()
	{
		this.plugin = this;
		this.filesManager = new FilesManager(this);
		filesManager.createConfigsFile();
		filesManager.createMessagesFile();
		
		playerFrozen = new HashMap<>();
		
		final OpsyFreezeCommand ofc = new OpsyFreezeCommand(this);
		
		registerEvents(this, new OpsyFreezePlayerMoveEvent(this)
								 , new OpsyFreezePlayerQuitEvent(this)
								 , new OpsyFreezeBlockBreakEvent(this)
								 , new OpsyFreezeBlockPlaceEvent(this)
								 , new OpsyFreezePlayerInteractEvent(this)
								 , new OpsyFreezeEntityDamageByEntityEvent(this));
		
		getCommand("opsyfreeze").setExecutor(ofc);
		getCommand("opsyunfreeze").setExecutor(ofc);
			
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
	
	public FilesManager getFilesManager()
	{
		return this.filesManager;
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

	public void setPlayerFrozen(Map<UUID, Freeze> playerFrozen)
	{
		this.playerFrozen = playerFrozen;
	}
}
