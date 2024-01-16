package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.InventoryUtil;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.conditions.util.OperatorCondition;

public class OpenSlotsCondition extends OperatorCondition {

	private int slots;
	
	@Override
	public boolean initialize(@NotNull String var) {
		if (var.length() < 2 || !super.initialize(var)) return false;

		try {
			slots = Integer.parseInt(var.substring(1));
			return true;
		} catch (NumberFormatException e) {
			DebugHandler.debugNumberFormat(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity caster) {
		return openSlots(caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return openSlots(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean openSlots(LivingEntity target) {
		int c = 0;
		ItemStack[] inv = null;

		if (target instanceof Player pl) inv = pl.getInventory().getContents();
		else if (target.getEquipment() != null) inv = InventoryUtil.getEquipmentItems(target.getEquipment());

		if (inv == null) return false;

		for (ItemStack itemStack : inv) {
			if (itemStack == null) c++;
		}

		return compare(c, slots);
	}

}
