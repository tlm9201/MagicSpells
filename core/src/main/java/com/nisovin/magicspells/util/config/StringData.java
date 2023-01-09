package com.nisovin.magicspells.util.config;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import me.clip.placeholderapi.PlaceholderAPI;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TxtUtil;
import com.nisovin.magicspells.variables.Variable;
import com.nisovin.magicspells.variables.variabletypes.GlobalStringVariable;
import com.nisovin.magicspells.variables.variabletypes.PlayerStringVariable;

public class StringData implements ConfigData<String> {

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("""
		%(?:\
		((var|castervar|targetvar):(\\w+)(?::(\\d+))?)|\
		(playervar:([a-zA-Z0-9_]{3,16}):(\\w+)(?::(\\d+))?)|\
		(arg:(\\d+):(\\w+))|\
		((papi|casterpapi|targetpapi):([^%]+))|\
		(playerpapi:([a-zA-Z0-9_]{3,16}):([^%]+))\
		)%""", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private final List<ConfigData<String>> values;
	private final List<String> fragments;

	public StringData(String value) {
		List<ConfigData<String>> values = new ArrayList<>();
		List<String> fragments = new ArrayList<>();

		Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
		int end = 0;
		while (matcher.find()) {
			ConfigData<String> data = createData(matcher);
			if (data == null) continue;

			fragments.add(value.substring(end, matcher.start()));
			values.add(data);

			end = matcher.end();
		}

		fragments.add(value.substring(end));

		this.fragments = Collections.unmodifiableList(fragments);
		this.values = Collections.unmodifiableList(values);
	}

	private static ConfigData<String> createData(Matcher matcher) {
		String placeholder = matcher.group(1);
		if (placeholder != null) {
			String owner = matcher.group(2);
			String variable = matcher.group(3);
			String placesString = matcher.group(4);

			int places = -1;
			if (placesString != null) {
				try {
					places = Integer.parseInt(placesString);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			return owner.equalsIgnoreCase("targetvar") ?
				new TargetVariableData(placeholder, variable, places) :
				new CasterVariableData(placeholder, variable, places);
		}

		placeholder = matcher.group(5);
		if (placeholder != null) {
			String player = matcher.group(6);
			String variable = matcher.group(7);
			String placesString = matcher.group(8);

			int places = -1;
			if (placesString != null) {
				try {
					places = Integer.parseInt(placesString);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			return new PlayerVariableData(placeholder, variable, player, places);
		}

		placeholder = matcher.group(9);
		if (placeholder != null) {
			String def = matcher.group(11);

			int index;
			try {
				index = Integer.parseInt(matcher.group(10));
			} catch (NumberFormatException e) {
				return null;
			}
			if (index == 0) return null;

			return new ArgumentData(index - 1, def);
		}

		placeholder = matcher.group(12);
		if (placeholder != null) {
			String owner = matcher.group(13);
			String papiPlaceholder = '%' + matcher.group(14) + '%';

			return owner.equalsIgnoreCase("targetpapi") ?
				new TargetPAPIData(placeholder, papiPlaceholder) :
				new CasterPAPIData(placeholder, papiPlaceholder);
		}

		placeholder = matcher.group(15);
		if (placeholder != null) {
			String player = matcher.group(16);
			String papiPlaceholder = '%' + matcher.group(17) + '%';

			return new PlayerPAPIData(placeholder, papiPlaceholder, player);
		}

		return null;
	}

	@Override
	public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
		if (values.isEmpty()) return fragments.get(0);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < fragments.size() - 1; i++) {
			builder.append(fragments.get(i));
			builder.append(values.get(i).get(caster, target, power, args));
		}
		builder.append(fragments.get(fragments.size() - 1));

		return builder.toString();
	}

	public List<ConfigData<String>> getValues() {
		return values;
	}

	public List<String> getFragments() {
		return fragments;
	}

	public static abstract class PlaceholderData implements ConfigData<String> {

		protected final String placeholder;

		public PlaceholderData(String placeholder) {
			this.placeholder = '%' + placeholder + '%';
		}

		@Override
		public boolean isConstant() {
			return false;
		}

	}

	public static class ArgumentData implements ConfigData<String> {

		private final String def;
		private final int index;

		public ArgumentData(int index, String def) {
			this.index = index;
			this.def = def;
		}

		@Override
		public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
			if (args != null && args.length > index) return args[index];
			else return def;
		}

		@Override
		public boolean isConstant() {
			return false;
		}

	}

	public static class CasterVariableData extends PlaceholderData {

		private final String variable;
		private final int places;

		public CasterVariableData(String placeholder, String variable, int places) {
			super(placeholder);

			this.variable = variable;
			this.places = places;
		}

		@Override
		public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
			if (!(caster instanceof Player player)) return placeholder;

			Variable var = MagicSpells.getVariableManager().getVariable(variable);
			if (var == null) return placeholder;

			if (places >= 0) {
				if (var instanceof PlayerStringVariable || var instanceof GlobalStringVariable)
					return TxtUtil.getStringNumber(var.getStringValue(player), places);

				return TxtUtil.getStringNumber(var.getValue(player), places);
			}

			return var.getStringValue(player);
		}

	}

	public static class TargetVariableData extends PlaceholderData {

		private final String variable;
		private final int places;

		public TargetVariableData(String placeholder, String variable, int places) {
			super(placeholder);

			this.variable = variable;
			this.places = places;
		}

		@Override
		public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
			if (!(target instanceof Player player)) return placeholder;

			Variable var = MagicSpells.getVariableManager().getVariable(variable);
			if (var == null) return placeholder;

			if (places >= 0) {
				if (var instanceof PlayerStringVariable || var instanceof GlobalStringVariable)
					return TxtUtil.getStringNumber(var.getStringValue(player), places);

				return TxtUtil.getStringNumber(var.getValue(player), places);
			}

			return var.getStringValue(player);
		}

	}

