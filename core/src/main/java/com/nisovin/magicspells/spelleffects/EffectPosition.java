package com.nisovin.magicspells.spelleffects;

import java.util.Map;
import java.util.HashMap;

public enum EffectPosition {

	START_CAST("start", "startcast"),
	CASTER("pos1", "position1", "caster", "actor"),
	TARGET("pos2", "position2", "target"),
	TRAIL("line", "trail"),
	DISABLED("disabled"),
	DELAYED("delayed"),
	SPECIAL("special"),
	BUFF("buff", "active"),
	BUFF_EFFECTLIB("buffeffectlib"),
	ORBIT("orbit"),
	ORBIT_EFFECTLIB("orbiteffectlib"),
	REVERSE_LINE("reverse_line", "reverseline", "rline"),
	PROJECTILE("projectile"),
	DYNAMIC_CASTER_PROJECTILE_LINE("casterprojectile", "casterprojectileline"),
	BLOCK_DESTRUCTION("blockdestroy", "blockdestruction"),
	COOLDOWN("cooldown"),
	MISSING_REAGENTS("missingreagents"),
	CHARGE_USE("chargeuse"),

	;

	private final String[] names;

	private static Map<String, EffectPosition> nameMap = new HashMap<>();
	private static boolean initialized = false;

	EffectPosition(String... names) {
		this.names = names;
	}

	private static void initializeNameMap() {
		if (nameMap == null) nameMap = new HashMap<>();
		nameMap.clear();
		for (EffectPosition pos: EffectPosition.values()) {
			// For all of the names
			for (String name: pos.names) {
				nameMap.put(name.toLowerCase(), pos);
			}
		}
		initialized = true;
	}

	public static EffectPosition getPositionFromString(String pos) {
		if (!initialized) initializeNameMap();
		return nameMap.get(pos.toLowerCase());
	}

}
