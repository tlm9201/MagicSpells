package com.nisovin.magicspells.volatilecode;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.OfflinePlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public interface VolatileCodeHandle {

	void addPotionGraphicalEffect(LivingEntity entity, int color, int duration);

	void creaturePathToLoc(Creature creature, Location loc, float speed);

	void sendFakeSlotUpdate(Player player, int slot, ItemStack item);

	boolean simulateTnt(Location target, LivingEntity source, float explosionSize, boolean fire);

	void setExperienceBar(Player player, int level, float percent);

	void setFallingBlockHurtEntities(FallingBlock block, float damage, int max);

	void setKiller(LivingEntity entity, Player killer);

	void playDragonDeathEffect(Location location);

	void addAILookAtPlayer(LivingEntity entity, int range);

	void setClientVelocity(Player player, Vector velocity);

	String getSkinData(Player player);

	void setTexture(SkullMeta meta, String texture, String signature);

	void setTexture(SkullMeta meta, String texture, String signature, String uuid, OfflinePlayer offlinePlayer);

	void setSkin(Player player, String skin, String signature);

	ItemStack setNBTString(ItemStack item, String key, String value);

	String getNBTString(ItemStack item, String key);

	void setInventoryTitle(Player player, String title);

	Recipe createSmithingRecipe(NamespacedKey namespaceKey, ItemStack result, Material base, Material addition);

	String colorize(String message);

	void sendActionBarMessage(Player player, String message);
}
