package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeBlockPlaceListener implements Listener, FilesManagerUtils.ReloadableFiles {
    private final ZeFreezePlugin instance;
    private YamlConfiguration messages;
    private YamlConfiguration config;
    private String prefix;

    public ZeFreezeBlockPlaceListener(final ZeFreezePlugin instance) throws FileNotFoundException {
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
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (!instance.getPlayerFrozen().containsKey(player.getUniqueId())
                || !config.getBoolean("disable-block-place", false)) return;

        event.setCancelled(true);
        sendMessage(player, messages.getStringList("errors.place-while-frozen"), prefix);
    }
}
