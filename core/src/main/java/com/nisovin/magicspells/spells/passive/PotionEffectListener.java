package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.*;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

public class PotionEffectListener extends PassiveListener {

	private List<PotionEffectType> types;
	private EnumSet<Action> actions;
	private EnumSet<Cause> causes;

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;

		types = new ArrayList<>();
		actions = EnumSet.noneOf(Action.class);
		causes = EnumSet.noneOf(Cause.class);

		String[] splits = var.toUpperCase().split(" ");
		//Asterisks are wildcards, for when you want the parameter to pass on *any* value
		if (splits[0].equals("*")) {
			//It's dirty, but it works. If a wildcard is used, dump every value into the list.
			types = Arrays.asList(PotionEffectType.values());
		} else {
			//Each parameter can accept a list of options, separated by commas
			for (String s : splits[0].split(",")) {
				PotionEffectType type = PotionEffectType.getByName(s);

				if (type == null) {
					MagicSpells.error("Invalid effect '" + s + "' in potioneffect trigger on passive spell '" + passiveSpell.getInternalName() + "'");
				}
				else types.add(type);
			}
		}

		if (splits.length > 1 && !splits[1].equals("*")) {
			for (String s : splits[1].split(",")) {
				try {
					Action action = Action.valueOf(s);
					actions.add(action);
				} catch (IllegalArgumentException e) {
					MagicSpells.error("Invalid action '" + s + "' in potioneffect trigger on passive spell '" + passiveSpell.getInternalName() + "'");
				}
			}
		} else actions = EnumSet.allOf(Action.class);

		if (splits.length > 1 && !splits[2].equals("*")) {
			for (String s : splits[2].split(",")) {
				try {
					Cause cause = Cause.valueOf(s);
					causes.add(cause);
				} catch (IllegalArgumentException e) {
					MagicSpells.error("Invalid cause '" + s + "' in potioneffect trigger on passive spell '" + passiveSpell.getInternalName() + "'");
				}
			}
		} else causes = EnumSet.allOf(Cause.class);
	}

	@EventHandler
	public void onPotionEffect(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (!isCancelStateOk(event.isCancelled())) return;

		if (!actions.contains(event.getAction()) || !causes.contains(event.getCause())) return;

		if (!canTrigger(entity)) return;

		PotionEffectType type = null;
		PotionEffect effect;
		switch (event.getAction()) { //The effect used by the event is referenced differently based on the action, so unfortunately this is needed
			case ADDED -> {
				effect = event.getNewEffect();
				if (effect != null) type = effect.getType();
			}
			case CHANGED -> type = event.getModifiedType();
			case REMOVED, CLEARED -> {
				effect = event.getOldEffect();
				if (effect != null) type = effect.getType();
			}
		}

		if (type == null || !types.contains(type)) return;

		boolean casted = passiveSpell.activate(entity);
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

}
