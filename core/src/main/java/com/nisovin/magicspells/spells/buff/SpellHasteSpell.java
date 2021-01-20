package com.nisovin.magicspells.spells.buff;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.SpellFilter;
import com.nisovin.magicspells.events.SpellCastEvent;

public class SpellHasteSpell extends BuffSpell {

	private final Map<UUID, Float> entities;

	private float castTimeModAmt;
	private float cooldownModAmt;

	private SpellFilter filter;

	public SpellHasteSpell(MagicConfig config, String spellName) {
		super(config, spellName);
	
		castTimeModAmt = getConfigInt("cast-time-mod-amt", -25) / 100F;
		cooldownModAmt = getConfigInt("cooldown-mod-amt", -25) / 100F;
	
		entities = new HashMap<>();

		List<String> spells = getConfigStringList("spells", null);
		List<String> deniedSpells = getConfigStringList("denied-spells", null);
		List<String> tagList = getConfigStringList("spell-tags", null);
		List<String> deniedTagList = getConfigStringList("denied-spell-tags", null);
	
		filter = new SpellFilter(spells, deniedSpells, tagList, deniedTagList);
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
	public void turnOffBuff(LivingEntity entity) {
		entities.remove(entity.getUniqueId());
	}

	@Override
	protected void turnOff() {
		entities.clear();
	}

	@EventHandler (priority=EventPriority.MONITOR)
	public void onSpellSpeedCast(SpellCastEvent event) {
		if (!filter.check(event.getSpell())) return;
		if (!isActive(event.getCaster())) return;
		
		Float power = entities.get(event.getCaster().getUniqueId());
		if (power == null) return;

		if (castTimeModAmt != 0) {
			int ct = event.getCastTime();
			float newCT = ct + (castTimeModAmt * power * ct);
			if (newCT < 0) newCT = 0;
			event.setCastTime(Math.round(newCT));
		}

		if (cooldownModAmt != 0) {
			float cd = event.getCooldown();
			float newCD = cd + (cooldownModAmt * power * cd);
			if (newCD < 0) newCD = 0;
			event.setCooldown(newCD);
		}

	}

	public Map<UUID, Float> getEntities() {
		return entities;
	}

	public float getCastTimeModAmt() {
		return castTimeModAmt;
	}

	public void setCastTimeModAmt(float castTimeModAmt) {
		this.castTimeModAmt = castTimeModAmt;
	}

	public float getCooldownModAmt() {
		return cooldownModAmt;
	}

	public void setCooldownModAmt(float cooldownModAmt) {
		this.cooldownModAmt = cooldownModAmt;
	}

	public SpellFilter getFilter() {
		return filter;
	}

	public void setFilter(SpellFilter filter) {
		this.filter = filter;
	}

}
