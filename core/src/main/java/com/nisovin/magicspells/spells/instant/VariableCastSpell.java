package com.nisovin.magicspells.spells.instant;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;

public class VariableCastSpell extends InstantSpell {

	private final ConfigData<String> variableName;
	private String strDoesntContainSpell;

	public VariableCastSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		variableName = getConfigDataString("variable-name", null);
		strDoesntContainSpell = getConfigString("str-doesnt-contain-spell", "You do not have a valid spell in memory");
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Player caster)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		String variableName = this.variableName.get(data);
		if (variableName == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		String value = MagicSpells.getVariableManager().getStringValue(variableName, caster);
		Spell toCast = MagicSpells.getSpellByInternalName(value);
		if (toCast == null) {
			sendMessage(caster, strDoesntContainSpell, data.args());
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		toCast.hardCast(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	public String getStrDoesntContainSpell() {
		return strDoesntContainSpell;
	}

	public void setStrDoesntContainSpell(String strDoesntContainSpell) {
		this.strDoesntContainSpell = strDoesntContainSpell;
	}

}
