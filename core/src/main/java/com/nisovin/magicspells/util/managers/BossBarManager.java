package com.nisovin.magicspells.util.managers;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.NamespacedKey;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;

public class BossBarManager {

	private final String NAMESPACE_DEFAULT = "ms_default";
	private final String NAMESPACE_VARIABLE = "ms_variable";

	private final Set<Bar> bars = new HashSet<>();

	public String getNamespaceVariable() {
		return NAMESPACE_VARIABLE;
	}

	private Bar findBar(Player player, String namespace) {
		Bar finalBar = null;
		if (namespace == null || namespace.isEmpty()) namespace = NAMESPACE_DEFAULT;
		for (Bar bar : bars) {
			if (bar.player.equals(player) && bar.namespace.equals(namespace)) {
				finalBar = bar;
				break;
			}
		}
		return finalBar;
	}

	public void setPlayerBar(Player player, String namespace, String title, double progress, BarStyle style, BarColor color) {
		createBar(player, namespace, title, progress, style, color);
	}

	public void setPlayerBar(Player player, String namespace, String title, double progress, BarStyle style) {
		createBar(player, namespace, title, progress, style, BarColor.PURPLE);
	}

	public void setPlayerBar(Player player, String namespace, String title, double progress) {
		createBar(player, namespace, title, progress, BarStyle.SOLID, BarColor.PURPLE);
	}

	public void addPlayerBarFlag(Player player, String namespace, BarFlag flag) {
		Bar bar = findBar(player, namespace);
		if (bar == null) return;
		bar.bossbar.addFlag(flag);
	}

	public void removePlayerBar(Player player, String namespace) {
		Bar bar = findBar(player, namespace);
		if (bar == null) return;
		bar.bossbar.removeAll();
		Bukkit.removeBossBar(createNamespace(namespace));
		bars.remove(bar);
	}

	public void turnOff() {
		bars.forEach(bar -> {
			bar.bossbar.removeAll();
			Bukkit.removeBossBar(createNamespace(bar.namespace));
		});
		bars.clear();
	}

	private void createBar(Player player, String namespace, String title, double progress, BarStyle style, BarColor color) {
		if (namespace == null || namespace.isEmpty()) namespace = NAMESPACE_DEFAULT;
		Bar bar = findBar(player, namespace);
		if (bar == null) {
			BossBar bossBar = Bukkit.createBossBar(createNamespace(namespace), Util.colorize(title), color, style);
			bar = new Bar(bossBar, player, namespace);
			bars.add(bar);
		}
		BossBar bossBar = bar.bossbar;
		bossBar.setTitle(Util.colorize(title));
		bossBar.setStyle(style);
		bossBar.setColor(color);
		bossBar.setProgress(progress);
		bossBar.addPlayer(player);
	}

	private NamespacedKey createNamespace(String namespace) {
		return new NamespacedKey(MagicSpells.plugin, namespace);
	}

	private static class Bar {
		final BossBar bossbar;
		final Player player;
		final String namespace;

		Bar(BossBar bossbar, Player player, String namespace) {
			this.bossbar = bossbar;
			this.player = player;
			this.namespace = namespace;
		}
	}
}
