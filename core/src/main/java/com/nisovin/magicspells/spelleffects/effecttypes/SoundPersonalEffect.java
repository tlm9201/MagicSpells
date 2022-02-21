package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class SoundPersonalEffect extends SoundEffect {

	private ConfigData<SoundPosition> source;

	private boolean broadcast;
	private boolean useListenerAsTarget;
	private boolean resolveSoundPerPlayer;
	private boolean resolvePitchPerPlayer;
	private boolean resolveVolumePerPlayer;
	private boolean resolveCategoryPerPlayer;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		super.loadFromConfig(config);

		source = ConfigDataUtil.getEnum(config, "source", SoundPosition.class, SoundPosition.LISTENER);

		broadcast = config.getBoolean("broadcast", false);
		useListenerAsTarget = config.getBoolean("use-listener-as-target", false);
		resolveSoundPerPlayer = config.getBoolean("resolve-sound-per-player", false);
		resolvePitchPerPlayer = config.getBoolean("resolve-pitch-per-player", false);
		resolveVolumePerPlayer = config.getBoolean("resolve-volume-per-player", false);
		resolveCategoryPerPlayer = config.getBoolean("resolve-category-per-player", false);
	}

	@Override
	public Runnable playEffectEntity(Entity entity, SpellData data) {
		if (broadcast) {
			if (entity != null) playEffectLocation(entity.getLocation(), data);
			return null;
		}

		SoundPosition position = source.get(data);
		if (entity instanceof Player player) {
			Location location = switch (position) {
				case CASTER -> {
					LivingEntity caster = data.caster();
					yield caster == null ? null : caster.getLocation();
				}
				case TARGET -> {
					LivingEntity target = data.target();
					yield target == null ? null : target.getLocation();
				}
				case LISTENER, POSITION -> entity.getLocation();
			};
			if (location == null) return null;

			player.playSound(applyOffsets(location, data), sound.get(data), category.get(data), volume.get(data), pitch.get(data));
		}

		return null;
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		if (!broadcast) return null;

		SoundPosition position = source.get(data);

		Location l = switch (position) {
			case CASTER -> {
				LivingEntity caster = data.caster();
				yield caster == null ? null : caster.getLocation();
			}
			case TARGET -> {
				LivingEntity target = data.target();
				yield target == null ? null : target.getLocation();
			}
			case POSITION -> location == null ? null : location.clone();
			case LISTENER -> null;
		};

		boolean listener = position == SoundPosition.LISTENER;
		if (l == null && !listener) return null;

		String sound = resolveSoundPerPlayer ? null : this.sound.get(data);
		float pitch = resolvePitchPerPlayer ? 0 : this.pitch.get(data);
		float volume = resolveVolumePerPlayer ? 0 : this.volume.get(data);
		SoundCategory category = resolveCategoryPerPlayer ? null : this.category.get(data);

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (useListenerAsTarget && data != null)
				data = new SpellData(data.caster(), player, data.power(), data.args());

			if (resolveSoundPerPlayer) sound = this.sound.get(data);
			if (resolvePitchPerPlayer) pitch = this.pitch.get(data);
			if (resolveVolumePerPlayer) volume = this.volume.get(data);
			if (resolveCategoryPerPlayer) category = this.category.get(data);

			if (sound == null || category == null) continue;
			player.playSound(listener ? player.getLocation() : l, sound, category, pitch, volume);
		}

		return null;
	}

	private enum SoundPosition {

		CASTER,
		TARGET,
		LISTENER,
		POSITION

	}

}
