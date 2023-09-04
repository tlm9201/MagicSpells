package com.nisovin.magicspells.spells.targeted;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

public class ParseSpell extends TargetedSpell implements TargetedEntitySpell {

	private final ConfigData<String> parseTo;
	private final ConfigData<String> operation;
	private final ConfigData<String> firstVariable;
	private final ConfigData<String> expectedValue;
	private final ConfigData<String> secondVariable;
	private final ConfigData<String> parseToVariable;
	private final ConfigData<String> variableToParse;

	public ParseSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		operation = getConfigDataString("operation", "normal"); //.toLowerCase();

		parseTo = getConfigDataString("parse-to", "");
		expectedValue = getConfigDataString("expected-value", "");
		parseToVariable = getConfigDataString("parse-to-variable", "");
		variableToParse = getConfigDataString("variable-to-parse", "");
		firstVariable = getConfigDataString("first-variable", "");
		secondVariable = getConfigDataString("second-variable", "");
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

		switch (operation.get(data)) {
			case "translate", "normal" -> {
				String expectedValue = this.expectedValue.get(data);

				String receivedValue = MagicSpells.getVariableManager().getStringValue(variableToParse.get(data), target);
				if (!receivedValue.equalsIgnoreCase(expectedValue) && !expectedValue.contains("any")) break;

				MagicSpells.getVariableManager().set(parseToVariable.get(data), target, parseTo.get(data));
			}
			case "difference" -> {
				double primary = MagicSpells.getVariableManager().getValue(firstVariable.get(data), target);
				double secondary = MagicSpells.getVariableManager().getValue(secondVariable.get(data), target);
				double diff = Math.abs(primary - secondary);

				MagicSpells.getVariableManager().set(parseToVariable.get(data), target, diff);
			}
			case "append" -> {
				String expectedValue = this.expectedValue.get(data);

				String receivedValue = MagicSpells.getVariableManager().getStringValue(variableToParse.get(data), target);
				if (!receivedValue.equalsIgnoreCase(expectedValue) && !expectedValue.contains("any")) break;

				String parseToVariable = this.parseToVariable.get(data);
				receivedValue = MagicSpells.getVariableManager().getStringValue(parseToVariable, target) + receivedValue + parseTo.get(data);

				MagicSpells.getVariableManager().set(parseToVariable, target, receivedValue);
			}
			case "regex", "regexp" -> {
				Pattern pattern = Pattern.compile(expectedValue.get(data));
				String parseTo = this.parseTo.get(data);

				String receivedValue = MagicSpells.getVariableManager().getStringValue(variableToParse.get(data), target);
				Matcher matcher = pattern.matcher(receivedValue);

				StringBuilder found = new StringBuilder();
				while (matcher.find()) {
					if (!parseTo.isEmpty()) {
						// If there is replacement text, replace and exit.
						matcher.reset();
						found.append(matcher.replaceAll(parseTo));
						break;
					}

					// Otherwise, collect found text.
					found.append(matcher.group());
				}

				MagicSpells.getVariableManager().set(parseToVariable.get(data), target, found.toString());
			}
			default -> {
				return new CastResult(PostCastAction.ALREADY_HANDLED, data);
			}
		}

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
