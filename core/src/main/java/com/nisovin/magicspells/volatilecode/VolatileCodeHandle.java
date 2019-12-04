package com.nisovin.magicspells.volatilecode;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

public interface VolatileCodeHandle {
	
	void addPotionGraphicalEffect(LivingEntity entity, int color, int duration);

	void creaturePathToLoc(Creature creature, Location loc, float speed);
	
	void sendFakeSlotUpdate(Player player, int slot, ItemStack item);

	boolean simulateTnt(Location target, LivingEntity source, float explosionSize, boolean fire);
	
	boolean createExplosionByEntity(Entity entity, Location location, float size, boolean fire, boolean breakBlocks);

	void setExperienceBar(Player player, int level, float percent);

	void setTarget(LivingEntity entity, LivingEntity target);

	void setFallingBlockHurtEntities(FallingBlock block, float damage, int max);

	void setKiller(LivingEntity entity, Player killer);

	void playDragonDeathEffect(Location location);

	void addAILookAtPlayer(LivingEntity entity, int range);
	
	void saveSkinData(Player player, String name);

	void setClientVelocity(Player player, Vector velocity);
	
	double getAbsorptionHearts(LivingEntity entity);

	void setTexture(SkullMeta meta, String texture, String signature);
	
	void setTexture(SkullMeta meta, String texture, String signature, String uuid, String name);
	
	void setSkin(Player player, String skin, String signature);
		
}
