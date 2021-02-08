package com.nisovin.magicspells.spelleffects.trackers;

import org.bukkit.entity.Entity;

import de.slikey.effectlib.Effect;

import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.spelleffects.SpellEffect.SpellEffectActiveChecker;

public class BuffEffectlibTracker extends AsyncEffectTracker implements Runnable {

	private final Effect effectlibEffect;

	public BuffEffectlibTracker(Entity entity, SpellEffectActiveChecker checker, SpellEffect effect) {
		super(entity, checker, effect);

		effectlibEffect = effect.playEffectLib(entity.getLocation());
		effectlibEffect.infinite();
	}

	@Override
	public void run() {
		if (entity == null || !entity.isValid() || !checker.isActive(entity) || effect == null) {
			stop();
			return;
		}

		effectlibEffect.setLocation(effect.applyOffsets(entity.getLocation().clone()));
	}

	@Override
	public void stop() {
		super.stop();
		if (effectlibEffect != null) effectlibEffect.cancel();
	}

	public Effect getEffectlibEffect() {
		return effectlibEffect;
	}

}
