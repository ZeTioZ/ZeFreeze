package fr.zetioz.zefreeze.listeners;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.zetioz.coreutils.FilesManagerUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.zetioz.zefreeze.ZeFreezeMain;
import fr.zetioz.zefreeze.object.Freeze;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezePlayerMoveEvent implements Listener, FilesManagerUtils.ReloadableFiles
{
	private final ZeFreezeMain instance;
	private final Map<UUID, Freeze> playerFrozen;
	private final Map<UUID, Long> playerErrorCooldown;
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private String prefix;
	
	public ZeFreezePlayerMoveEvent(ZeFreezeMain instance) throws FileNotFoundException
	{
		this.instance = instance;
		this.playerFrozen = instance.getPlayerFrozen();
		this.playerErrorCooldown = new HashMap<>();
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException
	{
		this.messagesFile = instance.getFilesManagerUtils().getSimpleYaml("messages");
		this.configsFile = instance.getFilesManagerUtils().getSimpleYaml("config");
		this.prefix = messagesFile.getString("prefix");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		final Player player = e.getPlayer();
		if(playerFrozen.containsKey(player.getUniqueId()))
		{
			final Freeze playerFreeze = playerFrozen.get(player.getUniqueId());
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
					sendMessage(player, messagesFile.getStringList("errors.move-while-frozen"), prefix, "{freezer}", playerFrozen.get(player.getUniqueId()).getFreezer());
				}
			}
		}
	}
}
