package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.*;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Vibration.Destination;
import org.bukkit.Particle.DustTransition;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ColorUtil;
import com.nisovin.magicspells.util.ConfigReaderUtil;
import com.nisovin.magicspells.spelleffects.SpellEffect;

import de.slikey.effectlib.util.VectorUtils;

public class ParticlesPersonalEffect extends SpellEffect {

	private Particle particle;
	private String particleName;

	private Material material;
	private String materialName;

	private BlockData blockData;
	private ItemStack itemStack;


	private int shriekDelay;
	private float sculkChargeRotation;

	private float dustSize;
	private String colorHex;
	private String toColorHex;
	private Color dustColor;
	private Color toDustColor;
	private DustOptions dustOptions;
	private DustTransition dustTransition;

	private int arrivalTime;
	private Vibration vibrationOptions;
	private Destination vibrationDestination;
	private Vector vibrationOffset;
	private Vector vibrationRelativeOffset;

	private int count;
	private float speed;
	private float xSpread;
	private float ySpread;
	private float zSpread;

	private boolean none = true;
	private boolean item = false;
	private boolean dust = false;
	private boolean block = false;
	private boolean shriek = false;
	private boolean vibration = false;
	private boolean sculkCharge = false;
	private boolean transitionDust = false;

	@Override
	public void loadFromConfig(ConfigurationSection config) {

		particleName = config.getString("particle-name", "EXPLOSION_NORMAL");
		particle = Util.getParticle(particleName);

		materialName = config.getString("material", "");
		material = Util.getMaterial(materialName);

		count = config.getInt("count", 5);
		speed = (float) config.getDouble("speed", 0.2F);
		xSpread = (float) config.getDouble("horiz-spread", 0.2F);
		ySpread = (float) config.getDouble("vert-spread", 0.2F);
		zSpread = xSpread;
		xSpread = (float) config.getDouble("x-spread", xSpread);
		ySpread = (float) config.getDouble("y-spread", ySpread);
		zSpread = (float) config.getDouble("z-spread", zSpread);

		dustSize = (float) config.getDouble("size", 1);
		colorHex = config.getString("color", "FF0000");
		toColorHex = config.getString("to-color", "000000");
		dustColor = ColorUtil.getColorFromHexString(colorHex);
		toDustColor = ColorUtil.getColorFromHexString(toColorHex);

		arrivalTime = config.getInt("arrival-time", 10);
		vibrationOffset = ConfigReaderUtil.readVector(config.getString("vibration-offset", "0,0,0"));
		vibrationRelativeOffset = ConfigReaderUtil.readVector(config.getString("vibration-relative-offset", "0,0,0"));

		shriekDelay = config.getInt("shriek-delay", 0);
		sculkChargeRotation = (float) config.getDouble("sculk-charge-rotation", 0.0);

		if (dustColor != null) dustOptions = new DustOptions(dustColor, dustSize);
		if (dustColor != null && toDustColor != null) dustTransition = new DustTransition(dustColor, toDustColor, dustSize);

		if ((particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST || particle == Particle.FALLING_DUST) && material != null && material.isBlock()) {
			block = true;
			blockData = material.createBlockData();
			none = false;
		} else if (particle == Particle.ITEM_CRACK && material != null && material.isItem()) {
			item = true;
			itemStack = new ItemStack(material);
			none = false;
		} else if (particle == Particle.REDSTONE && dustOptions != null) {
			dust = true;
			none = false;
		}  else if (particle == Particle.DUST_COLOR_TRANSITION && dustTransition != null) {
			transitionDust = true;
			none = false;
		} else if (particle == Particle.VIBRATION && arrivalTime >= 0) {
			vibration = true;
			none = false;
		} else if (particle == Particle.SHRIEK) {
			shriek = true;
			none = false;
		} else if (particle == Particle.SCULK_CHARGE) {
			sculkCharge = true;
			none = false;
		}

		if (particle == null) MagicSpells.error("Wrong particle-name defined! '" + particleName + "'");

		if ((particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST || particle == Particle.FALLING_DUST) && (material == null || !material.isBlock())) {
			particle = null;
			MagicSpells.error("Wrong material defined! '" + materialName + "'");
		}

		if (particle == Particle.ITEM_CRACK && (material == null || !material.isItem())) {
			particle = null;
			MagicSpells.error("Wrong material defined! '" + materialName + "'");
		}

		if (particle == Particle.REDSTONE && dustColor == null) {
			particle = null;
			MagicSpells.error("Wrong color defined! '" + colorHex + "'");
		}

		if (particle == Particle.DUST_COLOR_TRANSITION && dustTransition == null) {
			particle = null;
			MagicSpells.error("Wrong transition colors defined! '" + colorHex + "', '" + toColorHex + "'");
		}
	}

	@Override
	public Runnable playEffectEntity(Entity entity) {
		super.playEffectEntity(entity);
		if (particle == null) return null;
		if (!(entity instanceof Player)) return null;

		Location loc = entity.getLocation().clone();
		if (getOffset().getX() != 0 || getOffset().getY() != 0 || getOffset().getZ() != 0) loc.add(getOffset());
		if (getRelativeOffset().getX() != 0 || getRelativeOffset().getY() != 0 || getRelativeOffset().getZ() != 0) loc.add(VectorUtils.rotateVector(getRelativeOffset(), loc));
		if (getZOffset() != 0) {
			Vector locDirection = loc.getDirection().normalize();
			Vector horizOffset = new Vector(-locDirection.getZ(), 0.0, locDirection.getX()).normalize();
			loc.add(horizOffset.multiply(getZOffset())).getBlock().getLocation();
		}
		if (getHeightOffset() != 0) loc.setY(loc.getY() + getHeightOffset());
		if (getForwardOffset() != 0) loc.add(loc.getDirection().setY(0).normalize().multiply(getForwardOffset()));

		if (block) ((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed, blockData);
		else if (item) ((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed, itemStack);
		else if (dust) ((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed, dustOptions);
		else if (transitionDust) ((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed, dustTransition);
		else if (none) ((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed);
		else if (shriek) ((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed, shriekDelay);
		else if (sculkCharge) ((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed, sculkChargeRotation);
		else if (vibration) {
			vibrationDestination = new Vibration.Destination.BlockDestination(applyOffsets(loc.clone(), vibrationOffset, vibrationRelativeOffset, 0D, 0D, 0D));
			vibrationOptions = new Vibration(vibrationDestination, arrivalTime);
			((Player) entity).spawnParticle(particle, loc, count, xSpread, ySpread, zSpread, speed, vibrationOptions);
		}

		return null;
	}

}
