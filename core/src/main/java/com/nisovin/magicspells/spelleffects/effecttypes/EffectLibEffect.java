package com.nisovin.magicspells.spelleffects.effecttypes;

import java.awt.Font;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.lang.reflect.Field;

import com.google.common.base.CaseFormat;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.CustomSound;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class EffectLibEffect extends SpellEffect {

	protected EffectManager manager;
	protected String className;

	protected ConfigurationSection effectLibSection;
	protected Map<String, ConfigData<?>> options;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		effectLibSection = config.getConfigurationSection("effectlib");
		if (effectLibSection == null) return;

		className = effectLibSection.getString("class");
		manager = MagicSpells.getEffectManager();

		Effect effect = manager.getEffectByClassName(className);
		Class<? extends Effect> effectClass = effect.getClass();

		Set<String> keys = effectLibSection.getKeys(false);
		options = new HashMap<>();
		for (String actualKey : keys) {
			if (actualKey.equals("class") || !effectLibSection.isString(actualKey)) continue;

			String key = actualKey;
			if (key.contains("-")) key = key.replace("-", "_");
			if (key.contains("_")) key = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key);

			Field field;
			try {
				field = effectClass.getField(key);
			} catch (NoSuchFieldException e) {
				MagicSpells.error("Invalid option '" + actualKey + "' on EffectLib effect.");
				e.printStackTrace();

				continue;
			}

			Class<?> type = field.getType();
			if (type.equals(int.class) || type.equals(Integer.class) || type.equals(byte.class) || type.equals(Byte.class) || type.equals(short.class) || type.equals(Short.class))
				options.put(actualKey, ConfigDataUtil.getInteger(effectLibSection, actualKey, 0));
			else if (type.equals(long.class) || type.equals(Long.class))
				options.put(actualKey, ConfigDataUtil.getLong(effectLibSection, actualKey, 0));
			else if (type.equals(float.class) || type.equals(Float.class) || type.equals(double.class) || type.equals(Double.class))
				options.put(actualKey, ConfigDataUtil.getDouble(effectLibSection, actualKey, 0));
			else if (type.equals(boolean.class) || type.equals(Boolean.class))
				options.put(actualKey, ConfigDataUtil.getBoolean(effectLibSection, actualKey, false));
			else if (Enum.class.isAssignableFrom(type) || type.equals(String.class) || type.equals(Color.class) || type.equals(Font.class) || type.equals(CustomSound.class)) {
				ConfigData<?> data = ConfigDataUtil.getString(effectLibSection, actualKey, null);
				if (!data.isConstant()) options.put(actualKey, data);
			}

			if (options.containsKey(actualKey)) effectLibSection.set(actualKey, null);
		}

		if (options.isEmpty()) options = null;
	}

	@Override
	protected Runnable playEffectLocation(Location location, SpellData data) {
		if (!initialize()) return null;
		manager.start(className, getParameters(data), location);
		return null;
	}

	@Override
	protected Effect playEffectLibLocation(Location location, SpellData data) {
		if (!initialize()) return null;
		return manager.start(className, getParameters(data), location);
	}

	protected boolean initialize() {
		updateManager();
		if (manager.getEffects().size() >= MagicSpells.getEffectlibInstanceLimit()) {
			if (MagicSpells.shouldTerminateEffectlibEffects()) {
				MagicSpells.resetEffectlib();
				updateManager();
			} else return false;
		}
		return true;
	}

	protected void updateManager() {
		if (manager == null || manager.isDisposed()) manager = MagicSpells.getEffectManager();
	}

	protected ConfigurationSection getParameters(SpellData data) {
		if (options == null || effectLibSection == null) return effectLibSection;

		ConfigurationSection parameters = new MemoryConfiguration();
		for (String key : effectLibSection.getKeys(true))
			parameters.set(key, effectLibSection.get(key));

		for (Map.Entry<String, ConfigData<?>> option : options.entrySet())
			parameters.set(option.getKey(), option.getValue().get(data));

		return parameters;
	}

}
