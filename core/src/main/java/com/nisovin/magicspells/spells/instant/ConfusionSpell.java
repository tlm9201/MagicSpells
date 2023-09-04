package com.nisovin.magicspells.spells.instant;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class ConfusionSpell extends InstantSpell implements TargetedLocationSpell {

	private final ConfigData<Double> radius;
	private final ConfigData<Boolean> powerAffectsRadius;
	
	public ConfusionSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		radius = getConfigDataDouble("radius", 10);
		powerAffectsRadius = getConfigDataBoolean("power-affects-radius", true);
	}

	@Override
	public CastResult cast(SpellData data) {
		return castAtLocation(data.location(data.caster().getLocation()));
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		double castingRange = radius.get(data);
		if (powerAffectsRadius.get(data)) castingRange = castingRange * data.power();
		castingRange = Math.min(castingRange, MagicSpells.getGlobalRadius());

		Location location = data.location();
		Collection<Entity> entities = location.getWorld().getNearbyEntities(location, castingRange, castingRange, castingRange);
		List<LivingEntity> monsters = new ArrayList<>();

		for (Entity e : entities) {
			if (!(e instanceof LivingEntity livingEntity)) continue;
			if (!validTargetList.canTarget(data.caster(), e)) continue;
			monsters.add(livingEntity);
		}

		for (int i = 0; i < monsters.size(); i++) {
			int next = i + 1;
			if (next >= monsters.size()) next = 0;
			MobUtil.setTarget(monsters.get(i), monsters.get(next));

			SpellData subData = data.target(monsters.get(i));
			playSpellEffects(EffectPosition.TARGET, monsters.get(i), subData);
			if (data.hasCaster()) playSpellEffectsTrail(data.caster().getLocation(), monsters.get(i).getLocation(), subData);
		}

		if (data.hasCaster()) playSpellEffects(EffectPosition.CASTER, data.caster(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
