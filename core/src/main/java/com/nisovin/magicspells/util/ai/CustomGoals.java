package com.nisovin.magicspells.util.ai;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;

import org.bukkit.entity.Mob;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.VanillaGoal;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.ai.goals.*;
import com.nisovin.magicspells.handlers.DebugHandler;

public class CustomGoals {

	private static final Map<String, Class<? extends CustomGoal>> GOALS = new HashMap<>();
	static {
		addGoal(LookAtEntityTypeGoal.class);
		addGoal(PathToGoal.class);
	}
	
	private static final Map<String, GoalKey<?>> VANILLA_GOAL_KEYS = new HashMap<>();
	static {
		for (Field field : VanillaGoal.class.getDeclaredFields()) {
			try {
				if (!(field.get(null) instanceof GoalKey<?> key)) continue;
				if (field.isAnnotationPresent(Deprecated.class)) continue;
				VANILLA_GOAL_KEYS.put(field.getName(), key);
			}
			catch (IllegalAccessException ignored) {}
		}
	}

	public static Map<String, Class<? extends CustomGoal>> getGoals() {
		return GOALS;
	}

	/**
	 * @return {@link VanillaGoal} mapped by the passed field name.
	 */
	public static GoalKey<?> getVanillaGoal(String fieldName) {
		return VANILLA_GOAL_KEYS.get(fieldName.toUpperCase());
	}

	/**
	 * @param goal must be annotated with {@link Name}.
	 */
	public static void addGoal(Class<? extends CustomGoal> goal) {
		Name name = goal.getAnnotation(Name.class);
		if (name == null) throw new IllegalStateException("Missing 'Name' annotation from CustomGoal class: " + goal.getName());
		GOALS.put(name.value(), goal);
	}

	public static CustomGoal getGoal(String name, Mob mob, SpellData data) {
		Class<? extends CustomGoal> clazz = GOALS.get(name);
		if (clazz == null) return null;
		try {
			return clazz.getDeclaredConstructor(Mob.class, SpellData.class).newInstance(mob, data);
		} catch (Exception e) {
			DebugHandler.debugGeneral(e);
			return null;
		}
	}

}
