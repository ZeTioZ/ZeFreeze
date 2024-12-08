package fr.zetioz.zefreeze.guis;

import fr.zetioz.coreutils.FilesManagerUtils;
import fr.zetioz.coreutils.MaterialUtils;
import fr.zetioz.itembuilderutils.ItemBuilderUtils;
import fr.zetioz.zefreeze.ZeFreezePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.FileNotFoundException;

import static fr.zetioz.coreutils.ColorUtils.color;

public class AntiDisconnectionGUI implements InventoryHolder, FilesManagerUtils.ReloadableFiles {
	private final ZeFreezePlugin instance;
	private YamlConfiguration config;
	private YamlConfiguration messages;
	private String prefix;

	public AntiDisconnectionGUI(ZeFreezePlugin instance) throws FileNotFoundException {
		this.instance = instance;
		instance.getFilesManagerUtils().addReloadable(this);
		reloadFiles();
	}

	@Override
	public void reloadFiles() throws FileNotFoundException {
		config = instance.getFilesManagerUtils().getSimpleYaml("config");
		messages = instance.getFilesManagerUtils().getSimpleYaml("messages");
		prefix = messages.getString("prefix");
	}

	public Inventory buildInventory() {
		final Inventory inventory = Bukkit.createInventory(this, config.getInt("anti-disconnection-gui.size", 27), color(config.getString("anti-disconnection-gui.title", "&cAnti-disconnection").replace("{prefix}", prefix)));
		final ItemStack backgroundItem = new ItemBuilderUtils(instance).material(MaterialUtils.getMaterial(instance, config.getString("anti-disconnection-gui.background.material", "BARRIER")))
				.name(color(config.getString("anti-disconnection-gui.background.name", "")))
				.lore(color(config.getStringList("anti-disconnection-gui.background.lore")))
				.model(config.getInt("anti-disconnection-gui.background.model", 0))
				.build();
		final ItemStack borderItem = new ItemBuilderUtils(instance).material(MaterialUtils.getMaterial(instance, config.getString("anti-disconnection-gui.border.material", "BLACK_STAINED_GLASS_PANE")))
				.name(color(config.getString("anti-disconnection-gui.border.name", "")))
				.lore(color(config.getStringList("anti-disconnection-gui.border.lore")))
				.model(config.getInt("anti-disconnection-gui.border.model", 0))
				.build();
		final int rows = (int) Math.floor(inventory.getSize() / 9d);
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < 9; x++) {
				if ((x == 0 || x == 8 || y == 0 || y == rows - 1) && config.getBoolean("anti-disconnection-gui.border.enabled", true)) {
					inventory.setItem(x + (y * 9), borderItem);
				} else if (config.getBoolean("anti-disconnection-gui.background.enabled", true)) {
					inventory.setItem(x + (y * 9), backgroundItem);
				}
			}
		}
		if (config.isConfigurationSection("anti-disconnection-gui.items")) {
			for (String customItemKey : config.getConfigurationSection("anti-disconnection-gui.items").getKeys(false)) {
				final ConfigurationSection customItemSection = config.getConfigurationSection("anti-disconnection-gui.items." + customItemKey);
				final ItemStack customItem = new ItemBuilderUtils(instance).material(MaterialUtils.getMaterial(instance, customItemSection.getString("material", "BARRIER")))
						.name(color(customItemSection.getString("name", "")))
						.lore(color(customItemSection.getStringList("lore")))
						.model(customItemSection.getInt("model", 0))
						.build();
				final boolean validSlot = customItemSection.getInt("slot", 0) >= 0 && customItemSection.getInt("slot", 0) < inventory.getSize();
				if (!validSlot)
					instance.getLogger().warning("The slot " + customItemSection.getInt("slot", 0) + " is not valid for the item " + customItemKey + " in the anti-disconnection-gui. Please change it in the config file.");
				final int slot = validSlot ? customItemSection.getInt("slot", 0) : 0;
				inventory.setItem(slot, customItem);
			}
		}
		return inventory;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
}
