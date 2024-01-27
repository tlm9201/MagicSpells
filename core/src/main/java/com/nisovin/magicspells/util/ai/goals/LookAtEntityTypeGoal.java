package com.nisovin.magicspells.util.ai.goals;

import java.util.EnumSet;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.configuration.ConfigurationSection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.entity.ai.GoalType;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.ai.CustomGoal;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

@Name(name = "look_at_entity_type")
public class LookAtEntityTypeGoal extends CustomGoal {

	private ConfigData<Float> range = spellData -> 10f;
	private ConfigData<Float> chance = spellData -> 100f;
	private ConfigData<Boolean> onlyHorizontal = spellData -> false;
	private ConfigData<EntityType> type = spellData -> EntityType.PLAYER;

	private Entity target;
	private int lookTime;

	public LookAtEntityTypeGoal(Mob mob, SpellData data) {
		super(mob, data);
	}

	@Override
	public boolean initialize(@Nullable ConfigurationSection config) {
		if (config == null) return true;
		range = ConfigDataUtil.getFloat(config, "range", 10);
		chance = ConfigDataUtil.getFloat(config, "chance", 100);
		type = ConfigDataUtil.getEntityType(config, "type", EntityType.PLAYER);
		onlyHorizontal = ConfigDataUtil.getBoolean(config, "only-horizontal", false);
		return true;
	}

	@Override
	public boolean shouldActivate() {
		float range = LookAtEntityTypeGoal.this.range.get(data);
		EntityType type = LookAtEntityTypeGoal.this.type.get(data);
		float chance = LookAtEntityTypeGoal.this.chance.get(data) / 100F;

		if (RANDOM.nextFloat() >= chance) return false;
		if (mob.getTarget() != null) target = mob.getTarget();

		target = Util.getNearestEntity(mob, range, entity -> {
			if (type != EntityType.PLAYER) return type == entity.getType();
			return rides(entity).and(obj -> type == obj.getType()).test(entity);
		});
		return target != null;
	}

	@Override
	public boolean shouldStayActive() {
		float range = LookAtEntityTypeGoal.this.range.get(data);
		return target.isValid() &&
				lookTime > 0 &&
				mob.getLocation().distanceSquared(target.getLocation()) < (range * range);
	}

	@Override
	public void start() {
		lookTime = RANDOM.nextInt(40, 80);
	}

	@Override
	public void stop() {
		target = null;
	}

	@Override
	public void tick() {
		Location loc = target.getLocation();
		double y = target instanceof LivingEntity e && !onlyHorizontal.get(data) ? e.getEyeLocation().y() : loc.y();
		mob.lookAt(loc.x(), y, loc.z());
		lookTime--;
	}

	@NotNull
	@Override
	public EnumSet<GoalType> getTypes() {
		return EnumSet.of(GoalType.LOOK);
	}

	private Predicate<Entity> rides(Entity vehicle) {
		return passenger -> {
			while (passenger.getVehicle() != null) {
				passenger = passenger.getVehicle();
				if (passenger == vehicle) {
					return false;
				}
			}
			return true;
		};
	}

}
