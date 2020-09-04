package com.nisovin.magicspells.spelleffects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.EntityData;

public class EntityEffect extends SpellEffect {

	public static final String ENTITY_TAG = "MS_ENTITY";

	private EntityData entityData;

	private boolean gravity;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		ConfigurationSection section = config.getConfigurationSection("entity");
		if (section == null) return;

		entityData = new EntityData(section);

		gravity = section.getBoolean("gravity", false);
	}

	@Override
	protected Entity playEntityEffectLocation(Location location) {
		Entity entity = entityData.spawn(location);
		entity.addScoreboardTag(ENTITY_TAG);
		entity.setGravity(gravity);
		return entity;
	}

}
