package com.nisovin.magicspells.spells.targeted;

import com.nisovin.magicspells.MagicSpells;
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

import java.util.ArrayList;
import java.util.List;

public class PotionEffectSpell extends TargetedSpell implements TargetedEntitySpell {

	private List<PotionEffect> potionEffects;
	private List<String> potionEffectStrings;

	private PotionEffectType type;

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

		potionEffectStrings = getConfigStringList("potion-effects", null);

		type = Util.getPotionEffectType(getConfigString("type", "1"));

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

		if (potionEffectStrings == null) return;

		potionEffects = new ArrayList<>();
		for (String potionEffectString : potionEffectStrings) {
			String[] data = potionEffectString.split(" ");
			if (data.length == 0) continue;

			PotionEffectType type = Util.getPotionEffectType(data[0]);
			if (type == null) {
				MagicSpells.error("Invalid potion effect string '" + potionEffectString + "' in PotionEffectSpell '" + internalName + "'.");
				continue;
			}

			int duration = 0;
			if (data.length >= 2) {
				try {
					duration = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					MagicSpells.error("Invalid duration '" + duration + "' in potion effect string '" + potionEffectString + "' in PotionEffectSpell '" + internalName + "'.");
					continue;
				}
			}

			int strength = 0;
			if (data.length >= 2) {
				try {
					strength = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					MagicSpells.error("Invalid strength '" + strength + "' in potion effect string '" + potionEffectString + "' in PotionEffectSpell '" + internalName + "'.");
					continue;
				}
			}

			boolean hidden = false;
			if (data.length >= 3) hidden = Boolean.parseBoolean(data[2]);

			boolean ambient = false;
			if (data.length >= 4) hidden = Boolean.parseBoolean(data[3]);

			boolean icon = true;
			if (data.length >= 5) icon = Boolean.parseBoolean(data[4]);

			if (data.length > 5)
				MagicSpells.error("Trailing data found in potion effect string '" + potionEffectString + "' in PotionEffectSpell '" + internalName + "'.");

			potionEffects.add(new PotionEffect(type, duration, strength, hidden, ambient, icon));
		}

		if (potionEffects.isEmpty()) potionEffects = null;
		potionEffectStrings = null;
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

			handlePotionEffects(caster, target, power, args);
			playSpellEffects(caster, target);
			sendMessages(caster, target, args);

			return PostCastAction.NO_MESSAGES;
		}		
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power, String[] args) {
		if (!validTargetList.canTarget(caster, target)) return false;
		handlePotionEffects(caster, target, power, args);
		playSpellEffects(caster, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		return castAtEntity(caster, target, power, null);
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power, String[] args) {
		if (!validTargetList.canTarget(target)) return false;
		handlePotionEffects(null, target, power, args);
		playSpellEffects(EffectPosition.TARGET, target);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		return castAtEntity(target, power, null);
	}

	public void handlePotionEffects(LivingEntity caster, LivingEntity target, float power, String[] args) {
		if (potionEffects == null) {
			int duration = spellPowerAffectsDuration ? Math.round(this.duration * power) : this.duration;
			int strength = spellPowerAffectsStrength ? Math.round(this.strength * power) : this.strength;
			PotionEffect effect = new PotionEffect(type, duration, strength, ambient, !hidden, icon);

			callDamageEvent(caster, target, effect);

			if (override && target.hasPotionEffect(type)) target.removePotionEffect(type);
			target.addPotionEffect(effect);

			return;
		}

		for (PotionEffect effect : potionEffects) {
			if (spellPowerAffectsDuration || spellPowerAffectsStrength) {
				int duration = effect.getDuration();
				if (spellPowerAffectsDuration) duration = Math.round(duration * power);

				int strength = effect.getAmplifier();
				if (spellPowerAffectsStrength) strength = Math.round(strength * power);

				effect = new PotionEffect(effect.getType(), duration, strength, effect.isAmbient(), effect.hasParticles(), icon);
			}

			callDamageEvent(caster, target, effect);

			if (override && target.hasPotionEffect(effect.getType())) target.removePotionEffect(effect.getType());
			target.addPotionEffect(effect);
		}
	}

	private void callDamageEvent(LivingEntity caster, LivingEntity target, PotionEffect effect) {
		DamageCause cause = null;
		if (effect.getType() == PotionEffectType.POISON) cause = DamageCause.POISON;
		else if (effect.getType() == PotionEffectType.WITHER) cause = DamageCause.WITHER;

		new SpellApplyDamageEvent(this, caster, target, effect.getAmplifier(), cause, "").callEvent();
	}

}
