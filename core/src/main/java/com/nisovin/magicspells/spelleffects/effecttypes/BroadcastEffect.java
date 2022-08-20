package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class BroadcastEffect extends SpellEffect {

	private String message;

	private ConfigData<Double> range;

	private boolean targeted;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		message = config.getString("message", "");
		range = ConfigDataUtil.getDouble(config, "range", 0);
		targeted = config.getBoolean("targeted", false);
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		broadcast(location, message, data);
		return null;
	}

	@Override
	public Runnable playEffectEntity(Entity entity, SpellData data) {
		if (targeted) {
			if (entity instanceof Player player)
				MagicSpells.sendMessage(message, player,  data == null ? null : data.args());

			return null;
		}

		String msg = message;
		if (entity instanceof Player player) {
			String displayName = Util.getStringFromComponent(player.displayName());
			msg = msg.replaceAll("%a(?!rg:)|%t(?!argetvar:)", displayName)
				.replace("%n", entity.getName());
		}
		broadcast(entity == null ? null : entity.getLocation(), msg, data);

		return null;
	}

	private void broadcast(Location location, String message, SpellData data) {
		String[] args =  data == null ? null : data.args();

		double range = this.range.get(data);
		if (range <= 0) {
			Util.forEachPlayerOnline(player -> MagicSpells.sendMessage(message, player, args));
			return;
		}

		if (location == null) return;

		double rangeSq = range * range;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.getWorld().equals(location.getWorld())) continue;
			if (player.getLocation().distanceSquared(location) > rangeSq) continue;

			MagicSpells.sendMessage(message, player, args);
		}
	}

}
