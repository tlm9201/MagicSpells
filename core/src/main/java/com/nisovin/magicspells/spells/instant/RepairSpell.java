package com.nisovin.magicspells.spells.instant;

import java.util.Set;
import java.util.List;
import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.PlayerInventory;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;

public class RepairSpell extends InstantSpell {

	private final ConfigData<Integer> repairAmt;

	private final ConfigData<Boolean> resolveRepairAmtPerItem;

	private String strNothingToRepair;

	private final EnumSet<RepairSelector> toRepair;

	private Set<Material> ignoredItems;
	private Set<Material> allowedItems;

	public RepairSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		repairAmt = getConfigDataInt("repair-amount", 300);

		resolveRepairAmtPerItem = getConfigDataBoolean("resolve-repair-amount-per-item", false);

		strNothingToRepair = getConfigString("str-nothing-to-repair", "Nothing to repair.");

		List<String> toRepairList = getConfigStringList("to-repair", null);
		if (toRepairList == null) toRepair = EnumSet.of(RepairSelector.HELD);
		else {
			toRepair = EnumSet.noneOf(RepairSelector.class);

			for (String selector : toRepairList) {
				try {
					toRepair.add(RepairSelector.valueOf(selector.toUpperCase()));
				} catch (IllegalArgumentException e) {
					MagicSpells.error("RepairSpell '" + internalName + "' has defined an invalid to-repair option: " + selector);
				}
			}
		}

		ignoredItems = EnumSet.noneOf(Material.class);
		List<String> list = getConfigStringList("ignore-items", null);
		if (list != null) {
			for (String s : list) {
				Material mat = Util.getMaterial(s);
				if (mat == null) continue;
				ignoredItems.add(mat);
			}
		}
		if (ignoredItems.isEmpty()) ignoredItems = null;

		allowedItems = EnumSet.noneOf(Material.class);
		list = getConfigStringList("allowed-items", null);
		if (list != null) {
			for (String s : list) {
				Material mat = Util.getMaterial(s);
				if (mat == null) continue;
				allowedItems.add(mat);
			}
		}
		if (allowedItems.isEmpty()) allowedItems = null;
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Player caster)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		boolean resolveRepairAmtPerItem = this.resolveRepairAmtPerItem.get(data);
		int repairAmt = resolveRepairAmtPerItem ? 0 : this.repairAmt.get(data);
		PlayerInventory inventory = caster.getInventory();

		boolean heldRepaired = false;
		boolean repaired = false;

		for (RepairSelector selector : toRepair) {
			switch (selector) {
				case HELD, MAINHAND -> {
					if (heldRepaired) break;

					ItemStack mainHand = repair(inventory.getItemInMainHand(), data, repairAmt, resolveRepairAmtPerItem);
					if (mainHand == null) continue;

					inventory.setItemInMainHand(mainHand);
					heldRepaired = true;
					repaired = true;
				}
				case OFFHAND -> {
					ItemStack offHand = repair(inventory.getItemInOffHand(), data, repairAmt, resolveRepairAmtPerItem);
					if (offHand == null) continue;

					inventory.setItemInOffHand(offHand);
					repaired = true;
				}
				case HOTBAR -> {
					ItemStack[] contents = inventory.getContents();
					int held = inventory.getHeldItemSlot();

					boolean modified = false;
					for (int i = 0; i < 9; i++) {
						if (heldRepaired && i == held) continue;

						ItemStack item = repair(contents[i], data, repairAmt, resolveRepairAmtPerItem);
						if (item == null) continue;

						modified = true;
						repaired = true;
						heldRepaired = true;
					}
					if (modified) inventory.setContents(contents);
				}
				case INVENTORY -> {
					ItemStack[] contents = inventory.getContents();

					boolean modified = false;
					for (int i = 9; i < 36; i++) {
						ItemStack item = repair(contents[i], data, repairAmt, resolveRepairAmtPerItem);
						if (item == null) continue;

						modified = true;
						repaired = true;
					}
					if (modified) inventory.setContents(contents);
				}
				case HELMET -> {
					ItemStack helmet = repair(inventory.getHelmet(), data, repairAmt, resolveRepairAmtPerItem);
					if (helmet == null) continue;

					inventory.setHelmet(helmet);
					repaired = true;
				}
				case CHESTPLATE -> {
					ItemStack chestplate = repair(inventory.getChestplate(), data, repairAmt, resolveRepairAmtPerItem);
					if (chestplate == null) continue;

					inventory.setChestplate(chestplate);
					repaired = true;
				}
				case LEGGINGS -> {
					ItemStack leggings = repair(inventory.getLeggings(), data, repairAmt, resolveRepairAmtPerItem);
					if (leggings == null) continue;

					inventory.setLeggings(leggings);
					repaired = true;
				}
				case BOOTS -> {
					ItemStack boots = repair(inventory.getBoots(), data, repairAmt, resolveRepairAmtPerItem);
					if (boots == null) continue;

					inventory.setBoots(boots);
					repaired = true;
				}
			}
		}

		if (!repaired) {
			sendMessage(strNothingToRepair, caster, data);
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private ItemStack repair(ItemStack item, SpellData data, int repairAmt, boolean resolveRepairAmtPerItem) {
		if (item == null) return null;

		Material type = item.getType();
		if (type.isAir()) return null;
		if (ignoredItems != null && ignoredItems.contains(type)) return null;
		if (allowedItems != null && !allowedItems.contains(type)) return null;

		boolean repaired = item.editMeta(Damageable.class, meta -> {
			int repairAmount = resolveRepairAmtPerItem ? this.repairAmt.get(data) : repairAmt;
			int damage = meta.getDamage() - repairAmount;

			damage = Math.min(damage, type.getMaxDurability());
			damage = Math.max(damage, 0);

			meta.setDamage(damage);
		});

		return repaired ? item : null;
	}

	public String getStrNothingToRepair() {
		return strNothingToRepair;
	}

	public void setStrNothingToRepair(String strNothingToRepair) {
		this.strNothingToRepair = strNothingToRepair;
	}

	public Set<Material> getIgnoredItems() {
		return ignoredItems;
	}

	public Set<Material> getAllowedItems() {
		return allowedItems;
	}

	private enum RepairSelector {
		HELD,
		MAINHAND,
		OFFHAND,
		HOTBAR,
		INVENTORY,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS
	}

}
