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

	private NamespacedKey createNamespace(String namespace) {
		return new NamespacedKey(MagicSpells.plugin, namespace);
	}

	public Bar getBar(Player player, String namespace) {
		if (namespace == null || namespace.isEmpty()) namespace = NAMESPACE_DEFAULT;
		// Check if the bar exists.
		Bar finalBar = null;
		for (Bar bar : bars) {
			if (bar.player.equals(player) && bar.namespace.equals(namespace)) {
				finalBar = bar;
				break;
			}
		}
		// If it doesn't, create it.
		if (finalBar == null) {
			BossBar bossBar = Bukkit.createBossBar(createNamespace(namespace), "", BarColor.WHITE, BarStyle.SOLID);
			finalBar = new Bar(bossBar, player, namespace);
			bars.add(finalBar);
		}
		return finalBar;
	}

	public void turnOff() {
		bars.forEach(Bar::deleteBossbar);
		bars.clear();
	}

	public class Bar {
		final BossBar bossbar;
		final Player player;
		final String namespace;

		Bar(BossBar bossbar, Player player, String namespace) {
			this.bossbar = bossbar;
			this.player = player;
			this.namespace = namespace;
		}

		public void set(String title, double progress, BarStyle style, BarColor color) {
			bossbar.setTitle(Util.colorize(title));
			bossbar.setStyle(style);
			bossbar.setColor(color);
			bossbar.setProgress(progress);
			bossbar.addPlayer(player);
		}

		public void addFlag(BarFlag flag) {
			bossbar.addFlag(flag);
		}

        public void deleteBossbar() {
            bossbar.removeAll();
            Bukkit.removeBossBar(createNamespace(namespace));
        }

		public void remove() {
            deleteBossbar();
			bars.remove(this);
		}
	}
}
