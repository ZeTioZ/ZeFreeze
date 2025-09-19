package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeEntityDamageByEntityListener implements Listener, FilesManagerUtils.ReloadableFiles {
	private final ZeFreezePlugin instance;
	private YamlConfiguration messages;
	private YamlConfiguration config;
	private String prefix;

	public ZeFreezeEntityDamageByEntityListener(final ZeFreezePlugin instance) throws FileNotFoundException {
		this.instance = instance;
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException {
		messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
		config = instance.getFilesManagerUtils().getSimpleYaml("config");
		prefix = messages.getString("prefix", "&c[&6ZeFreeze&c] &r");
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof final Player victim)
		{
			if (!instance.getPlayerFrozen().containsKey(victim.getUniqueId())
					|| !config.getBoolean("disable-damages", false)) return;

			event.setCancelled(true);
			if (event.getDamager() instanceof final Player damager)
			{
				sendMessage(damager, messages.getStringList("errors.damaged-a-frozen-player"), prefix);
			}
			return;
		}

		if (event.getDamager() instanceof final Player damager)
		{
			if (!instance.getPlayerFrozen().containsKey(damager.getUniqueId())
					|| !config.getBoolean("disable-damages", false)) return;

			event.setCancelled(true);
			sendMessage(damager, messages.getStringList("errors.damage-while-frozen"), prefix);
		}

	}
}
