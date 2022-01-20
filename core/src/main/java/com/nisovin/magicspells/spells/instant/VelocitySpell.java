package com.nisovin.magicspells.spells.instant;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedEntityFromLocationSpell;

public class VelocitySpell extends InstantSpell implements TargetedEntitySpell, TargetedEntityFromLocationSpell {

	private final Set<UUID> jumping;

	private double speed;

	private boolean cancelDamage;
	private boolean addVelocityInstead;

	public VelocitySpell(MagicConfig config, String spellName) {
		super(config, spellName);

		jumping = new HashSet<>();

		speed = getConfigFloat("speed", 40) / 10F;

		cancelDamage = getConfigBoolean("cancel-damage", true);
		addVelocityInstead = getConfigBoolean("add-velocity-instead", false);
	}

	private boolean launch(LivingEntity caster, LivingEntity target, Vector velocity) {
		if (target == null) return false;

		if (addVelocityInstead) target.setVelocity(target.getVelocity().add(velocity));
		else target.setVelocity(velocity);

		jumping.add(target.getUniqueId());
		if (caster != null) playSpellEffects(EffectPosition.CASTER, caster);

		return true;
	}

	private Vector getVelocity(Location location, float power) {
		return location.getDirection().normalize().multiply(speed * power);
	}

	@Override
	public PostCastAction castSpell(LivingEntity caster, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			launch(caster, caster, getVelocity(caster.getEyeLocation(), power));
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntityFromLocation(LivingEntity caster, Location from, LivingEntity target, float power) {
		return launch(caster, target, getVelocity(from, power));
	}

	@Override
	public boolean castAtEntityFromLocation(Location from, LivingEntity target, float power) {
		return launch(null, target, getVelocity(from, power));
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		return launch(caster, target, getVelocity(target.getEyeLocation(), power));
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		return launch(null, target, getVelocity(target.getEyeLocation(), power));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
		LivingEntity livingEntity = (LivingEntity) event.getEntity();
		if (!jumping.remove(livingEntity.getUniqueId())) return;
		playSpellEffects(EffectPosition.TARGET, livingEntity.getLocation());
		if (cancelDamage) event.setCancelled(true);
	}

	public Set<UUID> getJumping() {
		return jumping;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean shouldCancelDamage() {
		return cancelDamage;
	}

	public void setCancelDamage(boolean cancelDamage) {
		this.cancelDamage = cancelDamage;
	}

	public boolean shouldAddVelocityInstead() {
		return addVelocityInstead;
	}

	public void setAddVelocityInstead(boolean addVelocityInstead) {
		this.addVelocityInstead = addVelocityInstead;
	}

}
