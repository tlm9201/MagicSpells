package com.nisovin.magicspells.spelleffects.util;

import de.slikey.effectlib.Effect;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.spelleffects.effecttypes.EffectLibEffect;

public class EffectlibSpellEffect {

	@NotNull
	private Effect effect;

	@NotNull
	private EffectLibEffect spellEffect;

	public EffectlibSpellEffect(@NotNull Effect effect, @NotNull EffectLibEffect spellEffect) {
		this.effect = effect;
		this.spellEffect = spellEffect;
	}

	@NotNull
	public Effect getEffect() {
		return effect;
	}

	public void setEffect(@NotNull Effect effect) {
		this.effect = effect;
	}

	@NotNull
	public EffectLibEffect getSpellEffect() {
		return spellEffect;
	}

	public void setSpellEffect(@NotNull EffectLibEffect spellEffect) {
		this.spellEffect = spellEffect;
	}

}
