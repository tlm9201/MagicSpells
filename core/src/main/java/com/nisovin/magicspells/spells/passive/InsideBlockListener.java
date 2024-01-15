package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.data.BlockData;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.OverridePriority;
import com.nisovin.magicspells.spells.passive.util.PassiveListener;

public class InsideBlockListener extends PassiveListener {

	private List<BlockData> blockData;

	@Override
	public void initialize(String var) {
		if (var == null || var.isEmpty()) return;

		blockData = new ArrayList<>();

		for (String data : var.split(",(?![^\\[]*])")) {
			try {
				blockData.add(Bukkit.createBlockData(data.trim().toLowerCase()));
			} catch (IllegalArgumentException e) {
				MagicSpells.error("Invalid block data '" + data + "' in insideblock trigger on passive spell '" + passiveSpell.getInternalName() + "'");
			}
		}
	}

	@OverridePriority
	@EventHandler
	public void onSwim(EntityInsideBlockEvent event) {
		if (!isCancelStateOk(event.isCancelled())) return;
		if (!(event.getEntity() instanceof LivingEntity caster) || !canTrigger(caster) || !hasSpell(caster)) return;

		Block block = event.getBlock();
		if (blockData != null && check(block)) return;

		boolean casted = passiveSpell.activate(caster, block.getLocation().add(0.5, 0.5, 0.5));
		if (cancelDefaultAction(casted)) event.setCancelled(true);
	}

	private boolean check(Block block) {
		BlockData bd = block.getBlockData();

		for (BlockData data : blockData)
			if (bd.matches(data))
				return false;

		return true;
	}

}
