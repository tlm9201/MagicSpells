package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EntityEquipment;

import com.nisovin.magicspells.castmodifiers.Condition;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class WearingPreciseCondition extends Condition {

	private MagicItemData itemData = null;
	
	@Override
	public boolean initialize(String var) {
		itemData = MagicItems.getMagicItemDataFromString(var);
		return itemData != null;
	}
	
	@Override
	public boolean check(LivingEntity livingEntity) {
		return checkInventory(livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return checkInventory(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean checkInventory(LivingEntity livingEntity) {
		EntityEquipment eq = livingEntity.getEquipment();
		if (eq == null) return false;

		if (checkItem(eq.getHelmet())) return true;
		if (checkItem(eq.getChestplate())) return true;
		if (checkItem(eq.getLeggings())) return true;
		return checkItem(eq.getBoots());
	}

	private boolean checkItem(ItemStack item) {
		MagicItemData data = MagicItems.getMagicItemDataFromItemStack(item);
		if (data == null) return false;

		return itemData.matches(data);
	}
	
}
