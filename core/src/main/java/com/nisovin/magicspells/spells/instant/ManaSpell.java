package com.nisovin.magicspells.spells.instant;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.util.config.ConfigData;

public class ManaSpell extends InstantSpell {

	private final ConfigData<Integer> mana;

	private final ConfigData<Boolean> powerAffectsMana;

	public ManaSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		mana = getConfigDataInt("mana", 25);
		powerAffectsMana = getConfigDataBoolean("power-affects-mana", true);
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Player caster)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		int mana = this.mana.get(data);
		if (powerAffectsMana.get(data)) mana = Math.round(mana * data.power());

		boolean added = MagicSpells.getManaHandler().addMana(caster, mana, ManaChangeReason.OTHER);
		if (!added) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
