package com.nisovin.magicspells.spells.targeted;

import java.util.HashSet;

import org.bukkit.Tag;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.MagicSpellsPlayerInteractEvent;

public class TelekinesisSpell extends TargetedSpell implements TargetedLocationSpell {

	private final ConfigData<Boolean> checkPlugins;

	public TelekinesisSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		checkPlugins = getConfigDataBoolean("check-plugins", true);

		losTransparentBlocks = new HashSet<>(losTransparentBlocks);
		losTransparentBlocks.removeIf(type -> type == Material.LEVER || Tag.BUTTONS.isTagged(type) || Tag.PRESSURE_PLATES.isTagged(type));
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Location> info = getTargetedBlockLocation(data);
		if (info.noTarget()) return noTarget(info);

		return castAtLocation(info.spellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		Block block = data.location().getBlock();

		Material type = block.getType();
		if (type != Material.LEVER && !Tag.BUTTONS.isTagged(type) && !Tag.PRESSURE_PLATES.isTagged(type))
			return noTarget(data);

		if (checkPlugins.get(data) && data.caster() instanceof Player caster) {
			MagicSpellsPlayerInteractEvent event = new MagicSpellsPlayerInteractEvent(caster, Action.RIGHT_CLICK_BLOCK, caster.getEquipment().getItemInMainHand(), block, BlockFace.SELF);
			event.callEvent();

			if (event.useInteractedBlock() == Result.DENY) return noTarget(data);
		}

		BlockUtils.activatePowerable(block);
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
