package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.FreezeElement;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.sendMessage;

public class ZeFreezeCommandPreProcessListener implements Listener, FilesManagerUtils.ReloadableFiles {
    private final ZeFreezePlugin instance;
    private YamlConfiguration config;
    private YamlConfiguration messages;
    private String prefix;

    public ZeFreezeCommandPreProcessListener(ZeFreezePlugin instance) throws FileNotFoundException {
        this.instance = instance;
        instance.getFilesManagerUtils().addReloadable(this);
        reloadFiles();
    }

    @Override
    public void reloadFiles() throws FileNotFoundException {
        config = instance.getFilesManagerUtils().getSimpleYaml("config");
        messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
        prefix = messages.getString("prefix", "&c[&6ZeFreeze&c] &r");
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        final String fullCommand = event.getMessage();
        final String[] args = fullCommand.split(" ");
        final Player player = event.getPlayer();

        if (args.length == 0) return;
        if (!config.getBoolean("block-commands.enabled")) return;
        if (config.getStringList("block-commands.whitelist").contains(args[0])) return;
        if (!instance.getPlayerFrozen().containsKey(player.getUniqueId())) return;


        event.setCancelled(true);
        final FreezeElement freeze = instance.getPlayerFrozen().get(player.getUniqueId());

        if (freeze == null) return;

        sendMessage(player, messages.getStringList("errors.blocked-command"), prefix, "{player}", player.getName(),
                "{reason}", freeze.getReason(),
                "{freezer}", freeze.getFreezer(),
                "{command}", fullCommand);
    }
}
