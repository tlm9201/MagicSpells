package com.nisovin.magicspells.spelleffects.trackers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.ModifiedEffect;

import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.spelleffects.SpellEffect.SpellEffectActiveChecker;

public class BuffEffectlibTracker extends AsyncEffectTracker implements Runnable {

	private final Effect effectlibEffect;

	public BuffEffectlibTracker(Entity entity, SpellEffectActiveChecker checker, SpellEffect effect) {
		super(entity, checker, effect);

		effectlibEffect = effect.playEffectLib(entity.getLocation());
		if (effectlibEffect != null) effectlibEffect.infinite();
	}

	@Override
	public void run() {
		if (!canRun()) {
			stop();
			return;
		}

		Location entityLoc = effect.applyOffsets(entity.getLocation().clone());

		effectlibEffect.setLocation(entityLoc);
		if (effectlibEffect instanceof ModifiedEffect) {
			Effect modifiedEffect = ((ModifiedEffect) effectlibEffect).getInnerEffect();
			if (modifiedEffect != null) modifiedEffect.setLocation(entityLoc);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (effectlibEffect != null) effectlibEffect.cancel();
	}

	public Effect getEffectlibEffect() {
		return effectlibEffect;
	}

	public boolean canRun() {
		if (entity == null) return false;
		if (!entity.isValid()) return false;
		if (!checker.isActive(entity)) return false;
		if (effect == null) return false;
		if (effectlibEffect == null) return false;
		return true;
	}

}
