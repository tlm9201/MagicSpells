package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.castmodifiers.Condition;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class HoveringWithCondition extends Condition {

	private MagicItemData itemData;

	@Override
	public boolean initialize(String var) {
		if (var == null || var.isEmpty()) return false;

		itemData = MagicItems.getMagicItemDataFromString(var);
		return itemData != null;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		if (!(livingEntity instanceof Player)) return false;
		Player player = (Player) livingEntity;
		ItemStack itemStack = player.getOpenInventory().getCursor();
		if (itemStack == null) return false;

		MagicItemData magicItemData = MagicItems.getMagicItemDataFromItemStack(itemStack);
		if (magicItemData == null) return false;

		return itemData.matches(magicItemData);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return check(target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

}
