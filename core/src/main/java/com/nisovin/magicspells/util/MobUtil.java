package com.nisovin.magicspells.util;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.entity.Mob;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class MobUtil {

	private static final Map<String, EntityType> entityTypeMap = new HashMap<>();

	static {
		for (EntityType type : EntityType.values()) {
			if (type == EntityType.UNKNOWN) continue;

			entityTypeMap.put(type.name().toLowerCase(), type);
			entityTypeMap.put(type.name().toLowerCase().replace("_", ""), type);

			entityTypeMap.put(type.getKey().getKey(), type);
			entityTypeMap.put(type.getKey().getKey().replace("_", ""), type);
		}

		Map<String, EntityType> types = new HashMap<>();
		for (Map.Entry<String, EntityType> entry : entityTypeMap.entrySet()) {
			types.put(entry.getKey() + 's', entry.getValue());
		}
		entityTypeMap.putAll(types);
	}

	public static EntityType getEntityType(String type) {
		if (type.equalsIgnoreCase("player")) return EntityType.PLAYER;
		return entityTypeMap.get(type.toLowerCase());
	}

	public static void setTarget(LivingEntity mob, LivingEntity target) {
		if (mob instanceof Mob m) m.setTarget(target);
	}


}
