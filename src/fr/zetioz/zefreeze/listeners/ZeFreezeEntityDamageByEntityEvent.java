package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezeMain;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeEntityDamageByEntityEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private YamlConfiguration messages;
	private YamlConfiguration config;
	private String prefix;

	public ZeFreezeEntityDamageByEntityEvent(final ZeFreezeMain instance) throws FileNotFoundException
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
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof final Player victim
			&& e.getDamager() instanceof final Player damager)
		{
			if(instance.getPlayerFrozen().containsKey(victim.getUniqueId())
				&& config.getBoolean("disable-damages", false))
			{
				e.setCancelled(true);
				sendMessage(damager, messages.getStringList("errors.damaged-a-frozen-player"), prefix);
			}
		}
		else if(e.getDamager() instanceof final Player damager)
		{
			if(instance.getPlayerFrozen().containsKey(damager.getUniqueId())
				&& config.getBoolean("disable-damages", false))
			{
				e.setCancelled(true);
				sendMessage(damager, messages.getStringList("errors.damage-while-frozen"), prefix);
			}
		}
	}}
