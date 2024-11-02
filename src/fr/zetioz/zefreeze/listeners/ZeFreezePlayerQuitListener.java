package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.FreezeElement;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

import static fr.zetioz.coreutils.ColorUtils.*;

public class ZeFreezePlayerQuitListener implements Listener, FilesManagerUtils.ReloadableFiles {
    private final ZeFreezePlugin instance;
    private Map<UUID, FreezeElement> playerFrozen;
    private YamlConfiguration messagesFile;
    private YamlConfiguration configsFile;
    private String prefix;

    public ZeFreezePlayerQuitListener(ZeFreezePlugin instance) throws FileNotFoundException {
        this.instance = instance;
        playerFrozen = instance.getPlayerFrozen();
        instance.getFilesManagerUtils().addReloadable(this);
        reloadFiles();
    }

    @Override
    public void reloadFiles() throws FileNotFoundException {
        this.messagesFile = instance.getFilesManagerUtils().getSimpleYaml("messages");
        this.configsFile = instance.getFilesManagerUtils().getSimpleYaml("config");
        this.prefix = messagesFile.getString("prefix");
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (!playerFrozen.containsKey(player.getUniqueId())) return;

        if (configsFile.getBoolean("disconnect-action.enabled")) {
            for (String command : configsFile.getStringList("disconnect-action.commands")) {
                command = command.replace("{player}", player.getName());
                command = command.replace("{freezer}", playerFrozen.get(player.getUniqueId()).getFreezer());
                command = command.replace("{reason}", playerFrozen.get(player.getUniqueId()).getReason());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }

        if (configsFile.getBoolean("unfreeze-on-disconnect")) {
            playerFrozen.remove(player.getUniqueId());
            parsePlaceholders(color(messagesFile.getStringList("unfreeze-disconnect")), "{player}", player.getName()).forEach(instance.getLogger()::info);
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer.hasPermission("zefreeze.disconnect.alert"))
                .forEach(onlinePlayer -> sendMessage(onlinePlayer, messagesFile.getStringList("staff-disconnect-alert"), prefix, "{player}", player.getName()));

        parsePlaceholders(color(messagesFile.getStringList("staff-disconnect-alert")), "{player}", player.getName()).forEach(instance.getLogger()::info);
    }
}
