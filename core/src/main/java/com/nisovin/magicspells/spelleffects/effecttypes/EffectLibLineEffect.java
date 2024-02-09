package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.configuration.ConfigurationSection;

import de.slikey.effectlib.util.DynamicLocation;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

@Name("effectlibline")
public class EffectLibLineEffect extends EffectLibEffect {

	private ConfigData<Boolean> forceStaticOriginLocation;
	private ConfigData<Boolean> forceStaticTargetLocation;
	
	@Override
	public void loadFromConfig(ConfigurationSection section) {
		super.loadFromConfig(section);
		forceStaticOriginLocation = ConfigDataUtil.getBoolean(section, "static-origin-location", true);
		forceStaticTargetLocation = ConfigDataUtil.getBoolean(section, "static-target-location", false);
	}
	
	@Override
	public Runnable playEffect(Location startLoc, Location endLoc, SpellData data) {
		if (!initialize()) return null;
		manager.start(className, getParameters(data), new DynamicLocation(startLoc), new DynamicLocation(endLoc), (ConfigurationSection) null, null);
		return null;
	}
	
	@Override
	public void playTrackingLinePatterns(Location origin, Location target, Entity originEntity, Entity targetEntity, SpellData data) {
		if (!initialize()) return;
		if (forceStaticOriginLocation.get(data)) {
			if (origin == null && originEntity != null) origin = originEntity.getLocation();
			originEntity = null;
		}
		if (forceStaticTargetLocation.get(data)) {
			if (target == null && targetEntity != null) target = targetEntity.getLocation();
			targetEntity = null;
		}
		manager.start(className, getParameters(data), new DynamicLocation(origin, originEntity), new DynamicLocation(target, targetEntity), (ConfigurationSection) null, null);
	}
	
}
