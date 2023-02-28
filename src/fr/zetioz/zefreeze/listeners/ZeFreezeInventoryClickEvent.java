package fr.zetioz.zefreeze.listeners;

import fr.zetioz.zefreeze.ZeFreezeMain;
import fr.zetioz.zefreeze.guis.AntiDisconnectionGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class ZeFreezeInventoryClickEvent implements Listener
{
	private final ZeFreezeMain instance;

	public ZeFreezeInventoryClickEvent(ZeFreezeMain instance)
	{
		this.instance = instance;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(!(event.getWhoClicked() instanceof final Player player)) return;

		final UUID playerUUID = player.getUniqueId();
		if(event.getView().getTopInventory().getHolder() instanceof AntiDisconnectionGUI
			&& instance.getPlayerFrozen().containsKey(playerUUID))
		{
			event.setCancelled(true);
		}
	}
}
