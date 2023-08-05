package com.nisovin.magicspells.spells.passive;

import java.util.EnumSet;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// No trigger variable is used here
public class FatalDamageListener extends PassiveListener {

	private final EnumSet<DamageCause> damageCauses = EnumSet.noneOf(DamageCause.class);

	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;
		for (String causeName : var.split("\\|")) {
			DamageCause cause = null;
			try {
				cause = DamageCause.valueOf(causeName.toUpperCase());
			} catch (IllegalArgumentException ignored) {}
			if (cause == null) {
				MagicSpells.error("Invalid damage cause '" + causeName + "' in fataldamage trigger on passive spell '" + passiveSpell.getInternalName() + "'");
				return;
			}
			damageCauses.add(cause);
		}
	}

	@OverridePriority
	@EventHandler
	void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity caster)) return;
		if (!isCancelStateOk(event.isCancelled())) return;
		if (event.getFinalDamage() < caster.getHealth()) return;
		if (!canTrigger(caster) || !hasSpell(caster)) return;
		if (!damageCauses.isEmpty() && !damageCauses.contains(event.getCause())) return;

		boolean casted = passiveSpell.activate(caster);
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

}
