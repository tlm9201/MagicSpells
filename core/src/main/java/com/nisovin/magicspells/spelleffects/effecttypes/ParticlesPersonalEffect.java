package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Vibration.Destination;
import org.bukkit.Particle.DustTransition;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ColorUtil;
import com.nisovin.magicspells.util.ConfigReaderUtil;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

import de.slikey.effectlib.util.VectorUtils;

public class ParticlesPersonalEffect extends ParticlesEffect {

	protected ConfigData<ParticlePosition> target;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		super.loadFromConfig(config);

		target = ConfigDataUtil.getEnum(config, "target", ParticlePosition.class, ParticlePosition.POSITION);
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		Player player = getTarget(entity, data);
		if (player == null) return null;

		Location location = applyOffsets(entity.getLocation(), data);
		if (staticLocation) return playEffectLocation(location, data);

		Particle particle = this.particle.get(data);
		Object particleData = getParticleData(particle, entity, location, data);

		player.spawnParticle(particle, location, count.get(data), xSpread.get(data), ySpread.get(data), zSpread.get(data), speed.get(data), particleData);

		return null;
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		Player player = getTarget(null, data);
		if (player == null) return null;

		Particle particle = this.particle.get(data);
		Object particleData = getParticleData(particle, location, data);

		player.spawnParticle(particle, location, count.get(data), xSpread.get(data), ySpread.get(data), zSpread.get(data), speed.get(data), particleData);

		return null;
	}

	private Player getTarget(Entity entity, SpellData data) {
		return switch (target.get(data)) {
			case CASTER -> {
				LivingEntity caster = data.caster();
				yield caster instanceof Player player ? player : null;
			}
			case TARGET -> {
				LivingEntity target = data.target();
				yield target instanceof Player player ? player : null;
			}
			case POSITION -> entity instanceof Player player ? player : null;
		};
	}

}
