package com.nisovin.magicspells.spells.buff;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.apache.commons.math3.util.FastMath;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class HasteSpell extends BuffSpell {

	private final Map<UUID, HasteData> players;

	private int strength;
	private int boostDuration;
	private int accelerationDelay;
	private int accelerationAmount;
	private int accelerationIncrease;
	private int accelerationInterval;

	private boolean hidden;
	private boolean acceleration;

	public HasteSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		strength = getConfigInt("effect-strength", 3);
		boostDuration = getConfigInt("boost-duration", 300);
		accelerationDelay = getConfigInt("acceleration-delay", 0);
		accelerationAmount = getConfigInt("acceleration-amount", 0);
		accelerationIncrease = getConfigInt("acceleration-increase", 0);
		accelerationInterval = getConfigInt("acceleration-interval", 0);

		hidden = getConfigBoolean("hidden", false);
		if (accelerationDelay >= 0 && accelerationAmount > 0 && accelerationIncrease > 0 && accelerationInterval > 0) acceleration = true;

		players = new HashMap<>();
	}

	@Override
	public boolean castBuff(LivingEntity entity, float power, String[] args) {
		if (!(entity instanceof Player)) return true;
		players.put(entity.getUniqueId(), new HasteData(FastMath.round(strength * power)));
		return true;
	}

	@Override
	public boolean isActive(LivingEntity entity) {
		return players.containsKey(entity.getUniqueId());
	}

	@Override
	public void turnOffBuff(LivingEntity entity) {
		HasteData data = players.get(entity.getUniqueId());
		if (data == null) return;
		MagicSpells.cancelTask(data.task);
		players.remove(entity.getUniqueId());
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0));
		entity.removePotionEffect(PotionEffectType.SPEED);
	}

	@Override
	protected void turnOff() {
		for (UUID id : players.keySet()) {
			Player player = Bukkit.getPlayer(id);
			if (player == null) continue;
			player.removePotionEffect(PotionEffectType.SPEED);
			HasteData data = players.get(id);
			if (data != null) MagicSpells.cancelTask(data.task);
		}

		players.clear();
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if (!isActive(player)) return;

		if (isExpired(player)) {
			turnOff(player);
			return;
		}

		HasteData data = players.get(player.getUniqueId());
		int amplifier = data.strength;

		if (event.isSprinting()) {
			event.setCancelled(true);
			addUseAndChargeCost(player);
			playSpellEffects(EffectPosition.CASTER, player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, boostDuration, amplifier, false, !hidden));
			if (acceleration) {
				data.task = MagicSpells.scheduleRepeatingTask(() -> {
					if (data.count >= accelerationAmount) {
						MagicSpells.cancelTask(data.task);
						return;
					}
					data.count++;
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, boostDuration, amplifier + (data.count * accelerationIncrease), false, !hidden));
				}, accelerationDelay, accelerationInterval);
			}
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0, false, !hidden));
			player.removePotionEffect(PotionEffectType.SPEED);
			playSpellEffects(EffectPosition.DISABLED, player);
			MagicSpells.cancelTask(data.task);
			data.count = 0;
		}
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if (!isActive(player)) return;

		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0, false, !hidden));
		player.removePotionEffect(PotionEffectType.SPEED);

		HasteData data = players.get(player.getUniqueId());
		if (data == null) return;
		MagicSpells.cancelTask(data.task);
	}

	public Map<UUID, HasteData> getPlayers() {
		return players;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getBoostDuration() {
		return boostDuration;
	}

	public void setBoostDuration(int boostDuration) {
		this.boostDuration = boostDuration;
	}

	public int getAccelerationDelay() {
		return accelerationDelay;
	}

	public void setAccelerationDelay(int accelerationDelay) {
		this.accelerationDelay = accelerationDelay;
	}

	public int getAccelerationAmount() {
		return accelerationAmount;
	}

	public void setAccelerationAmount(int accelerationAmount) {
		this.accelerationAmount = accelerationAmount;
	}

	public int getAccelerationIncrease() {
		return accelerationIncrease;
	}

	public void setAccelerationIncrease(int accelerationIncrease) {
		this.accelerationIncrease = accelerationIncrease;
	}

	public int getAccelerationInterval() {
		return accelerationInterval;
	}

	public void setAccelerationInterval(int accelerationInterval) {
		this.accelerationInterval = accelerationInterval;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean hasAcceleration() {
		return acceleration;
	}

	public void setAcceleration(boolean acceleration) {
		this.acceleration = acceleration;
	}

	private static class HasteData {

		private int task;
		private int count;
		private final int strength;

		private HasteData(int strength) {
			this.strength = strength;
		}

	}

}
