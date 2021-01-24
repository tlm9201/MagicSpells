package com.nisovin.magicspells.util.itemreader;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.TEXTURE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.SIGNATURE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.SKULL_OWNER;

public class SkullHandler {

	private static final String SKULL_OWNER_CONFIG_NAME = SKULL_OWNER.toString();
	private static final String UUID_CONFIG_NAME = ItemAttribute.UUID.toString();
	private static final String SIGNATURE_CONFIG_NAME = SIGNATURE.toString();
	private static final String TEXTURE_CONFIG_NAME = TEXTURE.toString();

	public static ItemMeta process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SkullMeta)) return meta;
		
		SkullMeta skullMeta = (SkullMeta) meta;
		OfflinePlayer offlinePlayer;

		if (config.isString(SKULL_OWNER_CONFIG_NAME)) {
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(config.getString(SKULL_OWNER_CONFIG_NAME)));

			skullMeta.setOwningPlayer(offlinePlayer);
			data.setItemAttribute(SKULL_OWNER, offlinePlayer);
		}

		String uuid = null;
		String texture = null;
		String signature = null;

		if (config.isString(UUID_CONFIG_NAME)) {
			uuid = config.getString(UUID_CONFIG_NAME);
			data.setItemAttribute(ItemAttribute.UUID, uuid);
		}
		if (config.isString(TEXTURE_CONFIG_NAME)) {
			texture = config.getString(TEXTURE_CONFIG_NAME);
			data.setItemAttribute(TEXTURE, texture);
		}
		if (config.isString(SIGNATURE_CONFIG_NAME)) {
			signature = config.getString(SIGNATURE_CONFIG_NAME);
			data.setItemAttribute(SIGNATURE, signature);
		}

		if (texture != null && skullMeta.getOwningPlayer() != null) {
			Util.setTexture(skullMeta, texture, signature, uuid, skullMeta.getOwningPlayer());
		}

		return meta;
	}

	public static ItemMeta process(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SkullMeta)) return meta;

		SkullMeta skullMeta = (SkullMeta) meta;

		OfflinePlayer offlinePlayer = (OfflinePlayer) data.getItemAttribute(SKULL_OWNER);
		if (offlinePlayer != null) skullMeta.setOwningPlayer(offlinePlayer);

		String uuid = (String) data.getItemAttribute(ItemAttribute.UUID);
		String signature = (String) data.getItemAttribute(SIGNATURE);
		String texture = (String) data.getItemAttribute(TEXTURE);

		if (texture != null && skullMeta.getOwningPlayer() != null) {
			Util.setTexture(skullMeta, texture, signature, uuid, skullMeta.getOwningPlayer());
		}

		return skullMeta;
	}

	public static MagicItemData process(ItemStack itemStack, MagicItemData data) {
		if (data == null) return null;
		if (itemStack == null) return data;

		ItemMeta meta = itemStack.getItemMeta();
		if (!(meta instanceof SkullMeta)) return data;

		data.setItemAttribute(SKULL_OWNER, ((SkullMeta) meta).getOwningPlayer());
		return data;
	}
	
}
