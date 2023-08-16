package com.nisovin.magicspells.spells.targeted;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;
import com.nisovin.magicspells.events.MagicSpellsEntityDamageByEntityEvent;

public class CombustSpell extends TargetedSpell implements TargetedEntitySpell {

	private final Map<UUID, SpellData> combusting;

	private final ConfigData<Integer> fireTicks;
	private final ConfigData<Double> fireTickDamage;

	private final ConfigData<Boolean> checkPlugins;
	private final ConfigData<Boolean> preventImmunity;
	private final ConfigData<Boolean> powerAffectsFireTicks;
	private final ConfigData<Boolean> powerAffectsFireTickDamage;

	public CombustSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		fireTicks = getConfigDataInt("fire-ticks", 100);
		fireTickDamage = getConfigDataDouble("fire-tick-damage", 1);

		checkPlugins = getConfigDataBoolean("check-plugins", true);
		preventImmunity = getConfigDataBoolean("prevent-immunity", true);
		powerAffectsFireTicks = getConfigDataBoolean("power-affects-fire-ticks", true);
		powerAffectsFireTickDamage = getConfigDataBoolean("power-affects-fire-tick-damage", true);

		combusting = new HashMap<>();
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (data.hasCaster() && checkPlugins.get(data)) {
			MagicSpellsEntityDamageByEntityEvent event = new MagicSpellsEntityDamageByEntityEvent(data.caster(), data.target(), DamageCause.ENTITY_ATTACK, 1, this);
			if (!event.callEvent()) return noTarget(data);
		}

		int duration = fireTicks.get(data);
		if (powerAffectsFireTicks.get(data)) duration = Math.round(duration * data.power());
		data.target().setFireTicks(duration);

		combusting.put(data.target().getUniqueId(), data);

		playSpellEffects(data);

		MagicSpells.scheduleDelayedTask(() -> combusting.remove(data.target().getUniqueId()), duration + 2);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.FIRE_TICK) return;

		Entity entity = event.getEntity();
		if (!(entity instanceof LivingEntity target)) return;

		SpellData data = combusting.get(target.getUniqueId());
		if (data == null) return;

		double fireTickDamage = this.fireTickDamage.get(data);
		if (powerAffectsFireTickDamage.get(data)) fireTickDamage = fireTickDamage * data.power();

		EventUtil.call(new SpellApplyDamageEvent(this, data.caster(), target, fireTickDamage, DamageCause.FIRE_TICK, ""));
		event.setDamage(fireTickDamage);

		if (preventImmunity.get(data)) MagicSpells.scheduleDelayedTask(() -> target.setNoDamageTicks(0), 0);
	}

}
