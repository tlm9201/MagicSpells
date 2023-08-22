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
	private ConfigData<Title.Times> times;
	private ConfigData<Boolean> broadcast;
	private ConfigData<Boolean> useViewerAsTarget;

	private static Duration millisOfTicks(int ticks) {
		return Duration.ofMillis(TimeUtil.MILLISECONDS_PER_SECOND * (ticks / TimeUtil.TICKS_PER_SECOND));
	}

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		title = config.getString("title", "");
		subtitle = config.getString("subtitle", "");

		ConfigData<Integer> fadeIn = ConfigDataUtil.getInteger(config, "fade-in", 10);
		ConfigData<Integer> stay = ConfigDataUtil.getInteger(config, "stay", 40);
		ConfigData<Integer> fadeOut = ConfigDataUtil.getInteger(config, "fade-out", 10);
		if (fadeIn.isConstant() && stay.isConstant() && fadeOut.isConstant()) {
			Title.Times times = Title.Times.times(
				millisOfTicks(fadeIn.get()),
				millisOfTicks(stay.get()),
				millisOfTicks(fadeOut.get())
			);

			this.times = data -> times;
		} else {
			times = data -> Title.Times.times(
				millisOfTicks(fadeIn.get(data)),
				millisOfTicks(stay.get(data)),
				millisOfTicks(fadeOut.get(data))
			);
		}

		broadcast = ConfigDataUtil.getBoolean(config, "broadcast", false);
		useViewerAsTarget = ConfigDataUtil.getBoolean(config, "use-viewer-as-target", false);
	}

	@Override
	protected Runnable playEffectEntity(Entity entity, SpellData data) {
		if (broadcast.get(data)) Util.forEachPlayerOnline(player -> send(player, data));
		else if (entity instanceof Player player) send(player, data);
		return null;
	}

	private void send(Player player, SpellData data) {
		if (useViewerAsTarget.get(data)) data = data.target(player);

		Component titleComponent = Util.getMiniMessage(title, player, data);
		Component subtitleComponent = Util.getMiniMessage(subtitle, player, data);
		player.showTitle(Title.title(titleComponent, subtitleComponent, times.get(data)));
	}

}
