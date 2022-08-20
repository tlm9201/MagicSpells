package com.nisovin.magicspells.util;

import org.bukkit.entity.LivingEntity;

public record SpellData(LivingEntity caster, LivingEntity target, float power, String[] args) {

	public SpellData(LivingEntity caster, float power, String[] args) {
		this(caster, null, power, args);
	}

	public SpellData(LivingEntity caster) {
		this(caster, null, 1f, null);
	}

}
