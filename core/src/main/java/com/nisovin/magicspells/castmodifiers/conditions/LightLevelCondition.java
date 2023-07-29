package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

public class LightLevelCondition extends OperatorCondition {

	private LightType type = LightType.ALL;
	private byte level = 0;

	@Override
	public boolean initialize(String var) {
		if (var == null || var.isEmpty()) return false;
		String[] splits = var.split(";");
		if (splits.length > 1) {
			try {
				type = LightType.valueOf(splits[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				DebugHandler.debugBadEnumValue(LightType.class, splits[0]);
				return false;
			}
			var = splits[1];
		}

		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			level = Byte.parseByte(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}
	
	@Override
	public boolean check(LivingEntity caster) {
		return lightLevel(caster.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return lightLevel(target.getLocation());
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return lightLevel(location);
	}

	private boolean lightLevel(Location location) {
		Block block = location.getBlock();
		byte lightLevel = switch (type) {
			case ALL -> block.getLightLevel();
			case BLOCK -> block.getLightFromBlocks();
			case SKY -> block.getLightFromSky();
		};
		if (equals) return lightLevel == level;
		else if (moreThan) return lightLevel > level;
		else if (lessThan) return lightLevel < level;
		return false;
	}

	public enum LightType {
		ALL,
		BLOCK,
		SKY
	}

}
