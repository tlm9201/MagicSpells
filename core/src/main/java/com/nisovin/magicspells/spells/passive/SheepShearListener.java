package com.nisovin.magicspells.spells.passive;

import java.util.EnumSet;

import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;

import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

// Optional trigger variable that can either be set to a dye color to accept or "all"
public class SheepShearListener extends PassiveListener {

	private final EnumSet<DyeColor> dyeColors = EnumSet.noneOf(DyeColor.class);
	
	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;

		try {
			DyeColor color = DyeColor.valueOf(var.toUpperCase());
			dyeColors.add(color);
		} catch (Exception e) {
			// ignored
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onSheepShear(PlayerShearEntityEvent event) {
		if (!(event.getEntity() instanceof Sheep)) return;
		Sheep s = (Sheep) event.getEntity();
		Player p = event.getPlayer();
		Spellbook spellbook = MagicSpells.getSpellbook(p);

		if (!dyeColors.isEmpty() && !dyeColors.contains(s.getColor())) return;

		if (!isCancelStateOk(event.isCancelled())) return;
		if (!spellbook.hasSpell(passiveSpell)) return;
		boolean casted = passiveSpell.activate(p);
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

}
