package com.nisovin.magicspells.spells.targeted;

import java.util.Set;
import java.util.List;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.MagicSpellsBlockBreakEvent;
import com.nisovin.magicspells.spells.TargetedEntityFromLocationSpell;

public class DestroySpell extends TargetedSpell implements TargetedLocationSpell, TargetedEntityFromLocationSpell {

	private Set<Material> blockTypesToThrow;
	private Set<Material> blockTypesToRemove;
	private Set<FallingBlock> fallingBlocks;

	private final ConfigData<Integer> vertRadius;
	private final ConfigData<Integer> horizRadius;
	private final ConfigData<Integer> fallingBlockMaxHeight;

	private final ConfigData<Double> velocity;

	private final ConfigData<Float> fallingBlockDamage;

	private final boolean preventLandingBlocks;
	private final ConfigData<Boolean> checkPlugins;
	private final ConfigData<Boolean> resolveDamagePerBlock;
	private final ConfigData<Boolean> resolveVelocityPerBlock;
	private final ConfigData<Boolean> resolveMaxHeightPerBlock;
	private final ConfigData<Boolean> resolveVelocityTypePerBlock;

	private final ConfigData<VelocityType> velocityType;

	public DestroySpell(MagicConfig config, String spellName) {
		super(config, spellName);

		vertRadius = getConfigDataInt("vert-radius", 3);
		horizRadius = getConfigDataInt("horiz-radius", 3);
		fallingBlockMaxHeight = getConfigDataInt("falling-block-max-height", 0);

		velocity = getConfigDataDouble("velocity", 0);

		fallingBlockDamage = getConfigDataFloat("falling-block-damage", 0);

		checkPlugins = getConfigDataBoolean("check-plugins", true);
		preventLandingBlocks = getConfigBoolean("prevent-landing-blocks", false);
		resolveDamagePerBlock = getConfigDataBoolean("resolve-damage-per-block", false);
		resolveVelocityPerBlock = getConfigDataBoolean("resolve-velocity-per-block", false);
		resolveMaxHeightPerBlock = getConfigDataBoolean("resolve-max-height-per-block", false);
		resolveVelocityTypePerBlock = getConfigDataBoolean("resolve-velocity-type-per-block", false);

		velocityType = getConfigDataEnum("velocity-type", VelocityType.class, VelocityType.NONE);

		fallingBlocks = new HashSet<>();

		List<String> toThrow = getConfigStringList("block-types-to-throw", null);
		if (toThrow != null && !toThrow.isEmpty()) {
			blockTypesToThrow = EnumSet.noneOf(Material.class);
			for (String s : toThrow) {
				Material m = Util.getMaterial(s);
				if (m == null) continue;
				blockTypesToThrow.add(m);
			}
		}

		List<String> toRemove = getConfigStringList("block-types-to-remove", null);
		if (toRemove != null && !toRemove.isEmpty()) {
			blockTypesToRemove = EnumSet.noneOf(Material.class);
			for (String s : toRemove) {
				Material m = Util.getMaterial(s);
				if (m == null) continue;
				blockTypesToRemove.add(m);
			}
		}

		if (preventLandingBlocks) {
			registerEvents(new FallingBlockListener());
			MagicSpells.scheduleRepeatingTask(() -> {
				if (fallingBlocks.isEmpty()) return;
				fallingBlocks.removeIf(fallingBlock -> !fallingBlock.isValid());
			}, 600, 600);
		}
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Location> info = getTargetedBlockLocation(data, 0.5, 0.5, 0.5, false);
		if (info.noTarget()) return noTarget(info);
		data = info.spellData();

		doIt(data.caster().getLocation(), info.target(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		doIt(data.caster().getLocation(), data.location(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtEntityFromLocation(SpellData data) {
		doIt(data.location(), data.target().getLocation(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private void doIt(Location source, Location targetLocation, SpellData data) {
		int centerX = targetLocation.getBlockX();
		int centerY = targetLocation.getBlockY();
		int centerZ = targetLocation.getBlockZ();

		List<Block> blocksToThrow = new ArrayList<>();
		List<Block> blocksToRemove = new ArrayList<>();

		int vertRadius = this.vertRadius.get(data);
		int horizRadius = this.horizRadius.get(data);

		boolean checkPlugins = this.checkPlugins.get(data);

		for (int y = centerY - vertRadius; y <= centerY + vertRadius; y++) {
			for (int x = centerX - horizRadius; x <= centerX + horizRadius; x++) {
				for (int z = centerZ - horizRadius; z <= centerZ + horizRadius; z++) {
					Block b = targetLocation.getWorld().getBlockAt(x, y, z);
					if (b.getType() == Material.BEDROCK) continue;
					if (BlockUtils.isAir(b.getType())) continue;

					if (checkPlugins && data.caster() instanceof Player player) {
						MagicSpellsBlockBreakEvent event = new MagicSpellsBlockBreakEvent(b, player);
						if (!event.callEvent()) continue;
					}

					if (blockTypesToThrow != null) {
						if (blockTypesToThrow.contains(b.getType())) blocksToThrow.add(b);
						else if (blockTypesToRemove != null && blockTypesToRemove.contains(b.getType())) blocksToRemove.add(b);
						else if (!b.getType().isSolid()) blocksToRemove.add(b);
						continue;
					}

					if (b.getType().isSolid()) blocksToThrow.add(b);
					else blocksToRemove.add(b);
				}
			}
		}

		for (Block b : blocksToRemove) b.setType(Material.AIR);

		boolean resolveDamagePerBlock = this.resolveDamagePerBlock.get(data);
		boolean resolveVelocityPerBlock = this.resolveVelocityPerBlock.get(data);
		boolean resolveMaxHeightPerBlock = this.resolveMaxHeightPerBlock.get(data);
		boolean resolveVelocityTypePerBlock = this.resolveVelocityTypePerBlock.get(data);

		double velocity = resolveVelocityPerBlock ? 0 : this.velocity.get(data);
		float fallingBlockDamage = resolveDamagePerBlock ? 0 : this.fallingBlockDamage.get(data);
		int fallingBlockHeight = resolveMaxHeightPerBlock ? 0 : this.fallingBlockMaxHeight.get(data);
		VelocityType velocityType = resolveVelocityTypePerBlock ? VelocityType.NONE : this.velocityType.get(data);

		for (Block b : blocksToThrow) {
			Material material = b.getType();
			Location l = b.getLocation().clone().add(0.5, 0.5, 0.5);
			FallingBlock fb = b.getWorld().spawnFallingBlock(l, material.createBlockData());
			fb.setDropItem(false);
			playSpellEffects(EffectPosition.PROJECTILE, fb, data);
			playTrackingLinePatterns(EffectPosition.DYNAMIC_CASTER_PROJECTILE_LINE, source, fb.getLocation(), null, fb, data);

			if (resolveVelocityPerBlock) velocity = this.velocity.get(data);
			if (resolveVelocityTypePerBlock) velocityType = this.velocityType.get(data);

			Vector v = switch (velocityType) {
				case UP -> {
					Vector vec = new Vector(0, velocity, 0);
					vec.setY(vec.getY() + ((Math.random() - 0.5) / 4));

					yield vec;
				}
				case RANDOM -> {
					Vector vec = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
					vec.normalize().multiply(velocity);

					yield vec;
				}
				case RANDOM_UP -> {
					Vector vec = new Vector(Math.random() - 0.5, Math.random() / 2, Math.random() - 0.5);
					vec.normalize().multiply(velocity);

					yield vec;
				}
				case DOWN -> new Vector(0, -velocity, 0);
				case TOWARD -> source.toVector().subtract(l.toVector()).normalize().multiply(velocity);
				case AWAY -> l.toVector().subtract(source.toVector()).normalize().multiply(velocity);
				case NONE -> new Vector(0, (Math.random() - 0.5) / 4, 0);
			};

			fb.setVelocity(v);

			if (resolveDamagePerBlock) fallingBlockDamage = this.fallingBlockDamage.get(data);
			if (fallingBlockDamage > 0) {
				if (resolveMaxHeightPerBlock) fallingBlockHeight = this.fallingBlockMaxHeight.get(data);
				MagicSpells.getVolatileCodeHandler().setFallingBlockHurtEntities(fb, fallingBlockDamage, fallingBlockHeight);
			}

			if (preventLandingBlocks) fallingBlocks.add(fb);
			b.setType(Material.AIR);
		}

		playSpellEffects(data);
	}

	private class FallingBlockListener implements Listener {

		@EventHandler
		public void onBlockLand(EntityChangeBlockEvent event) {
			boolean removed = fallingBlocks.remove(event.getEntity());
			if (removed) event.setCancelled(true);
		}

	}

	public enum VelocityType {

		NONE,
		UP,
		RANDOM,
		RANDOM_UP,
		DOWN,
		TOWARD,
		AWAY

	}

}
