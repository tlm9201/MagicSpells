package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.advancement.Advancement;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.Condition;

public class AdvancementCondition extends Condition {

	private Set<Advancement> advancements;

	@Override
	public boolean initialize(String var) {
		if (var == null || var.isEmpty()) return false;

		advancements = new HashSet<>();

		// minecraft advancements must start with minecraft:
		String[] split = var.split(",");
		for (String data : split) {
			NamespacedKey key = NamespacedKey.fromString(data, MagicSpells.getInstance());
			if (key == null) continue;

			Advancement advancement = Bukkit.getAdvancement(key);
			if (advancement == null) continue;

			advancements.add(advancement);
		}

		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return hasAdvancement(livingEntity);
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return hasAdvancement(target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean hasAdvancement(LivingEntity livingEntity) {
		if (!(livingEntity instanceof Player player)) return false;
		for (Advancement advancement : advancements) {
			if (!player.getAdvancementProgress(advancement).isDone()) return false;
		}
		return true;
	}

}
