package com.nisovin.magicspells.spells.targeted.ext;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import me.clip.placeholderapi.PlaceholderAPI;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

// NOTE: PLACEHOLDERAPI IS REQUIRED FOR THIS
public class PlaceholderAPIDataSpell extends TargetedSpell implements TargetedEntitySpell {

	private final String variableName;
	private final String placeholderAPITemplate;
	private final boolean useTargetVariables;
	private final boolean setTargetVariable;
	private final boolean setTargetPlaceholders;

	public PlaceholderAPIDataSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		variableName = getConfigString("variable-name", null);
		placeholderAPITemplate = getConfigString("placeholderapi-template", "An admin forgot to set placeholderapi-template");
		useTargetVariables = getConfigBoolean("use-target-variables", true);
		setTargetVariable = getConfigBoolean("set-target-variable", false);
		setTargetPlaceholders = getConfigBoolean("set-target-placeholders", true);
	}

	@Override
	public void initializeVariables() {
		super.initializeVariables();

		if (variableName == null) {
			MagicSpells.error("variable-name is null for '" + internalName + "'");
			MagicSpells.error("In most cases, this should be set to the name of a string variable, but non string variables may work depending on values.");
			return;
		}

		if (MagicSpells.getVariableManager().getVariable(variableName) == null) {
			MagicSpells.error("Invalid variable-name on '" + internalName + "'");
			return;
		}

		// You have to REALLY screw up for this to happen.
		if (placeholderAPITemplate == null) {
			MagicSpells.error("placeholderapi-template is null (you made it worse than the default) in '" + internalName + "'");
			MagicSpells.error("This was probably because you put something similar to \"placeholderapi-template\" and did not specify a value.");
		}
	}

	@Override
	public PostCastAction castSpell(LivingEntity caster, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL && caster instanceof Player player) {
			TargetInfo<Player> targetInfo = getTargetedPlayer(player, power, args);
			if (targetInfo == null) return noTarget(player);
			Player target = targetInfo.getTarget();
			if (target == null) return noTarget(player);
			String value = MagicSpells.doArgumentSubstitution(placeholderAPITemplate, args);
			setPlaceholders(player, target, value);
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		if (!(caster instanceof Player casterPlayer)) return false;
		if (!(target instanceof Player targetPlayer)) return false;
		setPlaceholders(casterPlayer, targetPlayer, placeholderAPITemplate);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		return false;
	}

	private void setPlaceholders(Player caster, Player target, String value) {
		value = MagicSpells.doVariableReplacements(useTargetVariables ? target : caster, value);
		value = PlaceholderAPI.setBracketPlaceholders(setTargetPlaceholders ? target : caster, value);
		value = PlaceholderAPI.setPlaceholders(setTargetPlaceholders ? target : caster, value);
		MagicSpells.getVariableManager().set(variableName, setTargetVariable ? target : caster, value);
		playSpellEffects(caster, target);
	}

}
