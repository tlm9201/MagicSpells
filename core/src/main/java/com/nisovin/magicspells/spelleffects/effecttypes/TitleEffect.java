package com.nisovin.magicspells.spelleffects.effecttypes;

import java.time.Duration;

import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class TitleEffect extends SpellEffect {

	private String title;
	private String subtitle;
	private Title.Times times;
	private ConfigData<Boolean> broadcast;

	private static Duration milisOfTicks(int ticks) {
		return Duration.ofMillis(TimeUtil.MILLISECONDS_PER_SECOND * (ticks / TimeUtil.TICKS_PER_SECOND));
	}

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		title = config.getString("title", "");
		subtitle = config.getString("subtitle", "");

		int fadeIn = config.getInt("fade-in", 10);
		int stay = config.getInt("stay", 40);
		int fadeOut = config.getInt("fade-out", 10);
		times = Title.Times.times(milisOfTicks(fadeIn), milisOfTicks(stay), milisOfTicks(fadeOut));

		broadcast = ConfigDataUtil.getBoolean(config, "broadcast", false);
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		if (broadcast.get(data)) Util.forEachPlayerOnline(p -> send(p, data.args()));
		else if (entity instanceof Player player) send(player, data.args());
		return null;
	}
	
	private void send(Player player, String[] args) {
		Component titleComponent = Util.getMiniMessageWithArgsAndVars(player, title, args);
		Component subtitleComponent = Util.getMiniMessageWithArgsAndVars(player, subtitle, args);
		player.showTitle(Title.title(titleComponent, subtitleComponent, times));
	}

}
