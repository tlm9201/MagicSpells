package com.nisovin.magicspells.spells.instant;

import java.util.List;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class RoarSpell extends InstantSpell {

	private final ConfigData<Double> radius;

	private String strNoTarget;

	private final ConfigData<Boolean> cancelIfNoTargets;

	public RoarSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		radius = getConfigDataDouble("radius", 8F);

		strNoTarget = getConfigString("str-no-target", "No targets found.");

		cancelIfNoTargets = getConfigDataBoolean("cancel-if-no-targets", true);
	}

	@Override
	public CastResult cast(SpellData data) {
		double radius = Math.min(this.radius.get(data), MagicSpells.getGlobalRadius());

		List<Entity> entities = data.caster().getNearbyEntities(radius, radius, radius);
		int count = 0;

		for (Entity entity : entities) {
			if (!(entity instanceof Mob mob)) continue;
			if (!validTargetList.canTarget(data.caster(), mob)) continue;

			SpellTargetEvent targetEvent = new SpellTargetEvent(this, data, mob);
			if (!targetEvent.callEvent()) continue;

			LivingEntity le = targetEvent.getTarget();
			if (!(le instanceof Mob target)) continue;

			target.setTarget(data.caster());
			count++;

			SpellData subData = targetEvent.getSpellData();
			playSpellEffects(EffectPosition.TARGET, target, subData);
			playSpellEffectsTrail(data.caster().getLocation(), target.getLocation(), subData);
		}

		if (cancelIfNoTargets.get(data) && count == 0) {
			sendMessage(strNoTarget, data.caster(), data);
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	public String getStrNoTarget() {
		return strNoTarget;
	}

	public void setStrNoTarget(String strNoTarget) {
		this.strNoTarget = strNoTarget;
	}

}
