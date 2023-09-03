package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class SoundPersonalEffect extends SoundEffect {

	private ConfigData<SoundTarget> target;

	private ConfigData<Boolean> broadcast;
	private ConfigData<Boolean> useListenerAsTarget;
	private ConfigData<Boolean> useListenerAsDefault;
	private ConfigData<Boolean> resolveSoundPerPlayer;
	private ConfigData<Boolean> resolvePitchPerPlayer;
	private ConfigData<Boolean> resolveVolumePerPlayer;
	private ConfigData<Boolean> resolveCategoryPerPlayer;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		super.loadFromConfig(config);

		target = ConfigDataUtil.getEnum(config, "target", SoundTarget.class, SoundTarget.POSITION);

		broadcast = ConfigDataUtil.getBoolean(config, "broadcast", false);
		useListenerAsTarget = ConfigDataUtil.getBoolean(config, "use-listener-as-target", false);
		useListenerAsDefault = ConfigDataUtil.getBoolean(config, "use-listener-as-default", true);
		resolveSoundPerPlayer = ConfigDataUtil.getBoolean(config, "resolve-sound-per-player", false);
		resolvePitchPerPlayer = ConfigDataUtil.getBoolean(config, "resolve-pitch-per-player", false);
		resolveVolumePerPlayer = ConfigDataUtil.getBoolean(config, "resolve-volume-per-player", false);
		resolveCategoryPerPlayer = ConfigDataUtil.getBoolean(config, "resolve-category-per-player", false);
	}

	@Override
	public Runnable playEffectEntity(Entity entity, SpellData data) {
		if (broadcast.get(data)) {
			broadcast(data);
			return null;
		}

		Player target = getTarget(entity, data);
		if (target != null) {
			if (useListenerAsTarget.get(data)) data = data.target(target);
			if (useListenerAsDefault.get(data)) data = data.recipient(target);

			target.playSound(applyOffsets(entity.getLocation(), data), sound.get(data), category.get(data), volume.get(data), pitch.get(data));
		}

		return null;
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		if (broadcast.get(data)) {
			broadcast(data);
			return null;
		}

		Player target = getTarget(null, data);
		if (target != null) {
			if (useListenerAsTarget.get(data)) data = data.target(target);
			if (useListenerAsDefault.get(data)) data = data.recipient(target);

			target.playSound(location, sound.get(data), category.get(data), volume.get(data), pitch.get(data));
		}

		return null;
	}

	private Player getTarget(Entity entity, SpellData data) {
		return switch (target.get(data)) {
			case CASTER -> {
				LivingEntity caster = data.caster();
				yield caster instanceof Player player ? player : null;
			}
			case TARGET -> {
				LivingEntity target = data.target();
				yield target instanceof Player player ? player : null;
			}
			case POSITION -> entity instanceof Player player ? player : null;
		};
	}

	private void broadcast(SpellData data) {
		boolean useListenerAsTarget = this.useListenerAsTarget.get(data);
		boolean useListenerAsDefault = this.useListenerAsDefault.get(data);
		boolean resolvePitchPerPlayer = this.resolvePitchPerPlayer.get(data);
		boolean resolveVolumePerPlayer = this.resolveVolumePerPlayer.get(data);
		boolean resolveSoundPerPlayer = this.resolveSoundPerPlayer.get(data);
		boolean resolveCategoryPerPlayer = this.resolveCategoryPerPlayer.get(data);

		float pitch = resolvePitchPerPlayer ? 0 : this.pitch.get(data);
		float volume = resolveVolumePerPlayer ? 0 : this.volume.get(data);
		String sound = resolveSoundPerPlayer ? null : this.sound.get(data);
		SoundCategory category = resolveCategoryPerPlayer ? null : this.category.get(data);

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (useListenerAsTarget) data = data.target(player);
			if (useListenerAsDefault) data = data.recipient(player);

			if (resolveSoundPerPlayer) sound = this.sound.get(data);
			if (resolvePitchPerPlayer) pitch = this.pitch.get(data);
			if (resolveVolumePerPlayer) volume = this.volume.get(data);
			if (resolveCategoryPerPlayer) category = this.category.get(data);

			if (sound == null || category == null) continue;
			player.playSound(player.getLocation(), sound, category, pitch, volume);
		}
	}

	private enum SoundTarget {

		CASTER,
		TARGET,
		POSITION

	}

}
