package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.ConfigDataUtil;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.events.MagicSpellsEntityDamageByEntityEvent;

public class GeyserSpell extends TargetedSpell implements TargetedEntitySpell {

	private ConfigData<Material> blockType;

	private ConfigData<Double> damage;
	private ConfigData<Double> velocity;

	private ConfigData<Integer> geyserHeight;
	private ConfigData<Integer> animationSpeed;

	private boolean ignoreArmor;
	private boolean checkPlugins;
	private boolean powerAffectsDamage;
	private boolean avoidDamageModification;

	public GeyserSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		damage = getConfigDataDouble("damage", 0);
		velocity = getConfigDataDouble("velocity", 10);

		geyserHeight = getConfigDataInt("geyser-height", 4);
		animationSpeed = getConfigDataInt("animation-speed", 2);

		ignoreArmor = getConfigBoolean("ignore-armor", false);
		checkPlugins = getConfigBoolean("check-plugins", true);
		powerAffectsDamage = getConfigBoolean("power-affects-damage", true);
		avoidDamageModification = getConfigBoolean("avoid-damage-modification", false);

		ConfigData<String> blockName = ConfigDataUtil.getString(getConfigString("geyser-type", "water"));
		blockType = (caster, target, power, args) -> {
			try {
				return Util.getMaterial(blockName.get(caster, target, power, args));
			} catch (Throwable ignored) {
				return null;
			}
		};

	}

	@Override
	public PostCastAction castSpell(LivingEntity caster, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			TargetInfo<LivingEntity> target = getTargetedEntity(caster, power, args);
			if (target.noTarget()) return noTarget(caster, args, target);

			boolean ok = geyser(caster, target.target(), target.power(), args);
			if (!ok) return noTarget(caster, args);

			playSpellEffects(caster, target.target(), target.power(), args);
			sendMessages(caster, target.target(), args);

			return PostCastAction.NO_MESSAGES;
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	private boolean geyser(LivingEntity caster, LivingEntity target, float power, String[] args) {
		SpellData spellData = new SpellData(caster, target, power, args);

		double damage = this.damage.get(spellData);
		if (powerAffectsDamage) damage *= power;
		
		if (caster != null && checkPlugins && damage > 0) {
			MagicSpellsEntityDamageByEntityEvent event = new MagicSpellsEntityDamageByEntityEvent(caster, target, DamageCause.ENTITY_ATTACK, damage, this);
			EventUtil.call(event);
			if (event.isCancelled()) return false;
			if (!avoidDamageModification) damage = event.getDamage();
		}
		
		if (damage > 0) {
			if (ignoreArmor) {
				double health = target.getHealth() - damage;
				if (health < 0) health = 0;
				target.setHealth(health);
				target.playEffect(EntityEffect.HURT);
			} else {
				if (caster != null) target.damage(damage, caster);
				else target.damage(damage);
			}
		}
		
		double velocity = this.velocity.get(spellData) / 10;
		if (velocity > 0) target.setVelocity(new Vector(0, velocity * power, 0));
		
		int geyserHeight = this.geyserHeight.get(spellData);
		if (geyserHeight > 0) {
			List<Entity> allNearby = target.getNearbyEntities(50, 50, 50);
			allNearby.add(target);

			List<Player> playersNearby = new ArrayList<>();
			for (Entity e : allNearby) {
				if (!(e instanceof Player)) continue;
				playersNearby.add((Player) e);
			}

			int animationSpeed = this.animationSpeed.get(spellData);

			Material blockType = this.blockType.get(spellData);
			new GeyserAnimation(blockType, target.getLocation(), playersNearby, animationSpeed, geyserHeight);
		}
		
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power, String[] args) {
		if (!validTargetList.canTarget(caster, target)) return false;
		geyser(caster, target, power, args);
		playSpellEffects(caster, target, power, args);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		return castAtEntity(caster, target, power, null);
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power, String[] args) {
		if (!validTargetList.canTarget(target)) return false;
		geyser(null, target, power, args);
		playSpellEffects(EffectPosition.TARGET, target, power, args);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		return castAtEntity(target, power, null);
	}

	private class GeyserAnimation extends SpellAnimation {

		private Material blockMaterial;
		private final List<Player> nearby;
		private final int geyserHeight;
		private final Location start;

		private GeyserAnimation(Material blockMaterial, Location start, List<Player> nearby, int animationSpeed, int geyserHeight) {
			super(0, animationSpeed, true);

			this.blockMaterial = blockMaterial;
			this.start = start;
			this.nearby = nearby;
			this.geyserHeight = geyserHeight;

			if (blockMaterial != null && !blockMaterial.isBlock()) this.blockMaterial = null;
		}

		@Override
		protected void onTick(int tick) {
			if (blockMaterial == null) {
				stop(true);
				return;
			}

			if (tick > geyserHeight << 1) {
				stop(true);
				return;
			}

			if (tick < geyserHeight) {
				Block block = start.clone().add(0, tick, 0).getBlock();
				if (!BlockUtils.isAir(block.getType())) return;
				for (Player p : nearby) p.sendBlockChange(block.getLocation(), blockMaterial.createBlockData());
				return;
			}

			int n = geyserHeight - (tick - geyserHeight) - 1;
			Block block = start.clone().add(0, n, 0).getBlock();
			for (Player p : nearby) p.sendBlockChange(block.getLocation(), block.getBlockData());
		}
		
	}

}
