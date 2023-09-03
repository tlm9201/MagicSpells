package com.nisovin.magicspells.spells.targeted.ext;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

// NOTE: PLACEHOLDERAPI IS REQUIRED FOR THIS
public class PlaceholderAPIDataSpell extends TargetedSpell implements TargetedEntitySpell {

	private final String placeholderAPITemplate;
	private final ConfigData<String> variableName;

	private final ConfigData<Boolean> setTargetVariable;
	private final ConfigData<Boolean> useTargetVariables;
	private final ConfigData<Boolean> setTargetPlaceholders;

	public PlaceholderAPIDataSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		variableName = getConfigDataString("variable-name", null);
		placeholderAPITemplate = getConfigString("placeholderapi-template", "An admin forgot to set placeholderapi-template");

		useTargetVariables = getConfigDataBoolean("use-target-variables", true);
		setTargetVariable = getConfigDataBoolean("set-target-variable", false);
		setTargetPlaceholders = getConfigDataBoolean("set-target-placeholders", true);
	}

	@Override
	public void initializeVariables() {
		super.initializeVariables();

		if (variableName == null) {
			MagicSpells.error("variable-name is null for '" + internalName + "'");
			MagicSpells.error("In most cases, this should be set to the name of a string variable, but non string variables may work depending on values.");
			return;
		}

		// You have to REALLY screw up for this to happen.
		if (placeholderAPITemplate == null) {
			MagicSpells.error("placeholderapi-template is null (you made it worse than the default) in '" + internalName + "'");
			MagicSpells.error("This was probably because you put something similar to \"placeholderapi-template\" and did not specify a value.");
		}
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Player caster)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		TargetInfo<Player> info = getTargetedPlayer(data);
		if (info.noTarget()) return noTarget(info);
		data = info.spellData();

		setPlaceholders(caster, info.target(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.caster() instanceof Player caster) || !(data.target() instanceof Player target))
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		setPlaceholders(caster, target, data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private void setPlaceholders(Player caster, Player target, SpellData data) {
		String value = MagicSpells.doArgumentSubstitution(placeholderAPITemplate, data.args());

		String variableName = this.variableName.get(data);
		boolean setTargetVariable = this.setTargetVariable.get(data);
		boolean useTargetVariables = this.useTargetVariables.get(data);
		boolean setTargetPlaceholders = this.setTargetPlaceholders.get(data);

		value = MagicSpells.doVariableReplacements(value, useTargetVariables ? target : caster, caster, target);
		value = PlaceholderAPI.setBracketPlaceholders(setTargetPlaceholders ? target : caster, value);
		value = PlaceholderAPI.setPlaceholders(setTargetPlaceholders ? target : caster, value);

		MagicSpells.getVariableManager().set(variableName, setTargetVariable ? target : caster, value);
		playSpellEffects(caster, target, data);
	}

}
