package com.nisovin.magicspells.spells.buff;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TimeUtil;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;

public class WindwalkSpell extends BuffSpell {

	private final Set<UUID> players;

	private int maxY;
	private int maxAltitude;

	private float flySpeed;
	private float launchSpeed;

	private boolean cancelOnLand;

	private HeightMonitor heightMonitor;
	
	public WindwalkSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		maxY = getConfigInt("max-y", 260);
		maxAltitude = getConfigInt("max-altitude", 100);
		flySpeed = getConfigFloat("fly-speed", 0.1F);
		launchSpeed = getConfigFloat("launch-speed", 1F);
		cancelOnLand = getConfigBoolean("cancel-on-land", true);

		players = new HashSet<>();
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		if (cancelOnLand) registerEvents(new SneakListener());
	}

	@Override
	public boolean castBuff(LivingEntity entity, float power, String[] args) {
		if (!(entity instanceof Player)) return true;

		if (launchSpeed > 0) {
			entity.teleport(entity.getLocation().add(0, 0.25, 0));
			entity.setVelocity(new Vector(0, launchSpeed, 0));
		}

		players.add(entity.getUniqueId());
		((Player) entity).setAllowFlight(true);
		((Player) entity).setFlying(true);
		((Player) entity).setFlySpeed(flySpeed);

		if (heightMonitor == null && (maxY > 0 || maxAltitude > 0)) heightMonitor = new HeightMonitor();

		return true;
	}

	@Override
	public boolean isActive(LivingEntity entity) {
		return players.contains(entity.getUniqueId());
	}

	@Override
	public void turnOffBuff(LivingEntity entity) {
		players.remove(entity.getUniqueId());
		((Player) entity).setFlying(false);
		if (((Player) entity).getGameMode() != GameMode.CREATIVE) ((Player) entity).setAllowFlight(false);
		((Player) entity).setFlySpeed(0.1F);
		entity.setFallDistance(0);

		if (heightMonitor != null && players.isEmpty()) {
			heightMonitor.stop();
			heightMonitor = null;
		}
	}

	@Override
	protected void turnOff() {
		for (UUID id : players) {
			Player player = Bukkit.getPlayer(id);
			if (player == null) continue;
			if (!player.isValid()) continue;
			turnOff(player);
		}

		players.clear();

		if (heightMonitor == null) return;
		heightMonitor.stop();
		heightMonitor = null;
	}

	public Set<UUID> getPlayers() {
		return players;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public int getMaxAltitude() {
		return maxAltitude;
	}

	public void setMaxAltitude(int maxAltitude) {
		this.maxAltitude = maxAltitude;
	}

	public float getFlySpeed() {
		return flySpeed;
	}

	public void setFlySpeed(float flySpeed) {
		this.flySpeed = flySpeed;
	}

	public float getLaunchSpeed() {
		return launchSpeed;
	}

	public void setLaunchSpeed(float launchSpeed) {
		this.launchSpeed = launchSpeed;
	}

	public boolean shouldCancelOnLand() {
		return cancelOnLand;
	}

	public void setCancelOnLand(boolean cancelOnLand) {
		this.cancelOnLand = cancelOnLand;
	}

	public class SneakListener implements Listener {
		
		@EventHandler(priority=EventPriority.MONITOR)
		public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
			Player player = event.getPlayer();
			if (!isActive(player)) return;
			if (BlockUtils.isAir(player.getLocation().subtract(0, 1, 0).getBlock().getType())) return;
			turnOff(player);
		}

	}
	
	private class HeightMonitor implements Runnable {

		private final int taskId;

		private HeightMonitor() {
			taskId = MagicSpells.scheduleRepeatingTask( this, TimeUtil.TICKS_PER_SECOND, TimeUtil.TICKS_PER_SECOND);
		}
		
		@Override
		public void run() {
			for (UUID id : players) {
				Player player = Bukkit.getPlayer(id);
				if (player == null) continue;
				if (!player.isValid()) continue;
				addUseAndChargeCost(player);
				if (maxY > 0) {
					int ydiff = player.getLocation().getBlockY() - maxY;
					if (ydiff > 0) {
						player.setVelocity(player.getVelocity().setY(-ydiff * 1.5));
						continue;
					}
				}

				if (maxAltitude > 0) {
					int ydiff = player.getLocation().getBlockY() - player.getWorld().getHighestBlockYAt(player.getLocation()) - maxAltitude;
					if (ydiff > 0) player.setVelocity(player.getVelocity().setY(-ydiff * 1.5));
				}
			}
		}
		
		public void stop() {
			MagicSpells.cancelTask(taskId);
		}
		
	}

}
