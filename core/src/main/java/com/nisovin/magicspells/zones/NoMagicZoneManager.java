package com.nisovin.magicspells.zones;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.DependsOn;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.zones.NoMagicZone.ZoneCheckResult;

public class NoMagicZoneManager {

	private Map<String, Class<? extends NoMagicZone>> zoneTypes;
	private Map<String, NoMagicZone> zones;
	private Set<NoMagicZone> zonesOrdered;

	public NoMagicZoneManager() {
		// Create zone types
		zoneTypes = new HashMap<>();
		addZoneType(NoMagicZoneCuboid.class);
		addZoneType(NoMagicZoneWorldGuard.class);
	}

	// DEBUG INFO: level 3, loaded no magic zone, zoneName
	// DEBUG INFO: level 1, no magic zones loaded #
	public void load(MagicConfig config) {
		// Get zones
		zones = new HashMap<>();
		zonesOrdered = new TreeSet<>();

		Set<String> zoneNodes = config.getKeys("no-magic-zones");
		if (zoneNodes != null) {

			ConfigurationSection zoneConfig;
			String type;
			Class<? extends NoMagicZone> clazz;
			NoMagicZone zone;

			for (String node : zoneNodes) {
				zoneConfig = config.getSection("no-magic-zones." + node);

				// Check enabled
				if (!zoneConfig.getBoolean("enabled", true)) continue;

				// Get zone type
				type = zoneConfig.getString("type", "");
				if (type.isEmpty()) {
					MagicSpells.error("Invalid no-magic zone type '" + type + "' on zone '" + node + "'");
					continue;
				}

				clazz = zoneTypes.get(type);
				if (clazz == null) {
					MagicSpells.error("Invalid no-magic zone type '" + type + "' on zone '" + node + "'");
					continue;
				}

				DependsOn dependsOn = clazz.getAnnotation(DependsOn.class);
				if (dependsOn != null && !Util.checkPluginsEnabled(dependsOn.value())) {
					MagicSpells.error("Could not load no magic zone type '" + type + "'.");
					continue;
				}

				// Create zone
				try {
					zone = clazz.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					MagicSpells.error("Failed to create no-magic zone '" + node + "'");
					e.printStackTrace();
					continue;
				}
				zone.create(node, zoneConfig);
				zones.put(node, zone);
				zonesOrdered.add(zone);
				MagicSpells.debug(3, "Loaded no-magic zone: " + node);
			}
		}

		MagicSpells.debug(1, "No-magic zones loaded: " + zones.size());
	}

	public boolean willFizzle(LivingEntity livingEntity, Spell spell) {
		return willFizzle(livingEntity.getLocation(), spell);
	}

	public boolean willFizzle(Location location, Spell spell) {
		if (zonesOrdered == null || zonesOrdered.isEmpty()) return false;
		for (NoMagicZone zone : zonesOrdered) {
			if (zone == null) return false;
			ZoneCheckResult result = zone.check(location, spell);
			if (result == ZoneCheckResult.DENY) return true;
			if (result == ZoneCheckResult.ALLOW) return false;
		}
		return false;
	}

	public boolean inZone(Player player, String zoneName) {
		return inZone(player.getLocation(), zoneName);
	}

	public boolean inZone(Location loc, String zoneName) {
		NoMagicZone zone = zones.get(zoneName);
		return zone != null && zone.inZone(loc);
	}

	@Deprecated
	public void sendNoMagicMessage(LivingEntity caster, Spell spell) {
		sendNoMagicMessage(spell, caster, null);
	}

	@Deprecated
	public void sendNoMagicMessage(Spell spell, LivingEntity caster, String[] args) {
		for (NoMagicZone zone : zonesOrdered) {
			ZoneCheckResult result = zone.check(caster.getLocation(), spell);
			if (result != ZoneCheckResult.DENY) continue;
			MagicSpells.sendMessage(zone.getMessage(), caster, args);
			return;
		}
	}

	public void sendNoMagicMessage(Spell spell, SpellData data) {
		for (NoMagicZone zone : zonesOrdered) {
			ZoneCheckResult result = zone.check(data.caster().getLocation(), spell);
			if (result != ZoneCheckResult.DENY) continue;
			MagicSpells.sendMessage(zone.getMessage(), data.caster(), data);
			return;
		}
	}

	public Map<String, NoMagicZone> getZones() {
		return zones;
	}

	/**
	 * @param type must be annotated with {@link Name}.
	 */
	public void addZoneType(Class<? extends NoMagicZone> type) {
		Name name = type.getAnnotation(Name.class);
		if (name == null) throw new IllegalStateException("Missing 'Name' annotation on NoMagicZone class: " + type.getName());
		zoneTypes.put(name.value(), type);
	}

	/**
	 * @deprecated Use {@link NoMagicZoneManager#addZoneType(Class)}
	 */
	@Deprecated(forRemoval = true)
	public void addZoneType(String name, Class<? extends NoMagicZone> clazz) {
		zoneTypes.put(name, clazz);
	}

	public void disable() {
		if (zoneTypes != null) zoneTypes.clear();
		if (zones != null) zones.clear();
		zoneTypes = null;
		zones = null;
	}

}
