package com.nisovin.magicspells.spells;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.util.magicitems.MagicItemData;

public class OffhandCooldownSpell extends InstantSpell {

	private List<Player> players = new ArrayList<>();

	private MagicItemData itemData;

	private ItemStack item;

	private Spell spellToCheck;
	private String spellToCheckName;

	public OffhandCooldownSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		if (isConfigString("item")) {
			MagicItem magicItem = MagicItems.getMagicItemFromString(getConfigString("item", "stone"));
			if (magicItem != null) {
				item = magicItem.getItemStack();
				itemData = magicItem.getMagicItemData();
			}
		} else if (isConfigSection("item")) {
			MagicItem magicItem = MagicItems.getMagicItemFromSection(getConfigSection("item"));
			if (magicItem != null) {
				item = magicItem.getItemStack();
				itemData = magicItem.getMagicItemData();
			}
		}

		spellToCheckName = getConfigString("spell", "");
	}
	
	@Override
	public void initialize() {
		super.initialize();

		spellToCheck = MagicSpells.getSpellByInternalName(spellToCheckName);

		if (spellToCheck == null || item == null || itemData == null) return;
		
		MagicSpells.scheduleRepeatingTask(() -> {
			Iterator<Player> iter = players.iterator();
			while (iter.hasNext()) {
				Player pl = iter.next();
				if (!pl.isValid()) {
					iter.remove();
					continue;
				}
				float cd = spellToCheck.getCooldown(pl);
				int amt = 1;
				if (cd > 0) amt = (int) Math.ceil(cd);

				PlayerInventory inventory = pl.getInventory();
				ItemStack off = inventory.getItemInOffHand();

				MagicItemData offItemData = MagicItems.getMagicItemDataFromItemStack(off);
				if (offItemData == null) continue;

				off.setAmount(amt);
				if (!offItemData.equals(itemData)) inventory.setItemInOffHand(item.clone());
			}
		}, TimeUtil.TICKS_PER_SECOND, TimeUtil.TICKS_PER_SECOND);
	}

	@Override
	public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL && livingEntity instanceof Player) {
			players.add((Player) livingEntity);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

}
