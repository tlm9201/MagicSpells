package com.nisovin.magicspells.spells.instant;

import org.bukkit.util.Vector;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class VelocitySpell extends InstantSpell {

	private final double speed;
	private final boolean addVelocityInstead;

	public VelocitySpell(MagicConfig config, String spellName) {
		super(config, spellName);

		speed = getConfigFloat("speed", 40) / 10F;
		addVelocityInstead = getConfigBoolean("add-velocity-instead", false);
	}

	@Override
	public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Vector v = livingEntity.getEyeLocation().getDirection().normalize().multiply(speed * power);
			if (addVelocityInstead) livingEntity.setVelocity(livingEntity.getVelocity().add(v));
			else livingEntity.setVelocity(v);
			playSpellEffects(EffectPosition.CASTER, livingEntity);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

}
