package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class RiptideSpell extends TargetedSpell implements TargetedEntitySpell {

	private int duration;

	public RiptideSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		duration = getConfigInt("duration", 40);
	}

	@Override
	public PostCastAction castSpell(LivingEntity caster, SpellCastState state, float power, String[] args) {
		if (state != SpellCastState.NORMAL) return PostCastAction.HANDLE_NORMALLY;

		TargetInfo<Player> targetInfo = getTargetedPlayer(caster, power);
		if (targetInfo == null) return noTarget(caster);

		Player target = targetInfo.getTarget();
		if (target == null) return noTarget(caster);

		MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(target, duration);
		playSpellEffects(caster, target);

		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		if (target instanceof Player player) {
			MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(player, duration);
			playSpellEffects(caster, target);
			return true;
		}

		return false;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (target instanceof Player player) {
			MagicSpells.getVolatileCodeHandler().startAutoSpinAttack(player, duration);
			playSpellEffects(EffectPosition.TARGET, target);
			return true;
		}

		return false;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
