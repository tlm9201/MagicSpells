package com.nisovin.magicspells.util.itemreader;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.TEXTURE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.SIGNATURE;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.SKULL_OWNER;

public class SkullHandler {

	private static final String SKULL_OWNER_CONFIG_NAME = SKULL_OWNER.toString();
	private static final String UUID_CONFIG_NAME = MagicItemAttribute.UUID.toString();
	private static final String SIGNATURE_CONFIG_NAME = SIGNATURE.toString();
	private static final String TEXTURE_CONFIG_NAME = TEXTURE.toString();

	public static void process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SkullMeta)) return;
		
		SkullMeta skullMeta = (SkullMeta) meta;
		OfflinePlayer offlinePlayer;

		if (config.isString(SKULL_OWNER_CONFIG_NAME)) {
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(config.getString(SKULL_OWNER_CONFIG_NAME)));

			skullMeta.setOwningPlayer(offlinePlayer);
			data.setAttribute(SKULL_OWNER, offlinePlayer);
		}

		String uuid = null;
		String texture = null;
		String signature = null;

		if (config.isString(UUID_CONFIG_NAME)) {
			uuid = config.getString(UUID_CONFIG_NAME);
			data.setAttribute(MagicItemAttribute.UUID, uuid);
		}
		if (config.isString(TEXTURE_CONFIG_NAME)) {
			texture = config.getString(TEXTURE_CONFIG_NAME);
			data.setAttribute(TEXTURE, texture);
		}
		if (config.isString(SIGNATURE_CONFIG_NAME)) {
			signature = config.getString(SIGNATURE_CONFIG_NAME);
			data.setAttribute(SIGNATURE, signature);
		}

		if (texture != null && skullMeta.getOwningPlayer() != null) {
			Util.setTexture(skullMeta, texture, signature, uuid, skullMeta.getOwningPlayer());
		}
	}

	public static void processItemMeta(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SkullMeta)) return;

		SkullMeta skullMeta = (SkullMeta) meta;

		OfflinePlayer offlinePlayer = (OfflinePlayer) data.getAttribute(SKULL_OWNER);
		if (offlinePlayer != null) skullMeta.setOwningPlayer(offlinePlayer);

		String uuid = (String) data.getAttribute(MagicItemAttribute.UUID);
		String signature = (String) data.getAttribute(SIGNATURE);
		String texture = (String) data.getAttribute(TEXTURE);

		if (texture != null && skullMeta.getOwningPlayer() != null) {
			Util.setTexture(skullMeta, texture, signature, uuid, skullMeta.getOwningPlayer());
		}
	}

	public static void processMagicItemData(ItemMeta meta, MagicItemData data) {
		if (!(meta instanceof SkullMeta)) return;

		data.setAttribute(SKULL_OWNER, ((SkullMeta) meta).getOwningPlayer());
	}
	
}
