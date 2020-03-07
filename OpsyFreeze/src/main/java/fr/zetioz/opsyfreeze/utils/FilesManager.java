package fr.zetioz.opsyfreeze.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.zetioz.opsyfreeze.OpsyFreezeMain;

public class FilesManager
{
	
	private OpsyFreezeMain main;
	private Plugin plugin;
	
	public FilesManager(OpsyFreezeMain main)
	{
		this.main = main;
		this.plugin = this.main.getPlugin();
	}
	
	//region Configs File (Creator/Getter)
    private YamlConfiguration configsFileConfig;
    
    public YamlConfiguration getConfigsFile()
    {
        return this.configsFileConfig;
    }

    public void createConfigsFile()
    {
    	File configsFile = new File(plugin.getDataFolder(), "configs.yml");
        if (!configsFile.exists())
        {
        	configsFile.getParentFile().mkdirs();
        	plugin.saveResource("configs.yml", false);
        }

        configsFileConfig = new YamlConfiguration();
        try
        {
        	configsFileConfig.load(configsFile);
        }
        catch (IOException | InvalidConfigurationException e)
        {
        	plugin.getLogger().severe("An error occured while loading the configs file!");
        	e.printStackTrace();
        }
    }
    
    //endregion
    
    //region Message File (Creator/Getter)
    private YamlConfiguration messagesFileConfig;

    public YamlConfiguration getMessagesFile()
    {
        return this.messagesFileConfig;
    }
    
	public void createMessagesFile()
	{
		File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		if(!messagesFile.exists())
		{
			messagesFile.getParentFile().mkdir();
			plugin.saveResource("messages.yml", false);
		}
		
		messagesFileConfig = new YamlConfiguration();
		try
	    {
			messagesFileConfig.load(messagesFile);
	    }
	    catch (IOException | InvalidConfigurationException e) {
	    	plugin.getLogger().severe("An error occured while loading the messages file!");
	    	e.printStackTrace();
	    }
	}
	//endregion
}