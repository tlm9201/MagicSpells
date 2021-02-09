package com.nisovin.magicspells.spelleffects.util;

import de.slikey.effectlib.Effect;

import com.nisovin.magicspells.spelleffects.effecttypes.EffectLibEffect;

public class EffectlibSpellEffect {

	private Effect effect;

	private EffectLibEffect spellEffect;

	public EffectlibSpellEffect(Effect effect, EffectLibEffect spellEffect) {
		this.effect = effect;
		this.spellEffect = spellEffect;
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public EffectLibEffect getSpellEffect() {
		return spellEffect;
	}

	public void setSpellEffect(EffectLibEffect spellEffect) {
		this.spellEffect = spellEffect;
	}

}
