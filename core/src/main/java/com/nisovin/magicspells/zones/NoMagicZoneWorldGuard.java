package com.nisovin.magicspells.zones;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.DependsOn;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@Name("worldguard")
@DependsOn("WorldGuard")
public class NoMagicZoneWorldGuard extends NoMagicZone {

	private String worldName;
	private String regionName;

	private ProtectedRegion region;

	private boolean global = false;

	@Override
	public void initialize(ConfigurationSection config) {
		worldName = config.getString("world", "");
		regionName = config.getString("region", "");

		World world = Bukkit.getWorld(worldName);
		if (world == null) return;

		RegionManager regionManager = WorldGuard.getInstance()
				.getPlatform()
				.getRegionContainer()
				.get(BukkitAdapter.adapt(world));
		if (regionManager != null) region = regionManager.getRegion(regionName);

		if (regionName.toLowerCase().contains("global")) global = true;
	}

	@Override
	public boolean inZone(Location location) {
		if (!worldName.equals(location.getWorld().getName())) return false;
		if (region == null && !global) {
			MagicSpells.error("Failed to access WorldGuard region '" + regionName + "'");
			return false;
		}

		if (global) return true;
		return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

}
