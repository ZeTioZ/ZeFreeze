package fr.zetioz.opsyfreeze.events;

import fr.zetioz.opsyfreeze.OpsyFreezeMain;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static fr.zetioz.opsyfreeze.utils.ColorUtils.color;
import static fr.zetioz.opsyfreeze.utils.ColorUtils.sendMessage;

public class OpsyFreezeEntityDamageByEntityEvent implements Listener
{
	private final OpsyFreezeMain instance;
	private final YamlConfiguration messages;
	private final YamlConfiguration config;
	private final String prefix;

	public OpsyFreezeEntityDamageByEntityEvent(final OpsyFreezeMain instance)
	{
		this.instance = instance;
		messages = instance.getFilesManager().getMessagesFile();
		config = instance.getFilesManager().getConfigsFile();
		prefix = color(messages.getString("prefix"));
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		final Entity damaged = e.getEntity();
		final Entity damager = e.getDamager();
		if(damaged instanceof Player
			&& damager instanceof Player)
		{
			final Player player = (Player) damaged;
			final Player playerDamager = (Player) damager;
			if(instance.getPlayerFrozen().containsKey(player.getUniqueId())
				&& config.getBoolean("disable-damages", false))
			{
				e.setCancelled(true);
				sendMessage(playerDamager, messages.getStringList("errors.damaged-a-frozen-player"), prefix);
			}
		}
		else if(damager instanceof Player)
		{
			final Player playerDamager = (Player) damager;
			if(instance.getPlayerFrozen().containsKey(playerDamager.getUniqueId())
				&& config.getBoolean("disable-damages", false))
			{
				e.setCancelled(true);
				sendMessage(playerDamager, messages.getStringList("errors.damage-while-frozen"), prefix);
			}
		}
	}}
