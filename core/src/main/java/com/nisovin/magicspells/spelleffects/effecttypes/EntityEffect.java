package com.nisovin.magicspells.spelleffects.effecttypes;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.EntityData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

@Name("entity")
public class EntityEffect extends SpellEffect {

	public static final Set<Entity> entities = new HashSet<>();

	public static final String ENTITY_TAG = "MS_ENTITY";

	private EntityData entityData;

	private ConfigData<Integer> duration;

	private ConfigData<Boolean> silent;
	private ConfigData<Boolean> gravity;
	private ConfigData<Boolean> enableAI;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		ConfigurationSection section = config.getConfigurationSection("entity");
		if (section == null) return;

		entityData = new EntityData(section);

		duration = ConfigDataUtil.getInteger(section, "duration", 0);

		silent = ConfigDataUtil.getBoolean(section, "silent", false);
		gravity = ConfigDataUtil.getBoolean(section, "gravity", false);
		enableAI = ConfigDataUtil.getBoolean(section, "ai", true);
	}

	@Override
	protected Entity playEntityEffectLocation(Location location, SpellData data) {
		return entityData.spawn(location, data, entity -> {
			entity.addScoreboardTag(ENTITY_TAG);
			entity.setGravity(gravity.get(data));
			entity.setSilent(silent.get(data));

			if (entity instanceof LivingEntity livingEntity) livingEntity.setAI(enableAI.get(data));
		});
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		Entity entity = playEntityEffectLocation(location, data);
		entities.add(entity);

		int duration = this.duration.get(data);
		if (duration > 0) MagicSpells.scheduleDelayedTask(() -> {
			entities.remove(entity);
			entity.remove();
		}, duration);
		return null;
	}

	@Override
	public void turnOff() {
		for (Entity entity : entities) {
			entity.remove();
		}
		entities.clear();
	}

}
