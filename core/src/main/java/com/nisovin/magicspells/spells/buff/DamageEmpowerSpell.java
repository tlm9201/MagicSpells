package com.nisovin.magicspells.spells.buff;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.function.Supplier;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;

public class DamageEmpowerSpell extends BuffSpell {

	private final Map<UUID, DamageSuppliers> entities;

	private SpellFilter filter;

	private final ConfigData<Double> flatDamage;

	private final ConfigData<Float> damageMultiplier;

	private final ConfigData<Boolean> constantFlatDamage;
	private final ConfigData<Boolean> constantDamageMultiplier;


	public DamageEmpowerSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		flatDamage = getConfigDataDouble("flat-damage", 0D);
		damageMultiplier = getConfigDataFloat("damage-multiplier", 1.5F);
		constantFlatDamage = getConfigDataBoolean("constant-flat-damage", true);
		constantDamageMultiplier = getConfigDataBoolean("constant-damage-multiplier", true);
		filter = getConfigSpellFilter();

		entities = new HashMap<>();
	}

	@Override
	public boolean castBuff(SpellData data) {
		Supplier<Float> multiplierSupplier;
		if (constantDamageMultiplier.get(data)) {
			float damageMultiplier = this.damageMultiplier.get(data);
			multiplierSupplier = () -> damageMultiplier;
		} else multiplierSupplier = () -> damageMultiplier.get(data);

		Supplier<Double> flatDamageSupplier;
		if (constantFlatDamage.get(data)) {
			double flatDamage = this.flatDamage.get(data);
			flatDamageSupplier = () -> flatDamage;
		} else flatDamageSupplier = () -> flatDamage.get(data);

		DamageSuppliers record = new DamageSuppliers(multiplierSupplier, flatDamageSupplier);

		entities.put(data.target().getUniqueId(), record);

		return true;
	}

	@Override
	public boolean recastBuff(SpellData data) {
		stopEffects(data.target());
		return castBuff(data);
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpellApplyDamage(SpellApplyDamageEvent event) {
		LivingEntity caster = event.getCaster();
		if (!isActive(caster)) return;
		if (!filter.check(event.getSpell())) return;

		DamageSuppliers suppliers = entities.get(caster.getUniqueId());

		double flatDamage = suppliers.flatDamage().get();
		float damageMultiplier = suppliers.damageMultiplier().get();
		event.applyFlatDamage(flatDamage);
		event.applyDamageModifier(damageMultiplier);

		addUseAndChargeCost(caster);
	}

	public Map<UUID, DamageSuppliers> getEntities() {
		return entities;
	}

	public SpellFilter getFilter() {
		return filter;
	}

	public void setFilter(SpellFilter filter) {
		this.filter = filter;
	}

	public record DamageSuppliers(Supplier<Float> damageMultiplier, Supplier<Double> flatDamage) {}

}
