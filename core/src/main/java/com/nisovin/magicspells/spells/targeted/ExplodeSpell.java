package com.nisovin.magicspells.spells.targeted;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Animals;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class ExplodeSpell extends TargetedSpell implements TargetedLocationSpell {

	private final ConfigData<Integer> backfireChance;

	private final ConfigData<Float> explosionSize;
	private final ConfigData<Float> damageMultiplier;

	private final ConfigData<Boolean> addFire;
	private final ConfigData<Boolean> simulateTnt;
	private final ConfigData<Boolean> ignoreCancelled;
	private final ConfigData<Boolean> preventBlockDamage;
	private final ConfigData<Boolean> preventPlayerDamage;
	private final ConfigData<Boolean> preventAnimalDamage;
	private final ConfigData<Boolean> powerAffectsExplosionSize;
	private final ConfigData<Boolean> powerAffectsDamageMultiplier;

	private SpellData currentData = null;
	private long currentTick = 0;

	public ExplodeSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		backfireChance = getConfigDataInt("backfire-chance", 0);

		explosionSize = getConfigDataFloat("explosion-size", 4);
		damageMultiplier = getConfigDataFloat("damage-multiplier", 0);

		addFire = getConfigDataBoolean("add-fire", false);
		simulateTnt = getConfigDataBoolean("simulate-tnt", true);
		ignoreCancelled = getConfigDataBoolean("ignore-cancelled", false);
		preventBlockDamage = getConfigDataBoolean("prevent-block-damage", false);
		preventPlayerDamage = getConfigDataBoolean("prevent-player-damage", false);
		preventAnimalDamage = getConfigDataBoolean("prevent-animal-damage", false);
		powerAffectsExplosionSize = getConfigDataBoolean("power-affects-explosion-size", true);
		powerAffectsDamageMultiplier = getConfigDataBoolean("power-affects-damage-multiplier", true);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Location> info = getTargetedBlockLocation(data, false);
		if (info.noTarget()) return noTarget(info);

		return castAtLocation(info.spellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		boolean ignoreCancelled = this.ignoreCancelled.get(data);

		float explosionSize = this.explosionSize.get(data);
		if (powerAffectsExplosionSize.get(data)) explosionSize *= data.power();

		Location location = data.location();

		if (simulateTnt.get(data)) {
			boolean cancelled = MagicSpells.getVolatileCodeHandler().simulateTnt(location, data.caster(), explosionSize, addFire.get(data));
			if (cancelled) {
				if (!ignoreCancelled) return noTarget(data);
				return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
			}
		}

		int backfireChance = this.backfireChance.get(data);
		if (backfireChance > 0 && random.nextInt(10000) < backfireChance) {
			location = data.caster().getLocation();
			data = data.location(location);
		}

		currentTick = Bukkit.getWorlds().get(0).getFullTime();
		currentData = data;

		boolean success = location.createExplosion(data.caster(), explosionSize, addFire.get(data), !preventBlockDamage.get(data));
		if (success) playSpellEffects(data);

		return success || ignoreCancelled ? new CastResult(PostCastAction.HANDLE_NORMALLY, data) : noTarget(data);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getCause() != DamageCause.ENTITY_EXPLOSION) return;

		if (currentTick != Bukkit.getWorlds().get(0).getFullTime() || !currentData.caster().equals(event.getDamager()))
			return;

		SpellData data = currentData.target(event.getEntity() instanceof LivingEntity le ? le : null);

		float damageMultiplier = this.damageMultiplier.get(data);
		boolean preventPlayerDamage = this.preventPlayerDamage.get(data);
		boolean preventAnimalDamage = this.preventAnimalDamage.get(data);
		if (!(damageMultiplier > 0 || preventPlayerDamage || preventAnimalDamage)) return;

		if (preventPlayerDamage && event.getEntity() instanceof Player) event.setCancelled(true);
		else if (preventAnimalDamage && event.getEntity() instanceof Animals) event.setCancelled(true);
		else if (damageMultiplier > 0) {
			if (powerAffectsDamageMultiplier.get(data)) damageMultiplier *= data.power();
			event.setDamage(damageMultiplier);
		}
	}

}
