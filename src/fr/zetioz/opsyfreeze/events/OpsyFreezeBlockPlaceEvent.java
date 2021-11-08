package fr.zetioz.opsyfreeze.events;

import fr.zetioz.opsyfreeze.OpsyFreezeMain;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import static fr.zetioz.opsyfreeze.utils.ColorUtils.color;
import static fr.zetioz.opsyfreeze.utils.ColorUtils.sendMessage;

public class OpsyFreezeBlockPlaceEvent implements Listener
{
	private final OpsyFreezeMain instance;
	private final YamlConfiguration messages;
	private final YamlConfiguration config;
	private final String prefix;

	public OpsyFreezeBlockPlaceEvent(final OpsyFreezeMain instance)
	{
		this.instance = instance;
		messages = instance.getFilesManager().getMessagesFile();
		config = instance.getFilesManager().getConfigsFile();
		prefix = color(messages.getString("prefix"));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		final Player player = e.getPlayer();
		if(instance.getPlayerFrozen().containsKey(player.getUniqueId())
			&& config.getBoolean("disable-block-place", false))
		{
			e.setCancelled(true);
			sendMessage(player, messages.getStringList("errors.place-while-frozen"), prefix);
		}
	}
}
