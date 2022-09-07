package com.nisovin.magicspells.util.config;

import com.nisovin.magicspells.MagicSpells;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class StringData implements ConfigData<String> {

	private final boolean targetedReplacement;
	private final boolean varReplacement;
	private final boolean argReplacement;

	private final String value;

	public StringData(String value, boolean varReplacement, boolean targetedReplacement, boolean argReplacement) {
		this.targetedReplacement = targetedReplacement;
		this.varReplacement = varReplacement;
		this.argReplacement = argReplacement;

		this.value = value;
	}

	@Override
	public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
		String ret = value;

		if (argReplacement) ret = MagicSpells.doArgumentSubstitution(ret, args);

		Player playerCaster = caster instanceof Player p ? p : null;
		Player playerTarget = target instanceof Player p ? p : null;

		if (varReplacement) ret = MagicSpells.doVariableReplacements(playerCaster, ret);
		if (targetedReplacement) ret = MagicSpells.doTargetedVariableReplacements(playerCaster, playerTarget, ret);

		return ret;
	}

	@Override
	public boolean isConstant() {
		return !argReplacement && !varReplacement && !targetedReplacement;
	}

}
