package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.handlers.PotionEffectHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

public class PotionEffectCondition extends OperatorCondition {

	private static final Pattern PATTERN = Pattern.compile("^(?<potion>\\w+)(?:(?<op>[:=<>])(?<potency>\\d+))?$");

	private PotionEffectType effectType;
	private int amplifier;
	private boolean matchOnlyType;

	@Override
	public boolean initialize(@NotNull String var) {
		Matcher matcher = PATTERN.matcher(var);
		if (!matcher.find()) return false;
		String potion = matcher.group("potion");
		String op = matcher.group("op");
		String potency = matcher.group("potency");

		effectType = PotionEffectHandler.getPotionEffectType(potion);
		if (effectType == null) return false;

		if (op == null || potency == null) {
			matchOnlyType = true;
			return true;
		}
		if (!super.initialize(op)) return false;
		try {
			amplifier = Integer.parseInt(potency);
		} catch (NumberFormatException ignored) {
			return false;
		}
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return checkPotionEffects(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkPotionEffects(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean checkPotionEffects(LivingEntity target) {
		for (PotionEffect effect : target.getActivePotionEffects()) {
			if (effect.getType() != effectType) continue;
			if (matchOnlyType) return true;
			return compare(effect.getAmplifier(), amplifier);
		}
		return false;
	}

}
