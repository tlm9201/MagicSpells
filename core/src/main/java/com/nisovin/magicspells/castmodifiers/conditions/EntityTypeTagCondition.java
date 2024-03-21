package com.nisovin.magicspells.castmodifiers.conditions;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.castmodifiers.Condition;

@Name("entitytypetag")
public class EntityTypeTagCondition extends Condition {

	private List<Tag<EntityType>> tags;

	@Override
	public boolean initialize(@NotNull String var) {
		if (var.isEmpty()) return false;

		tags = new ArrayList<>();

		String[] tagStrings = var.split(",");
		for (String tagString : tagStrings) {
			NamespacedKey key = NamespacedKey.fromString(tagString);
			if (key == null) return false;

			Tag<EntityType> tag = Bukkit.getTag("entity_types", key, EntityType.class);
			if (tag == null) return false;

			tags.add(tag);
		}

		return !this.tags.isEmpty();
	}

	@Override
	public boolean check(LivingEntity caster) {
		EntityType type = caster.getType();

		for (Tag<EntityType> tag : tags)
			if (tag.isTagged(type))
				return true;

		return false;
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return check(target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

}
