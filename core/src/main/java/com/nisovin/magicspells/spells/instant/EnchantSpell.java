package com.nisovin.magicspells.spells.instant;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EntityEquipment;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.handlers.EnchantmentHandler;

public class EnchantSpell extends InstantSpell {

	private final Map<Enchantment, Integer> enchantments;

	private ConfigData<Boolean> safeEnchants;

	public EnchantSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		enchantments = new HashMap<>();

		List<String> enchantmentList = getConfigStringList("enchantments", null);

		safeEnchants = getConfigDataBoolean("safe-enchants", true);

		if (enchantmentList != null && !enchantmentList.isEmpty()) {
			for (String string : enchantmentList) {
				Enchantment enchant = null;
				int level = 1;
				String[] str = string.split(" ");
				if (str[0] != null) enchant = EnchantmentHandler.getEnchantment(str[0]);
				if (str.length > 1 && str[1] != null) level = Integer.parseInt(str[1]);
				if (enchant != null) enchantments.put(enchant, level);
			}
		} else MagicSpells.error("EnchantSpell '" + internalName + "' has invalid enchantments defined!");
	}

	@Override
	public CastResult cast(SpellData data) {
		EntityEquipment eq = data.caster().getEquipment();
		if (eq == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		ItemStack item = eq.getItemInMainHand();
		if (item.getType().isAir()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		boolean safeEnchants = this.safeEnchants.get(data);
		for (Enchantment e : enchantments.keySet())
			enchant(item, safeEnchants, e, enchantments.get(e));

		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private void enchant(ItemStack item, boolean safeEnchants, Enchantment enchant, int level) {
		if (!enchant.canEnchantItem(item)) return;
		if (safeEnchants && level > enchant.getMaxLevel()) level = enchant.getMaxLevel();
		if (level <= 0) item.removeEnchantment(enchant);
		else {
			if (safeEnchants) item.addEnchantment(enchant, level);
			else item.addUnsafeEnchantment(enchant, level);
		}
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

}
