package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.Damageable;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.InventoryUtil;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

public class HasItemCondition extends Condition {

	private Material material;
	private short durability;
	private String name;
	private boolean checkName;

	@Override
	public boolean initialize(String var) {
		try {
			if (var.contains("|")) {
				String[] subvardata = var.split("\\|");
				var = subvardata[0];
				name = Util.colorize(subvardata[1]).replace("__", " ");
				if (name.isEmpty()) name = null;
				checkName = true;
			} else {
				name = null;
				checkName = false;
			}
			if (var.contains(":")) {
				String[] vardata = var.split(":");
				material = Util.getMaterial(vardata[0]);
				durability = vardata[1].equals("*") ? 0 : Short.parseShort(vardata[1]);
			} else {
				material = Util.getMaterial(var);
			}
			return true;
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
			return false;
		}
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return check(livingEntity, livingEntity);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		if (target == null) return false;
		if (target instanceof InventoryHolder) return check(((InventoryHolder) target).getInventory());
		else return check(target.getEquipment());
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		Block target = location.getBlock();
		BlockState targetState = target.getState();
		return targetState instanceof InventoryHolder holder && check((holder).getInventory());
	}

	private boolean check(Inventory inventory) {
		if (inventory == null) return false;
		if (checkName) {
			for (ItemStack item : inventory.getContents()) {
				if (item == null) continue;
				String thisname = null;
				try {
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) thisname = Util.getLegacyFromComponent(item.getItemMeta().displayName());
				} catch (Exception e) {
					DebugHandler.debugGeneral(e);
				}
				if (item.getType() == material && (item instanceof Damageable damageable && damageable.getDamage() == durability) && (!checkName || Objects.equals(thisname, name))) return true;
			}

			return false;
		}

		return inventory.contains(material);
	}

	private boolean check(EntityEquipment entityEquipment) {
		if (entityEquipment == null) return false;
		ItemStack[] items = InventoryUtil.getEquipmentItems(entityEquipment);

		if (checkName) {
			for (ItemStack item : items) {
				if (item == null) continue;
				String thisname = null;
				try {
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) thisname = Util.getLegacyFromComponent(item.getItemMeta().displayName());
				} catch (Exception e) {
					DebugHandler.debugGeneral(e);
				}
				if (item.getType() == material && (item instanceof Damageable damageable && damageable.getDamage() == durability) && (!checkName || Objects.equals(name, thisname))) return true;
			}

			return false;
		}

		for (ItemStack i : items) {
			if (i == null) continue;
			return i.getType() == material;
		}

		return false;
	}

}
