package com.nisovin.magicspells.spells.targeted;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;

import org.apache.commons.math3.util.FastMath;

public class PotionEffectSpell extends TargetedSpell implements TargetedEntitySpell {

	private Set<PotionEffect> potionEffects;

	private List<String> potionEffectData;

	private PotionEffectType type;
	private PotionEffect potionEffect;

	private int duration;
	private int strength;

	private boolean icon;
	private boolean hidden;
	private boolean ambient;

	private boolean override;
	private boolean spellPowerAffectsDuration;
	private boolean spellPowerAffectsStrength;
	
	public PotionEffectSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		//Format: <PotionType> <Duration> <Strength> <Hidden> <Ambient> <Icon>
		potionEffectData = getConfigStringList("potion-effects", new ArrayList<>());
		potionEffects = new HashSet<>();

		type = Util.getPotionEffectType(getConfigString("type", "speed"));

		duration = getConfigInt("duration", 0);
		strength = getConfigInt("strength", 0);

		icon = getConfigBoolean("icon", true);
		hidden = getConfigBoolean("hidden", false);
		ambient = getConfigBoolean("ambient", false);
		override = getConfigBoolean("override", false);

		spellPowerAffectsDuration = getConfigBoolean("spell-power-affects-duration", true);
		spellPowerAffectsStrength = getConfigBoolean("spell-power-affects-strength", true);
	}

	@Override
	public void initialize() {
		super.initialize();

		if (type != null) potionEffect = new PotionEffect(type, duration, strength, ambient, !hidden, icon);

		if (potionEffectData.isEmpty()) return;

		PotionEffect p;

		PotionEffectType t = null;
		int d = 0;
		int s = 0;
		boolean h = false;
		boolean a = false;
		boolean i = true;

		for (String str : potionEffectData) {
			String[] args = str.split(" ");

			if (args.length <= 0) continue;

			if (args.length >= 1) t = Util.getPotionEffectType(args[0]);
			if (args.length >= 2) d = Integer.parseInt(args[1]);
			if (args.length >= 3) s = Integer.parseInt(args[2]);
			if (args.length >= 4) h = Boolean.parseBoolean(args[3]);
			if (args.length >= 5) a = Boolean.parseBoolean(args[4]);
			if (args.length >= 6) i = Boolean.parseBoolean(args[5]);

			if (t == null) continue;
			p = new PotionEffect(t, d, s, h, a, i);
			potionEffects.add(p);
		}

	}

	public Set<PotionEffect> getPotionEffects() {
		return potionEffects;
	}
	
	public PotionEffectType getPotionType() {
		return type;
	}
	
	public int getDuration() {
		return duration;
	}

	@Override
	public PostCastAction castSpell(LivingEntity caster, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<LivingEntity> targetInfo = getTargetedEntity(caster, power);
			if (targetInfo == null) return noTarget(caster);

			LivingEntity target = targetInfo.getTarget();

			handlePotionEffects(caster, target, power);
			playSpellEffects(caster, target);
			sendMessages(caster, target, args);

			return PostCastAction.NO_MESSAGES;
		}		
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		if (!validTargetList.canTarget(caster, target)) return false;
		handlePotionEffects(caster, target, power);
		playSpellEffects(caster, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (!validTargetList.canTarget(target)) return false;
		handlePotionEffects(null, target, power);
		playSpellEffects(EffectPosition.TARGET, target);
		return true;
	}

	private void handlePotionEffects(LivingEntity caster, LivingEntity target, float power) {
		if (potionEffects.isEmpty()) {
			applyPotionEffect(caster, target, potionEffect, power);
			return;
		}

		for (PotionEffect effect : potionEffects) {
			applyPotionEffect(caster, target, effect, power);
		}
	}

	private void applyPotionEffect(LivingEntity caster, LivingEntity target, PotionEffect effect, float power) {
		if (effect == null) return;

		DamageCause cause = null;
		if (effect.getType() == PotionEffectType.POISON) cause = DamageCause.POISON;
		else if (effect.getType() == PotionEffectType.WITHER) cause = DamageCause.WITHER;

		int d = spellPowerAffectsDuration ? FastMath.round(effect.getDuration() * power) : effect.getDuration();
		int s = spellPowerAffectsStrength ? FastMath.round(effect.getAmplifier() * power) : effect.getAmplifier();

		if (cause != null) EventUtil.call(new SpellApplyDamageEvent(this, caster, target, s, cause, ""));

		if (override && target.hasPotionEffect(effect.getType())) target.removePotionEffect(effect.getType());

		target.addPotionEffect(new PotionEffect(effect.getType(), d, s, effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
	}

}
