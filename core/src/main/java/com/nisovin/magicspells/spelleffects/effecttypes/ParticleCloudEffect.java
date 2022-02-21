package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class ParticleCloudEffect extends SpellEffect {

	private ConfigData<Particle> particle;

	private ConfigData<BlockData> blockData;
	private ConfigData<Material> material;
	private ConfigData<DustOptions> dustOptions;

	private ConfigData<Integer> color;
	private ConfigData<Integer> duration;

	private ConfigData<Float> radius;
	private ConfigData<Float> yOffset;
	private ConfigData<Float> radiusPerTick;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		particle = ConfigDataUtil.getParticle(config, "particle-name", Particle.EXPLOSION_NORMAL);

		blockData = ConfigDataUtil.getBlockData(config, "material", null);
		material = ConfigDataUtil.getEnum(config, "material", Material.class, null);
		dustOptions = ConfigDataUtil.getDustOptions(config, "color", "size", new DustOptions(Color.RED, 1));

		color = ConfigDataUtil.getInteger(config, "color", 0xFF0000);
		duration = ConfigDataUtil.getInteger(config, "duration", 60);

		radius = ConfigDataUtil.getFloat(config, "radius", 5);
		yOffset = ConfigDataUtil.getFloat(config, "y-offset", 0);
		radiusPerTick = ConfigDataUtil.getFloat(config, "radius-per-tick", 0);
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		Particle particle = this.particle.get(data);
		if (particle == null) return null;

		AreaEffectCloud cloud = location.getWorld().spawn(location.clone().add(0, yOffset.get(data), 0), AreaEffectCloud.class);
		cloud.setColor(Color.fromRGB(color.get(data)));
		cloud.setRadius(radius.get(data));
		cloud.setDuration(duration.get(data));
		cloud.setRadiusPerTick(radiusPerTick.get(data));

		if (particle.getDataType() == ItemStack.class) {
			Material material = this.material.get(data);
			cloud.setParticle(particle, material == null || !material.isItem() ? null : new ItemStack(material));
		} else if (blockData != null && particle.getDataType() == BlockData.class) cloud.setParticle(particle, blockData.get(data));
		else if (particle.getDataType() == DustOptions.class) cloud.setParticle(particle, dustOptions.get(data));
		else cloud.setParticle(particle);

		return null;
	}

}
