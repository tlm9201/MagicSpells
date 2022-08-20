package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class RiptideSpell extends TargetedSpell implements TargetedEntitySpell {

	private ConfigData<Integer> duration;

	public RiptideSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		duration = getConfigDataInt("duration", 40);
	}

	@Override
	public PostCastAction castSpell(LivingEntity caster, SpellCastState state, float power, String[] args) {
		if (state != SpellCastState.NORMAL) return PostCastAction.HANDLE_NORMALLY;

		TargetInfo<Player> targetInfo = getTargetedPlayer(caster, power, args);
		if (targetInfo == null) return noTarget(caster);

		Player target = targetInfo.getTarget();
		if (target == null) return noTarget(caster);

		power = targetInfo.getPower();

		MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(target, duration.get(caster, target, power, args));
		playSpellEffects(caster, target, power, args);

		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power, String[] args) {
		if (target instanceof Player player && validTargetList.canTarget(caster, target)) {
			MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(player, duration.get(caster, target, power, args));
			playSpellEffects(caster, target, power, args);
			return true;
		}

		return false;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		if (target instanceof Player player && validTargetList.canTarget(caster, target)) {
			MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(player, duration.get(caster, target, power, null));
			playSpellEffects(caster, target, power, null);
			return true;
		}

		return false;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power, String[] args) {
		if (target instanceof Player player && validTargetList.canTarget(target)) {
			MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(player, duration.get(null, target, power, args));
			playSpellEffects(EffectPosition.TARGET, target, power, args);
			return true;
		}

		return false;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (target instanceof Player player && validTargetList.canTarget(target)) {
			MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(player, duration.get(null, target, power, null));
			playSpellEffects(EffectPosition.TARGET, target, power, null);
			return true;
		}

		return false;
	}

}
