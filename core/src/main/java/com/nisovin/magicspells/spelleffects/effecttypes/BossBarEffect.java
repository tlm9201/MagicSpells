package com.nisovin.magicspells.spelleffects.effecttypes;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.variables.Variable;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.managers.BossBarManager.Bar;

public class BossBarEffect extends SpellEffect {

	private final static Map<String, Integer> tasks = new HashMap<>();

	private String namespaceKey;
	private String title;
	private String color;
	private String style;

	private String strVar;
	private Variable variable;
	private String maxVar;
	private Variable maxVariable;
	private double maxValue;

	private BarColor barColor;
	private BarStyle barStyle;

	private int duration;
	private double progress;

	private boolean remove;
	private boolean visible;
	private boolean broadcast;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		namespaceKey = config.getString("namespace-key");
		if (namespaceKey != null && !MagicSpells.getBossBarManager().isNamespaceKey(namespaceKey)) {
			MagicSpells.error("BossBarEffect '"
					+ config.getCurrentPath()
					+ "' has a wrong namespace-key '"
					+ namespaceKey + "' defined!");
		}

		broadcast = config.getBoolean("broadcast", false);

		remove = config.getBoolean("remove", false);
		if (remove) return;

		title = config.getString("title", "");
		color = config.getString("color", "red");
		style = config.getString("style", "solid");
		strVar = config.getString("variable", "");
		maxValue = config.getDouble("max-value", 100);
		maxVar = config.getString("max-variable", "");
		visible = config.getBoolean("visible", true);

		try {
			barColor = BarColor.valueOf(color.toUpperCase());
		} catch (IllegalArgumentException ignored) {
			barColor = BarColor.WHITE;
			MagicSpells.error("BossBarEffect '"
					+ config.getCurrentPath()
					+ "' has a wrong bar color '"
					+ color + "' defined!");
		}

		try {
			barStyle = BarStyle.valueOf(style.toUpperCase());
		} catch (IllegalArgumentException ignored) {
			barStyle = BarStyle.SOLID;
			MagicSpells.error("BossBarEffect '"
					+ config.getCurrentPath()
					+ "' has a wrong bar style '"
					+ style + "' defined!");
		}

		duration = config.getInt("duration", 60);
		progress = config.getDouble("progress", 1);
		if (progress > 1) progress = 1;
		if (progress < 0) progress = 0;
	}

	@Override
	public void initializeModifiers(Spell spell) {
		super.initializeModifiers(spell);

		if (remove) return;

		variable = MagicSpells.getVariableManager().getVariable(strVar);
		if (variable == null && !strVar.isEmpty()) {
			MagicSpells.error("Wrong variable defined! '" + strVar + "'");
		}

		maxVariable = MagicSpells.getVariableManager().getVariable(maxVar);
		if (maxVariable == null && !maxVar.isEmpty()) {
			MagicSpells.error("Wrong variable defined! '" + maxVar + "'");
		}
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		if (!remove && (barStyle == null || barColor == null)) return null;

		if (broadcast) Util.forEachPlayerOnline(this::createBar);
		else if (entity instanceof Player pl) createBar(pl);

		return null;
	}

	private void createBar(Player player) {
		Bar bar = MagicSpells.getBossBarManager().getBar(player, namespaceKey, !remove);
		String key;
		if (remove) {
			if (bar == null) return;
			key = bar.getNamespaceKey();
			if (tasks.containsKey(key)) {
				Bukkit.getScheduler().cancelTask(tasks.get(key));
				tasks.remove(key);
			}
			bar.remove();
			return;
		}

		double progress = this.progress;
		if (variable != null) {
			progress = variable.getValue(player) / (maxVariable == null ? maxValue : maxVariable.getValue(player));

			if (progress < 0D) progress = 0D;
			if (progress > 1D) progress = 1D;
		}

		String title = Util.doVarReplacementAndColorize(player, this.title);
		bar.set(title, progress, barStyle, barColor, visible);
		key = bar.getNamespaceKey();

		if (duration > 0) {
			if (tasks.containsKey(key)) Bukkit.getScheduler().cancelTask(tasks.get(key));

			int task = MagicSpells.scheduleDelayedTask(() -> {
				tasks.remove(bar.getNamespaceKey());
                bar.remove();
            }, duration);

			tasks.put(key, task);
		}
	}

	@Override
	public void turnOff() {
		for (int i : tasks.values()) {
			Bukkit.getScheduler().cancelTask(i);
		}
		tasks.clear();
	}

}
