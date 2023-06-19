package com.nisovin.magicspells.spelleffects.trackers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.ModifierResult;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.spelleffects.SpellEffect.SpellEffectActiveChecker;

public class BuffTracker extends EffectTracker implements Runnable {

	private ModifierResult result;

	public BuffTracker(Entity entity, SpellEffectActiveChecker checker, SpellEffect effect, SpellData data) {
		super(entity, checker, effect, data);
	}

	@Override
	public void run() {
		if (!entity.isValid() || !checker.isActive(entity) || effect == null) {
			stop();
			return;
		}

		if (entity instanceof LivingEntity livingEntity && effect.getModifiers() != null) {
			result = effect.getModifiers().apply(livingEntity, data);
			data = result.data();

			if (!result.check()) return;
		}

		effect.playEffect(entity, data);
	}

	@Override
	public void stop() {
		super.stop();
	}

}
