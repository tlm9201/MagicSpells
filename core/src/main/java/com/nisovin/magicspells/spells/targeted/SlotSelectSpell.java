package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.variables.Variable;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.FunctionData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class SlotSelectSpell extends TargetedSpell implements TargetedEntitySpell {

	private final String variable;

	private final ConfigData<Integer> slot;

	private final ConfigData<Boolean> ignoreSlotBounds;

	public SlotSelectSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		ignoreSlotBounds = getConfigDataBoolean("ignore-slot-bounds", false);

		String path = internalKey + "slot";
		if (config.isString(path)) {
			String value = config.getString(path, null);
			if (value == null) {
				slot = data -> 0;
				variable = null;
				return;
			}

			if (value.matches("\\w+")) {
				slot = null;
				variable = value;
				return;
			}

			FunctionData<Integer> function = FunctionData.build(value, Double::intValue, 0);
			slot = function == null ? data -> 0 : function;
		} else {
			int value = config.getInt(path, 0);
			slot = data -> value;
		}

		variable = null;
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Player> info = getTargetedPlayer(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.target() instanceof Player target)) return noTarget(data);

		int slot;
		if (variable != null) {
			Variable var = MagicSpells.getVariableManager().getVariable(variable);
			if (var == null) {
				MagicSpells.error("SlotSelectSpell '" + internalName + "' has an invalid variable specified in 'slot'!");
				return new CastResult(PostCastAction.ALREADY_HANDLED, data);
			}

			slot = (int) Math.round(var.getValue(target));
		} else slot = this.slot.get(data);

		try {
			target.getInventory().setHeldItemSlot(slot);
		} catch (IllegalArgumentException e) {
			if (!ignoreSlotBounds.get(data)) {
				MagicSpells.error("SlotSelectSpell '" + internalName + "' attempted to set to a slot outside bounds (0-8)! If this is intended, set 'ignore-slot-bounds' to true.");
				return new CastResult(PostCastAction.ALREADY_HANDLED, data);
			}
		}

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
