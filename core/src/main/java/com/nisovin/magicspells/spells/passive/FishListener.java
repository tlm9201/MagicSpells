package com.nisovin.magicspells.spells.passive;

import java.util.EnumSet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerFishEvent;

import org.jetbrains.annotations.NotNull;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.MobUtil;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Trigger variable can optionally include a comma-separated list of fish event states and entity types
@Name("fish")
public class FishListener extends PassiveListener {

	private final EnumSet<PlayerFishEvent.State> states = EnumSet.noneOf(PlayerFishEvent.State.class);
	private final EnumSet<EntityType> types = EnumSet.noneOf(EntityType.class);

	@Override
	public void initialize(@NotNull String var) {
		if (var.isEmpty()) return;
		for (String val : var.replace(" ", "").split(",")) {
			try {
				states.add(PlayerFishEvent.State.valueOf(val.toUpperCase()));
			} catch (IllegalArgumentException e) {
				EntityType type = MobUtil.getEntityType(val);
				if (type == null) {
					MagicSpells.error("Invalid fish event state or entity type '" + val
						+ "' in fish trigger on passive spell '" + passiveSpell.getName() + "'");
					continue;
				}

				types.add(type);
			}
		}
	}

	@OverridePriority
	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if (!isCancelStateOk(event.isCancelled())) return;

		Player caster = event.getPlayer();
		if (!canTrigger(caster)) return;

		if (!states.isEmpty() && !states.contains(event.getState())) return;

		Entity caught = event.getCaught();
		if (!types.isEmpty() && (caught == null || !types.contains(caught.getType()))) return;

		boolean casted = caught instanceof LivingEntity ? passiveSpell.activate(caster, (LivingEntity) caught) :
			passiveSpell.activate(caster, event.getHook().getLocation());
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

}
