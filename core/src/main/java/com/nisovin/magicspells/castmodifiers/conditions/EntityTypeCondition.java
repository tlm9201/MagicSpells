package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Set;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.MobUtil;
import com.nisovin.magicspells.castmodifiers.Condition;

public class EntityTypeCondition extends Condition {

	private boolean player = false;
	private boolean monster = false;
	private boolean animal = false;
	private final Set<EntityType> types = new HashSet<>();
	
	@Override
	public boolean initialize(String var) {
		String[] vars = var.replace(" ", "").split(",");
		for (String v : vars) {
			switch (v.toLowerCase()) {
				case "player" -> player = true;
				case "monster" -> monster = true;
				case "animal" -> animal = true;
				default -> {
					EntityType type = MobUtil.getEntityType(v);
					if (type != null) types.add(type);
				}
			}
		}
		return player || monster || animal || !types.isEmpty();
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return entityType(livingEntity);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return entityType(target);
	}
	
	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean entityType(LivingEntity livingEntity) {
		if (player && livingEntity instanceof Player) return true;
		if (monster && livingEntity instanceof Monster) return true;
		if (animal && livingEntity instanceof Animals) return true;
		return types.contains(livingEntity.getType());
	}

}
