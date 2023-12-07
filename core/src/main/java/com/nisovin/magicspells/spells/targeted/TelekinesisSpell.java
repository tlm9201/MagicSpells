package com.nisovin.magicspells.spells.targeted;

import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.util.RayTraceResult;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;
import com.nisovin.magicspells.events.MagicSpellsPlayerInteractEvent;

public class TelekinesisSpell extends TargetedSpell implements TargetedLocationSpell {

	private final ConfigData<Boolean> checkPlugins;

	public TelekinesisSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		checkPlugins = getConfigDataBoolean("check-plugins", true);
	}

	@Override
	public CastResult cast(SpellData data) {
		Location start = data.caster().getEyeLocation();
		Vector direction = start.getDirection();
		World world = start.getWorld();

		boolean losIgnorePassableBlocks = this.losIgnorePassableBlocks.get(data);
		int range = getRange(data);

		RayTraceResult result = world.rayTraceBlocks(start, direction, range, losFluidCollisionMode.get(data), false,
			block -> {
				Material type = block.getType();
				return checkType(type) || !losIgnorePassableBlocks || !block.isPassable() || !losTransparentBlocks.contains(type);
			}
		);
		if (result == null) return noTarget(data);

		Block block = result.getHitBlock();
		if (!checkType(block.getType())) {
			block = block.getRelative(result.getHitBlockFace());
			if (!checkType(block.getType())) return noTarget(data);
		}

		SpellTargetLocationEvent targetEvent = new SpellTargetLocationEvent(this, data, block.getLocation());
		if (!targetEvent.callEvent()) return noTarget(targetEvent);

		return activate(targetEvent.getTargetLocation().getBlock(), targetEvent.getSpellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		Block block = data.location().getBlock();

		Material type = block.getType();
		if (!checkType(type)) return noTarget(data);

		return activate(block, data);
	}

	private CastResult activate(Block block, SpellData data) {
		if (checkPlugins.get(data) && data.caster() instanceof Player caster) {
			MagicSpellsPlayerInteractEvent event = new MagicSpellsPlayerInteractEvent(caster, Action.RIGHT_CLICK_BLOCK, caster.getEquipment().getItemInMainHand(), block, BlockFace.SELF);
			event.callEvent();

			if (event.useInteractedBlock() == Result.DENY) return noTarget(data);
		}

		BlockUtils.activatePowerable(block);
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private boolean checkType(Material type) {
		return type == Material.LEVER || Tag.BUTTONS.isTagged(type) || Tag.PRESSURE_PLATES.isTagged(type);
	}

}
