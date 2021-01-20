package com.nisovin.magicspells.spells.buff;

import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.HashSet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellFilter;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;

public class DamageEmpowerSpell extends BuffSpell {

	private final Set<UUID> entities;

	private SpellFilter filter;

	private float damageMultiplier;

	public DamageEmpowerSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		damageMultiplier = getConfigFloat("damage-multiplier", 1.5F);

		List<String> spells = getConfigStringList("spells", null);
		List<String> deniedSpells = getConfigStringList("denied-spells", null);
		List<String> tagList = getConfigStringList("spell-tags", null);
		List<String> deniedTagList = getConfigStringList("denied-spell-tags", null);

		filter = new SpellFilter(spells, deniedSpells, tagList, deniedTagList);

		entities = new HashSet<>();
	}

	@Override
	public boolean castBuff(LivingEntity entity, float power, String[] args) {
		entities.add(entity.getUniqueId());
		return true;
	}

	@Override
	public boolean isActive(LivingEntity entity) {
		return entities.contains(entity.getUniqueId());
	}

	@Override
	protected void turnOffBuff(LivingEntity entity) {
		entities.remove(entity.getUniqueId());
	}

	@Override
	protected void turnOff() {
		entities.clear();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpellApplyDamage(SpellApplyDamageEvent event) {
		LivingEntity caster = event.getCaster();
		if (!isActive(caster)) return;
		if (!filter.check(event.getSpell())) return;

		addUseAndChargeCost(caster);
		event.applyDamageModifier(damageMultiplier);
	}

	public Set<UUID> getEntities() {
		return entities;
	}

	public float getDamageMultiplier() {
		return damageMultiplier;
	}

	public void setDamageMultiplier(float damageMultiplier) {
		this.damageMultiplier = damageMultiplier;
	}

	public SpellFilter getFilter() {
		return filter;
	}

	public void setFilter(SpellFilter filter) {
		this.filter = filter;
	}

}
