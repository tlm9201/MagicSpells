package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.EnumSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.castmodifiers.Condition;

public class OnFireCondition extends Condition {

	private final EnumSet<Material> fireTypes = EnumSet.of(Material.FIRE, Material.SOUL_FIRE);

	@Override
	public boolean initialize(String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return onFire(livingEntity, null);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return onFire(livingEntity, null);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return onFire(livingEntity, location);
	}

	private boolean onFire(LivingEntity livingEntity, Location location) {
		if (location != null) {
			Block b = location.getBlock();
			return fireTypes.contains(b.getType()) || fireTypes.contains(b.getRelative(BlockFace.UP).getType());
		}
		return livingEntity.getFireTicks() > 0;
	}

}
