package com.nisovin.magicspells.spells.instant;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class PurgeSpell extends InstantSpell implements TargetedLocationSpell {

	private List<EntityType> entities;

	private final ConfigData<Double> radius;

	private final ConfigData<Boolean> powerAffectsRadius;

	public PurgeSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		radius = getConfigDataDouble("radius", 15);

		powerAffectsRadius = getConfigDataBoolean("power-affects-radius", true);

		List<String> list = getConfigStringList("entities", null);
		if (list != null && !list.isEmpty()) {
			entities = new ArrayList<>();
			for (String s : list) {
				EntityType t = MobUtil.getEntityType(s);
				if (t != null) entities.add(t);
				else MagicSpells.error("PurgeSpell '" + internalName + "' has an invalid entity defined: " + s);
			}

			if (entities.isEmpty()) entities = null;
		}
	}

	@Override
	public CastResult cast(SpellData data) {
		return castAtLocation(data.location(data.caster().getLocation()));
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		Location loc = data.location();

		double radius = this.radius.get(data);
		if (powerAffectsRadius.get(data)) radius *= data.power();
		radius = Math.min(radius, MagicSpells.getGlobalRadius());

		boolean killed = false;
		for (LivingEntity target : loc.getNearbyLivingEntities(radius)) {
			if (target instanceof Player) continue;
			if (!validTargetList.canTarget(data.caster(), target)) continue;
			if (entities != null && !entities.contains(target.getType())) continue;

			SpellData subData = data.target(target);
			playSpellEffectsTrail(loc, target.getLocation(), subData);
			playSpellEffects(EffectPosition.TARGET, target, subData);

			target.setHealth(0);
			killed = true;
		}
		if (!killed) return noTarget(data);

		if (data.hasCaster()) playSpellEffects(EffectPosition.CASTER, data.caster(), data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	public List<EntityType> getEntities() {
		return entities;
	}

}
