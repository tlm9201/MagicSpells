package com.nisovin.magicspells.util;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

import static org.bukkit.attribute.AttributeModifier.Operation;

public class AttributeUtil {

	private static final Map<String, Attribute> attributeNameMap = new HashMap<>();
	private static final Map<String, Operation> operationNameMap = new HashMap<>();

	static {
		for (Operation operation : Operation.values()) {
			String name = operation.name().toLowerCase();

			operationNameMap.put(name, operation);
			operationNameMap.put(name.replaceAll("_", ""), operation);
		}

		operationNameMap.put("multiply_scalar", Operation.MULTIPLY_SCALAR_1);
		operationNameMap.put("multiplyscalar", Operation.MULTIPLY_SCALAR_1);

		operationNameMap.put("add", Operation.ADD_NUMBER);
		operationNameMap.put("multiply_base", Operation.ADD_SCALAR);
		operationNameMap.put("multiply", Operation.MULTIPLY_SCALAR_1);
		operationNameMap.put("multiply_total", Operation.MULTIPLY_SCALAR_1);

		operationNameMap.put("add_value", Operation.ADD_NUMBER);
		operationNameMap.put("add_multiplied_base", Operation.ADD_SCALAR);
		operationNameMap.put("add_multiplied_total", Operation.MULTIPLY_SCALAR_1);

		for (Attribute attribute : Registry.ATTRIBUTE) {
			String key = attribute.getKey().getKey();
			String rep = key.replaceAll("_", "");

			attributeNameMap.put(rep, attribute);
		}

		loadLegacyAttributeNames();
	}

	private static void loadLegacyAttributeNames() {
		Map<String, Attribute> legacy = new HashMap<>();
		legacy.put("generic.max_health", Attribute.GENERIC_MAX_HEALTH);
		legacy.put("generic.follow_range", Attribute.GENERIC_FOLLOW_RANGE);
		legacy.put("generic.knockback_resistance", Attribute.GENERIC_KNOCKBACK_RESISTANCE);
		legacy.put("generic.movement_speed", Attribute.GENERIC_MOVEMENT_SPEED);
		legacy.put("generic.flying_speed", Attribute.GENERIC_FLYING_SPEED);
		legacy.put("generic.attack_damage", Attribute.GENERIC_ATTACK_DAMAGE);
		legacy.put("generic.attack_knockback", Attribute.GENERIC_ATTACK_KNOCKBACK);
		legacy.put("generic.attack_speed", Attribute.GENERIC_ATTACK_SPEED);
		legacy.put("generic.armor", Attribute.GENERIC_ARMOR);
		legacy.put("generic.armor_toughness", Attribute.GENERIC_ARMOR_TOUGHNESS);
		legacy.put("generic.fall_damage_multiplier", Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER);
		legacy.put("generic.luck", Attribute.GENERIC_LUCK);
		legacy.put("generic.max_absorption", Attribute.GENERIC_MAX_ABSORPTION);
		legacy.put("generic.safe_fall_distance", Attribute.GENERIC_SAFE_FALL_DISTANCE);
		legacy.put("generic.scale", Attribute.GENERIC_SCALE);
		legacy.put("generic.step_height", Attribute.GENERIC_STEP_HEIGHT);
		legacy.put("generic.gravity", Attribute.GENERIC_GRAVITY);
		legacy.put("generic.jump_strength", Attribute.GENERIC_JUMP_STRENGTH);
		legacy.put("generic.burning_time", Attribute.GENERIC_BURNING_TIME);
		legacy.put("generic.explosion_knockback_resistance", Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
		legacy.put("generic.movement_efficiency", Attribute.GENERIC_MOVEMENT_EFFICIENCY);
		legacy.put("generic.oxygen_bonus", Attribute.GENERIC_OXYGEN_BONUS);
		legacy.put("generic.water_movement_efficiency", Attribute.GENERIC_WATER_MOVEMENT_EFFICIENCY);
		legacy.put("player.block_interaction_range", Attribute.PLAYER_BLOCK_INTERACTION_RANGE);
		legacy.put("player.entity_interaction_range", Attribute.PLAYER_ENTITY_INTERACTION_RANGE);
		legacy.put("player.block_break_speed", Attribute.PLAYER_BLOCK_BREAK_SPEED);
		legacy.put("player.mining_efficiency", Attribute.PLAYER_MINING_EFFICIENCY);
		legacy.put("player.sneaking_speed", Attribute.PLAYER_SNEAKING_SPEED);
		legacy.put("player.submerged_mining_speed", Attribute.PLAYER_SUBMERGED_MINING_SPEED);
		legacy.put("player.sweeping_damage_ratio", Attribute.PLAYER_SWEEPING_DAMAGE_RATIO);
		legacy.put("zombie.spawn_reinforcements", Attribute.ZOMBIE_SPAWN_REINFORCEMENTS);

		attributeNameMap.putAll(legacy);

		for (Map.Entry<String, Attribute> entry : legacy.entrySet()) {
			Attribute attribute = entry.getValue();

			String key = entry.getKey();
			String rep = key.replaceAll("_", "");

			attributeNameMap.put(key.replace('.', '_'), attribute);

			attributeNameMap.put(key, attribute);
			attributeNameMap.put(key.substring(key.indexOf('.') + 1), attribute);

			attributeNameMap.put(rep, attribute);
			attributeNameMap.put(rep.substring(rep.indexOf('.') + 1), attribute);
		}
	}

	public static Attribute getAttribute(String attribute) {
		Attribute attr = attributeNameMap.get(attribute.toLowerCase());
		if (attr != null) return attr;

		NamespacedKey key = NamespacedKey.fromString(attribute);
		if (key == null) return null;

		return Registry.ATTRIBUTE.get(key);
	}

	public static Operation getOperation(String operation) {
		return operationNameMap.get(operation.toLowerCase());
	}

	public static String getOperationName(Operation operation) {
		return switch (operation) {
			case ADD_NUMBER -> "add_value";
			case ADD_SCALAR -> "add_multiplied_base";
			case MULTIPLY_SCALAR_1 -> "add_multiplied_total";
		};
	}

}
