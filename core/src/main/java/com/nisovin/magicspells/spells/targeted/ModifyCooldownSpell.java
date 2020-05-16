package com.nisovin.magicspells.spells.targeted;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellFilter;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class ModifyCooldownSpell extends TargetedSpell implements TargetedEntitySpell {

	private final SpellFilter filter;
	
	private final float seconds;
	private final float multiplier;
	
	public ModifyCooldownSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		seconds = getConfigFloat("seconds", 1F);
		multiplier = getConfigFloat("multiplier", 0F);

		List<String> spells = getConfigStringList("spells", null);
		List<String> deniedSpells = getConfigStringList("denied-spells", null);
		List<String> tagList = getConfigStringList("spell-tags", null);
		List<String> deniedTagList = getConfigStringList("denied-spell-tags", null);
		filter = new SpellFilter(spells, deniedSpells, tagList, deniedTagList);
	}

	@Override
	public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<LivingEntity> target = getTargetedEntity(livingEntity, power);
			if (target == null) return noTarget(livingEntity);
			modifyCooldowns(target.getTarget(), target.getPower());
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		modifyCooldowns(target, power);
		playSpellEffects(caster, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		modifyCooldowns(target, power);
		playSpellEffects(EffectPosition.TARGET, target);
		return true;
	}

	private void modifyCooldowns(LivingEntity target, float power) {
		float sec = seconds * power;
		float mult = multiplier * (1F / power);

		for (Spell spell : MagicSpells.spells()) {
		        if (!spell.onCooldown(target)) continue;
			if (!filter.check(spell)) continue;

			float cd = spell.getCooldown(target) - sec;
			if (mult > 0) cd *= mult;
			if (cd < 0) cd = 0;
			spell.setCooldown(target, cd, false);
		}
	}

}
