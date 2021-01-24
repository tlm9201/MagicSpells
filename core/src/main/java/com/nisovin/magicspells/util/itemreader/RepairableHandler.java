package com.nisovin.magicspells.util.itemreader;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.REPAIR_COST;

public class RepairableHandler {

	private static final String CONFIG_NAME = REPAIR_COST.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof Repairable)) return meta;
		if (!config.isInt(CONFIG_NAME)) return meta;

		int repairCost = config.getInt(CONFIG_NAME);
		((Repairable) meta).setRepairCost(repairCost);
		if (data != null) data.setItemAttribute(REPAIR_COST, repairCost);

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof Repairable)) return meta;
		if (!data.hasItemAttribute(REPAIR_COST)) return meta;

		((Repairable) meta).setRepairCost((int) data.getItemAttribute(REPAIR_COST));
		return meta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData itemData) {
		if (itemData == null) return null;
		if (itemStack == null) return itemData;

		ItemMeta meta = itemStack.getItemMeta();
		if (!(meta instanceof Repairable)) return itemData;

		itemData.setItemAttribute(REPAIR_COST, ((Repairable) meta).getRepairCost());
		return itemData;
	}
	
}
