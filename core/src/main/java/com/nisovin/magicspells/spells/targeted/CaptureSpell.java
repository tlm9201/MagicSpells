package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class CaptureSpell extends TargetedSpell implements TargetedEntitySpell {

	private static final ValidTargetChecker CAPTURABLE = entity -> MobUtil.hasEggMaterialForEntityType(entity.getType());

	private final String itemName;
	private final List<String> itemLore;

	private final ConfigData<Boolean> gravity;
	private final ConfigData<Boolean> addToInventory;
	private final ConfigData<Boolean> powerAffectsQuantity;

	public CaptureSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		itemName = getConfigString("item-name", null);
		itemLore = getConfigStringList("item-lore", null);

		gravity = getConfigDataBoolean("gravity", true);
		addToInventory = getConfigDataBoolean("add-to-inventory", false);
		powerAffectsQuantity = getConfigDataBoolean("power-affects-quantity", true);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data, CAPTURABLE);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		LivingEntity target = data.target();

		ItemStack item = MobUtil.getEggItemForEntityType(target.getType());
		if (item == null) return noTarget(data);

		if (powerAffectsQuantity.get(data)) {
			int q = Math.round(data.power());
			if (q > 1) item.setAmount(q);
		}

		String entityName = MagicSpells.getEntityNames().get(target.getType());
		if (itemName != null || itemLore != null) {
			if (entityName == null) entityName = "unknown";
			ItemMeta meta = item.getItemMeta();
			if (itemName != null) meta.displayName(Util.getMiniMessage(MagicSpells.doReplacements(itemName, data, "%name%", entityName)));
			if (itemLore != null) {
				List<Component> lore = new ArrayList<>();
				for (String l : itemLore) lore.add(Util.getMiniMessage(MagicSpells.doReplacements(l, data, "%name%", entityName)));
				meta.lore(lore);
			}

			item.setItemMeta(meta);
		}

		target.remove();
		boolean added = false;

		if (addToInventory.get(data) && data.caster() instanceof Player player)
			added = Util.addToInventory(player.getInventory(), item, true, false);


		if (!added) {
			Item dropped = target.getWorld().dropItem(target.getLocation().add(0, 1, 0), item);
			dropped.setItemStack(item);
			dropped.setGravity(gravity.get(data));
		}

		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public ValidTargetChecker getValidTargetChecker() {
		return CAPTURABLE;
	}

}
