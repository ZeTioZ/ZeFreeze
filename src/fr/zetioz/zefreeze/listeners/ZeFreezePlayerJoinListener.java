package fr.zetioz.zefreeze.listeners;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.zefreeze.FreezeElement;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import fr.zetioz.zefreeze.guis.AntiDisconnectionGUI;
import org.bukkit.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ZeFreezePlayerJoinListener implements Listener, FilesManagerUtils.ReloadableFiles {
	private final ZeFreezePlugin instance;
	private final AntiDisconnectionGUI antiDisconnectionGUI;
	private final Map<UUID, FreezeElement> playerFrozen;
	private YamlConfiguration config;

	public ZeFreezePlayerJoinListener(ZeFreezePlugin instance) throws FileNotFoundException {
		this.instance = instance;
		this.antiDisconnectionGUI = new AntiDisconnectionGUI(instance);
		playerFrozen = instance.getPlayerFrozen();
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException {
		this.config = instance.getFilesManagerUtils().getSimpleYaml("config");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final UUID playerId = player.getUniqueId();

		if (!playerFrozen.containsKey(playerId)) {
			if (instance.getPendingEffectRemoval().remove(playerId)) {
				config.getStringList("freeze-effects").forEach(effect -> {
					if (Registry.EFFECT.match(effect) != null) {
						player.removePotionEffect(Objects.requireNonNull(Registry.EFFECT.match(effect)));
					} else {
						instance.getLogger().severe(String.format("The potion %s doesn't exist! Please, change it...", effect));
					}
				});
			}
			return;
		}

		final FreezeElement freeze = playerFrozen.get(playerId);
		if (freeze.getLocation() == null) {
			playerFrozen.put(playerId, new FreezeElement(freeze.getFreezer(), freeze.getReason(), player.getLocation()));
		}

		config.getStringList("freeze-effects").forEach(effect -> {
			if (Registry.EFFECT.match(effect) != null) {
				PotionEffect potionEffect = Objects.requireNonNull(Registry.EFFECT.match(effect)).createEffect(Integer.MAX_VALUE, 0);
				player.addPotionEffect(potionEffect);
			} else {
				instance.getLogger().severe(String.format("The potion %s doesn't exist! Please, change it...", effect));
			}
		});

		if (!config.getBoolean("anti-disconnection-gui.enabled")) return;

		player.openInventory(antiDisconnectionGUI.getInventory());
	}
}
