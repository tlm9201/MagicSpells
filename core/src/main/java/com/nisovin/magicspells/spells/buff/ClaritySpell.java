package com.nisovin.magicspells.spells.buff;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellFilter;
import com.nisovin.magicspells.util.SpellReagents;
import com.nisovin.magicspells.events.SpellCastEvent;

public class ClaritySpell extends BuffSpell {

	private final Map<UUID, Float> entities;

	private float multiplier;

	private SpellFilter filter;

	public ClaritySpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		multiplier = getConfigFloat("multiplier", 0.5F);

		List<String> spells = getConfigStringList("spells", null);
		List<String> deniedSpells = getConfigStringList("denied-spells", null);
		List<String> tagList = getConfigStringList("spell-tags", null);
		List<String> deniedTagList = getConfigStringList("denied-spell-tags", null);
		filter = new SpellFilter(spells, deniedSpells, tagList, deniedTagList);

		entities = new HashMap<>();
	}

	@Override
	public boolean castBuff(LivingEntity entity, float power, String[] args) {
		entities.put(entity.getUniqueId(), power);
		return true;
	}

	@Override
	public boolean isActive(LivingEntity entity) {
		return entities.containsKey(entity.getUniqueId());
	}

	@Override
	protected void turnOffBuff(LivingEntity entity) {
		entities.remove(entity.getUniqueId());
	}

	@Override
	protected void turnOff() {
		entities.clear();
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpellCast(SpellCastEvent event) {
		LivingEntity caster = event.getCaster();
		if (!isActive(caster)) return;
		if (!filter.check(event.getSpell())) return;

		float mod = multiplier;
		float power = entities.get(caster.getUniqueId());

		if (multiplier < 1) mod *= 1 / power;
		else if (multiplier > 1) mod *= power;

		SpellReagents reagents = event.getReagents();
		if (reagents != null) event.setReagents(reagents.multiply(mod));
		
		addUseAndChargeCost(caster);
	}

	public Map<UUID, Float> getEntities() {
		return entities;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	public SpellFilter getFilter() {
		return filter;
	}

	public void setFilter(SpellFilter filter) {
		this.filter = filter;
	}

}
