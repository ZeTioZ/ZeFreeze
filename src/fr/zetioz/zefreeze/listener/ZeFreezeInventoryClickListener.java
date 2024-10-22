package fr.zetioz.zefreeze.listener;

import fr.zetioz.zefreeze.ZeFreezePlugin;
import fr.zetioz.zefreeze.gui.AntiDisconnectionGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class ZeFreezeInventoryClickListener implements Listener {
    private final ZeFreezePlugin instance;

    public ZeFreezeInventoryClickListener(ZeFreezePlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) return;

        final UUID playerUUID = player.getUniqueId();
        if (!(event.getView().getTopInventory().getHolder() instanceof AntiDisconnectionGUI)
                || !instance.getPlayerFrozen().containsKey(playerUUID)) return;

        event.setCancelled(true);
    }
}
