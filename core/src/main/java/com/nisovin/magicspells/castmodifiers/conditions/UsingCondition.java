package com.nisovin.magicspells.castmodifiers.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Name("using")
public class UsingCondition extends Condition {

	private MagicItemData itemData = null;
	
	@Override
	public boolean initialize(@NotNull String var) {
		itemData = MagicItems.getMagicItemDataFromString(var);
		return itemData != null;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return checkUsing(caster);
	}
	
	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkUsing(target);
	}
	
	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean checkUsing(LivingEntity target) {
		if (!target.hasActiveItem()) return false;

		ItemStack item = target.getActiveItem();
        MagicItemData data = MagicItems.getMagicItemDataFromItemStack(item);

		return itemData != null && itemData.matches(data);
	}

}
