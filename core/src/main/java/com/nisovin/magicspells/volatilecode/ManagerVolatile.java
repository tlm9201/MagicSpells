package com.nisovin.magicspells.volatilecode;

import java.util.Map;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.volatilecode.latest.VolatileCodeLatest;

public class ManagerVolatile {

	private static final Map<String, String> COMPATIBLE_VERSIONS = Map.of(
	);

	private static final VolatileCodeHelper helper = new VolatileCodeHelper() {

		@Override
		public void error(String message) {
			MagicSpells.error(message);
		}

		@Override
		public ScheduledTask scheduleDelayedTask(Runnable task, long delay) {
			return MagicSpells.scheduleDelayedTask(task, delay);
		}

	};

	public static VolatileCodeHandle constructVolatileCodeHandler() {
		VolatileCodeHandle handle;
		try {
			String mcVersion = Bukkit.getMinecraftVersion();
			String convertedVersion = COMPATIBLE_VERSIONS.getOrDefault(mcVersion, mcVersion);
			String version = "v" + convertedVersion.replace(".", "_");
			Class<?> volatileCode = Class.forName("com.nisovin.magicspells.volatilecode." + version + ".VolatileCode_" + version);

			handle = (VolatileCodeHandle) volatileCode.getConstructor(VolatileCodeHelper.class).newInstance(helper);
			MagicSpells.log("Found volatile code handler for " + mcVersion + ".");
			return handle;
		} catch (Throwable ignored) {}

		try {
			handle = new VolatileCodeLatest(helper);
			MagicSpells.log("Using latest volatile code handler.");
		} catch (Throwable ignored) {
			handle = new VolatileCodeDisabled();
			MagicSpells.error("Volatile code handler could not be initialized.");
		}

		return handle;
	}

}
