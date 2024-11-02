package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import fr.zetioz.zefreeze.guis.AntiDisconnectionGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.FileNotFoundException;
import java.util.UUID;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeInventoryCloseListener implements Listener, FilesManagerUtils.ReloadableFiles {
    private final ZeFreezePlugin instance;
    private final AntiDisconnectionGUI antiDisconnectionGUI;
    private YamlConfiguration messages;
    private String prefix;

    public ZeFreezeInventoryCloseListener(ZeFreezePlugin instance) throws FileNotFoundException {
        this.instance = instance;
        this.antiDisconnectionGUI = new AntiDisconnectionGUI(instance);
        instance.getFilesManagerUtils().addReloadable(this);
        reloadFiles();
    }

    @Override
    public void reloadFiles() throws FileNotFoundException {
        messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
        prefix = messages.getString("prefix");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) return;

        final UUID playerUUID = player.getUniqueId();
        if (!(event.getView().getTopInventory().getHolder() instanceof AntiDisconnectionGUI)
                || !instance.getPlayerFrozen().containsKey(playerUUID)) return;

        sendMessage(player, messages.getStringList("errors.close-anti-disconnection-gui"), prefix);
        Bukkit.getScheduler().runTaskLater(instance, () -> player.openInventory(antiDisconnectionGUI.buildInventory()), 2L);
    }
}
