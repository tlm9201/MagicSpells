package com.nisovin.magicspells.castmodifiers.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class OffHandPreciseCondition extends Condition {

	private MagicItemData itemData = null;
	
	@Override
	public boolean initialize(String var) {
		itemData = MagicItems.getMagicItemDataFromString(var);
		return itemData != null;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		EntityEquipment eq = livingEntity.getEquipment();
		return eq != null && check(eq.getItemInOffHand());
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		EntityEquipment eq = target.getEquipment();
		return eq != null && check(eq.getItemInOffHand());
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
