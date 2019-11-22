package com.nisovin.magicspells.zones;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.SpellFilter;

public abstract class NoMagicZone implements Comparable<NoMagicZone> {

	private String id;
	private String message;

	private int priority;

	private boolean allowAll;
	private boolean disallowAll;

	private SpellFilter spellFilter;
	
	public final void create(String id, ConfigurationSection config) {
		this.id = id;
		message = config.getString("message", "You are in a no-magic zone.");

		priority = config.getInt("priority", 0);

		allowAll = config.getBoolean("allow-all", false);
		disallowAll = config.getBoolean("disallow-all", true);

		List<String> allowedSpells = config.getStringList("allowed-spells");
		List<String> disallowedSpells = config.getStringList("disallowed-spells");
		List<String> allowedSpellTags = config.getStringList("allowed-spell-tags");
		List<String> disallowedSpellTags = config.getStringList("disallowed-spell-tags");
		spellFilter = new SpellFilter(allowedSpells, disallowedSpells, allowedSpellTags, disallowedSpellTags);

		initialize(config);
	}
	
	public abstract void initialize(ConfigurationSection config);
	
	public final ZoneCheckResult check(Player player, Spell spell) {
		return check(player.getLocation(), spell);
	}
	
	public final ZoneCheckResult check(Location location, Spell spell) {
		if (!inZone(location)) return ZoneCheckResult.IGNORED;
		if (disallowAll) return ZoneCheckResult.DENY;
		if (allowAll) return ZoneCheckResult.ALLOW;
		if (!spellFilter.check(spell)) return ZoneCheckResult.DENY;
		if (spellFilter.check(spell)) return ZoneCheckResult.ALLOW;
		return ZoneCheckResult.IGNORED;
	}
	
	public abstract boolean inZone(Location location);
	
	public String getId() {
		return id;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public int compareTo(NoMagicZone other) {
		if (priority < other.priority) return 1;
		if (priority > other.priority) return -1;
		return id.compareTo(other.id);
	}
	
	public enum ZoneCheckResult {
		
		ALLOW,
		DENY,
		IGNORED
		
	}
	
}
