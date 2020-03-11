package fr.zetioz.opsyfreeze;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.zetioz.opsyfreeze.utils.FilesManager;

public class OpsyFreezeMain extends JavaPlugin
{
	private Plugin plugin;
	private FilesManager filesManager;
	private OpsyFreezeCommand ofc;
	private OpsyFreezeEvent ofe;
	
	@Override
	public void onEnable()
	{
		this.plugin = this;
		this.filesManager = new FilesManager(this);
		filesManager.createConfigsFile();
		filesManager.createMessagesFile();
		
		
		ofe = new OpsyFreezeEvent(this);
		ofc = new OpsyFreezeCommand(this);
		
		registerEvents(this, ofe);
		
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
	
	public OpsyFreezeCommand getOFC()
	{
		return this.ofc;
	}
	
	public OpsyFreezeEvent getOFE()
	{
		return this.ofe;
	}
	
	private void registerEvents(Plugin plugin, Listener... listeners)
	{
		for(Listener listener : listeners)
		{
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		}
	}
}
