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
		return check(livingEntity, livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		EntityEquipment eq = target.getEquipment();
		if (eq == null) return false;

		if (check(eq.getHelmet())) return true;
		if (check(eq.getChestplate())) return true;
		if (check(eq.getLeggings())) return true;
		return check(eq.getBoots());
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean check(ItemStack item) {
		MagicItemData data = MagicItems.getMagicItemDataFromItemStack(item);
		if (data == null) return false;

		return itemData.matches(data);
	}
	
}
