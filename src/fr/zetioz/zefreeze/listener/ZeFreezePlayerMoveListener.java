package fr.zetioz.zefreeze.listener;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.FreezeElement;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezePlayerMoveListener implements Listener, FilesManagerUtils.ReloadableFiles {
    private final ZeFreezePlugin instance;
    private final Map<UUID, FreezeElement> playerFrozen;
    private final Map<UUID, Long> playerErrorCooldown;
    private YamlConfiguration messagesFile;
    private YamlConfiguration configsFile;
    private String prefix;

    public ZeFreezePlayerMoveListener(ZeFreezePlugin instance) throws FileNotFoundException {
        this.instance = instance;
        this.playerFrozen = instance.getPlayerFrozen();
        this.playerErrorCooldown = new HashMap<>();
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
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (!playerFrozen.containsKey(player.getUniqueId())) return;

        final FreezeElement playerFreeze = playerFrozen.get(player.getUniqueId());

        if (player.getLocation().getX() == playerFreeze.getLocation().getX()
                && player.getLocation().getZ() == playerFreeze.getLocation().getZ()
                && (!configsFile.getBoolean("block-y-axis")
                || player.getLocation().getY() == playerFreeze.getLocation().getY())) {
            return;
        }

        player.teleport(playerFreeze.getLocation());

        if (playerErrorCooldown.containsKey(player.getUniqueId())
                && playerErrorCooldown.get(player.getUniqueId()) - System.currentTimeMillis() >= 0) return;

        playerErrorCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (configsFile.getLong("freeze-message-cooldown") * 1000));
        sendMessage(player, messagesFile.getStringList("errors.move-while-frozen"), prefix, "{freezer}", playerFrozen.get(player.getUniqueId()).getFreezer());
    }
}
