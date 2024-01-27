package com.nisovin.magicspells.util.ai;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Mob;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;

/**
 * Annotate this class with {@link Name} which will hold the configuration name of the goal.
 */
public abstract class CustomGoal implements Goal<Mob> {

	protected final Mob mob;
	protected final SpellData data;

	protected static final Random RANDOM = ThreadLocalRandom.current();

	public CustomGoal(Mob mob, SpellData data) {
		this.mob = mob;
		this.data = data;
	}

	public abstract boolean initialize(@Nullable ConfigurationSection config);

	@NotNull
	@Override
	public final GoalKey<Mob> getKey() {
		Name name = getClass().getAnnotation(Name.class);
		if (name == null) throw new IllegalStateException("Missing 'Name' annotation from Goal class");
		return GoalKey.of(Mob.class, new NamespacedKey(MagicSpells.getInstance(), name.name()));
	}

}
