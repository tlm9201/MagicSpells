package com.nisovin.magicspells.spells.passive;

import java.util.Set;
import java.util.EnumSet;

import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPoseChangeEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

public class StopPoseListener extends PassiveListener {

	private Set<Pose> poses;

	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;

		poses = EnumSet.noneOf(Pose.class);

		for (String pose : var.split(",")) {
			try {
				poses.add(Pose.valueOf(pose.trim().toUpperCase()));
			} catch (IllegalArgumentException e) {
				MagicSpells.error("Invalid pose '" + pose + "' in startpose trigger on passive spell '" + passiveSpell.getInternalName() + "'");
			}
		}
	}

	@OverridePriority
	@EventHandler
	public void onPoseChange(EntityPoseChangeEvent event) {
		if (!(event.getEntity() instanceof LivingEntity caster) || !canTrigger(caster) || !hasSpell(caster)) return;
		if (poses != null && !poses.contains(caster.getPose())) return;

		passiveSpell.activate(caster);
	}

}