	public static class PlayerVariableData extends PlaceholderData {

		private final String variable;
		private final String player;
		private final int places;

		public PlayerVariableData(String placeholder, String variable, String player, int places) {
			super(placeholder);

			this.variable = variable;
			this.player = player;
			this.places = places;
		}

		@Override
		public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
			Variable var = MagicSpells.getVariableManager().getVariable(variable);
			if (var == null) return placeholder;

			if (places >= 0) {
				if (var instanceof PlayerStringVariable || var instanceof GlobalStringVariable)
					return TxtUtil.getStringNumber(var.getStringValue(player), places);

				return TxtUtil.getStringNumber(var.getValue(player), places);
			}

			return var.getStringValue(player);
		}

	}

	public static class CasterPAPIData extends PlaceholderData {

		private final String papiPlaceholder;

		public CasterPAPIData(String placeholder, String papiPlaceholder) {
			super(placeholder);

			this.papiPlaceholder = papiPlaceholder;
		}

		@Override
		public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
			if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") || !(caster instanceof Player player))
				return placeholder;

			return PlaceholderAPI.setPlaceholders(player, papiPlaceholder);
		}

	}

	public static class TargetPAPIData extends PlaceholderData {

		private final String papiPlaceholder;

		public TargetPAPIData(String placeholder, String papiPlaceholder) {
			super(placeholder);

			this.papiPlaceholder = papiPlaceholder;
		}

		@Override
		public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
			if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") || !(target instanceof Player player))
				return placeholder;

			return PlaceholderAPI.setPlaceholders(player, papiPlaceholder);
		}

	}

	public static class PlayerPAPIData extends PlaceholderData {

		private final String papiPlaceholder;
		private final String player;

		public PlayerPAPIData(String placeholder, String papiPlaceholder, String player) {
			super(placeholder);

			this.papiPlaceholder = papiPlaceholder;
			this.player = player;
		}

		@Override
		public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
			if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return placeholder;
			return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(player), papiPlaceholder);
		}

	}

	@Override
	public boolean isConstant() {
		return values.isEmpty();
	}

}
