package com.nisovin.magicspells.volatilecode;

import org.bukkit.entity.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.inventory.ItemStack;

public class VolatileCodeDisabled implements VolatileCodeHandle {

	public VolatileCodeDisabled() {
	}

	@Override
	public void addPotionGraphicalEffect(LivingEntity entity, int color, int duration) {
		// Need the volatile code for this
	}

	@Override
	public void sendFakeSlotUpdate(Player player, int slot, ItemStack item) {
		// Need the volatile code for this
	}

	@Override
	public boolean simulateTnt(Location target, LivingEntity source, float explosionSize, boolean fire) {
		return false;
	}

	@Override
	public void setFallingBlockHurtEntities(FallingBlock block, float damage, int max) {
		block.setHurtEntities(true);
		// Need the (rest of) volatile code for this
	}

	@Override
	public void playDragonDeathEffect(Location location) {
		// Need the volatile code for this
	}

	@Override
	public void setClientVelocity(Player player, Vector velocity) {
		// Need the volatile code for this
	}

	@Override
	public void setInventoryTitle(Player player, String title) {
		// Need volatile code for this
	}
}
