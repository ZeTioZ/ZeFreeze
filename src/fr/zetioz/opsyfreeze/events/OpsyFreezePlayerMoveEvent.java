package fr.zetioz.opsyfreeze.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.zetioz.opsyfreeze.OpsyFreezeMain;
import fr.zetioz.opsyfreeze.object.Freeze;
import fr.zetioz.opsyfreeze.utils.ColorUtils;

public class OpsyFreezePlayerMoveEvent implements Listener
{
	private OpsyFreezeMain main;
	private Map<UUID, Freeze> playerFrozen;
	private Map<UUID, Long> playerErrorCooldown;
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private String prefix;
	
	public OpsyFreezePlayerMoveEvent(OpsyFreezeMain main)
	{
		this.main = main;
		this.playerFrozen = main.getPlayerFrozen();
		this.playerErrorCooldown = new HashMap<>();
		this.messagesFile = this.main.getFilesManager().getMessagesFile();
		this.configsFile = this.main.getFilesManager().getConfigsFile();
		this.prefix = ColorUtils.color(messagesFile.getString("prefix"));
	}
	
	public void setPlayerFrozen(Map<UUID, Freeze> playerFrozen)
	{
		this.playerFrozen = playerFrozen;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		if(playerFrozen.containsKey(player.getUniqueId()))
		{
			Freeze playerFreeze = playerFrozen.get(player.getUniqueId());
			if(player.getLocation().getX() != playerFreeze.getLocation().getX()
				|| player.getLocation().getZ() != playerFreeze.getLocation().getZ()
				|| (configsFile.getBoolean("block-y-axis")
					&& player.getLocation().getY() != playerFreeze.getLocation().getY()))
			{
				player.teleport(playerFreeze.getLocation());
				if(!playerErrorCooldown.containsKey(player.getUniqueId())
					|| playerErrorCooldown.get(player.getUniqueId()) - System.currentTimeMillis() < 0)
				{					
					playerErrorCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (configsFile.getLong("freeze-message-cooldown") * 1000));
					ColorUtils.sendMessage(player, messagesFile.getStringList("errors.move-while-frozen"), prefix, "{freezer}", playerFrozen.get(player.getUniqueId()).getFreezer());
				}
			}
		}
	}
}
