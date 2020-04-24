package com.nisovin.magicspells.spells.instant;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class PhaseSpell extends InstantSpell {

	private final int maxDistance;
	private final String strCantPhase;

	private final List<Material> nonPhasableBlocks;
	private final List<Material> phasableBlocks;

	public PhaseSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		maxDistance = getConfigInt("max-distance", 15);
		strCantPhase = getConfigString("str-cant-phase", "Unable to find place to phase to.");
		phasableBlocks = new ArrayList<>();
		nonPhasableBlocks = new ArrayList<>();
		processMaterials(phasableBlocks,"phasable-blocks");
		processMaterials(nonPhasableBlocks,"non-phasable-blocks");
	}

	private void processMaterials(List<Material> materials, String path) {
		List<String> matList = getConfigStringList(path, null);
		if (matList == null || matList.isEmpty()) return;
		for (String mat : matList) {
			Material material = Material.getMaterial(mat.toUpperCase());
			if (material == null) {
				MagicSpells.error("PhaseSpell has an invalid material specified on '" + path + "': " + mat);
				continue;
			}
			materials.add(material);
		}
	}

	@Override
	public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			int r = Math.round(range * power);
			int distance = Math.round(maxDistance * power);

			BlockIterator iter;
			try {
				iter = new BlockIterator(livingEntity, distance << 1);
			} catch (IllegalStateException e) {
				sendMessage(strCantPhase, livingEntity, args);
				return PostCastAction.ALREADY_HANDLED;
			}

			int i = 0;
			Block start = null;
			Location location = null;

			while (i++ < r << 1 && iter.hasNext()) {
				Block b = iter.next();
				if (BlockUtils.isAir(b.getType())) continue;
				if (livingEntity.getLocation().distanceSquared(b.getLocation()) >= r * r) continue;
				start = b;
				break;
			}

			if (start != null) {
				if (canPassThrough(start)) {
					while (i++ < distance << 1 && iter.hasNext()) {
						Block block = iter.next();
						if (BlockUtils.isAir(block.getType()) && BlockUtils.isAir(block.getRelative(0, 1, 0).getType()) && livingEntity.getLocation().distanceSquared(block.getLocation()) < distance * distance) {
							location = block.getLocation();
							break;
						}
						if (!canPassThrough(block)) break;
					}
				}
			}

			if (location == null) {
				sendMessage(strCantPhase, livingEntity, args);
				return PostCastAction.ALREADY_HANDLED;
			}

			location.setX(location.getX() + 0.5);
			location.setZ(location.getZ() + 0.5);
			location.setPitch(livingEntity.getLocation().getPitch());
			location.setYaw(livingEntity.getLocation().getYaw());
			playSpellEffects(EffectPosition.CASTER, livingEntity.getLocation());
			playSpellEffects(EffectPosition.TARGET, location);
			livingEntity.teleport(location);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	private boolean canPassThrough(Block block) {
		// Check only blacklist.
		if (phasableBlocks.isEmpty()) return !nonPhasableBlocks.contains(block.getType());
		return phasableBlocks.contains(block.getType()) && !nonPhasableBlocks.contains(block.getType());
	}
}