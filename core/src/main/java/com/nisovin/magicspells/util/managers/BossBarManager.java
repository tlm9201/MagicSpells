package com.nisovin.magicspells.util.managers;

import java.util.*;
import java.util.regex.Pattern;

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

	private static final Pattern VALID_NAMESPACE_KEY = Pattern.compile("[a-z0-9._-]+");

	private static final String NAMESPACE_KEY_DEFAULT = "ms_default";
	private static final String NAMESPACE_KEY_VARIABLE = "ms_variable";

	private final Set<Bar> bars = new HashSet<>();

	public String getNamespaceKeyVariable() {
		return NAMESPACE_KEY_VARIABLE;
	}

	public boolean isNamespaceKey(String namespaceKey) {
		return VALID_NAMESPACE_KEY.matcher(namespaceKey).matches();
	}

	private NamespacedKey createNamespaceKey(String namespaceKey) {
		return new NamespacedKey(MagicSpells.plugin, namespaceKey);
	}

	public Bar getBar(Player player, String namespaceKey) {
		return getBar(player, namespaceKey, true);
	}

	public Bar getBar(Player player, String namespaceKey, boolean create) {
		if (namespaceKey == null || namespaceKey.isEmpty()) namespaceKey = NAMESPACE_KEY_DEFAULT;

		// Return bar if it already exists.
		for (Bar bar : bars) {
			if (bar.player.equals(player.getUniqueId()) && bar.namespaceKey.equals(namespaceKey)) {
				return bar;
			}
		}

		// Create bar if specified.
		Bar bar = null;
		if (create) {
			BossBar bossBar = Bukkit.createBossBar(createNamespaceKey(namespaceKey), "", BarColor.WHITE, BarStyle.SOLID);
			bar = new Bar(bossBar, player, namespaceKey);
			bars.add(bar);
		}

		return bar;
	}

	public void disable() {
		bars.forEach(Bar::deleteBossbar);
		bars.clear();
	}

	public class Bar {

		final String namespaceKey;
		final BossBar bossbar;
		final UUID player;

		public Bar(BossBar bossbar, Player player, String namespaceKey) {
			this.bossbar = bossbar;
			this.namespaceKey = namespaceKey;
			this.player = player.getUniqueId();

			bossbar.addPlayer(player);
		}

		public void set(String title, double progress, BarStyle style, BarColor color) {
			bossbar.setTitle(Util.colorize(title));
			bossbar.setStyle(style);
			bossbar.setColor(color);
			bossbar.setProgress(progress);
		}

		public void set(String title, double progress, BarStyle style, BarColor color, boolean visible) {
			set(title, progress, style, color);
			bossbar.setVisible(visible);
		}

		public String getNamespaceKey() {
			return namespaceKey;
		}

		public void addFlag(BarFlag flag) {
			bossbar.addFlag(flag);
		}

		public void deleteBossbar() {
			bossbar.removeAll();
			Bukkit.removeBossBar(createNamespaceKey(namespaceKey));
		}

		public void remove() {
			deleteBossbar();
			bars.remove(this);
		}

	}

}
