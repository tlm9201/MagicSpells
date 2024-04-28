package com.nisovin.magicspells.handlers;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TimeUtil;

public class LifeLengthTracker implements Listener {

	private final Map<UUID, Long> lastSpawnMillis = new HashMap<>();
	private final Map<UUID, Integer> lastLifeLength = new HashMap<>();

	public LifeLengthTracker() {
		Util.forEachPlayerOnline(player -> lastSpawnMillis.put(player.getUniqueId(), System.currentTimeMillis()));
		MagicSpells.registerEvents(this);
	}

	public int getCurrentLifeLength(Player player) {
		UUID uuid = player.getUniqueId();
		if (!lastLifeLength.containsKey(uuid)) return 0;
		return durationSeconds(lastSpawnMillis.get(uuid));
	}

	public int getLastLifeLength(Player player) {
		return lastLifeLength.getOrDefault(player.getUniqueId(), 0);
	}

	private static int durationSeconds(long spawnTime) {
		return (int) ((System.currentTimeMillis() - spawnTime) / TimeUtil.MILLISECONDS_PER_SECOND);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		lastSpawnMillis.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		lastSpawnMillis.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		Long spawn = lastSpawnMillis.remove(uuid);
		if (spawn == null) return;
		lastLifeLength.put(uuid, durationSeconds(spawn));
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		Long spawn = lastSpawnMillis.remove(uuid);
		if (spawn == null) return;
		lastLifeLength.put(uuid, durationSeconds(spawn));
	}

}
