package com.nisovin.magicspells.spells.targeted;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.util.BlockUtils;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;

public class BlinkSpell extends TargetedSpell implements TargetedLocationSpell {

	private final ConfigData<Boolean> passThroughCeiling;

	private final String strCantBlink;

	public BlinkSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		strCantBlink = getConfigString("str-cant-blink", "You can't blink there.");

		passThroughCeiling = getConfigDataBoolean("pass-through-ceiling", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		List<Block> blocks = getLastTwoTargetedBlocks(data);
		if (blocks.isEmpty()) return noTarget(strCantBlink, data);

		Block prev, found;
		if (blocks.size() == 1) {
			prev = null;
			found = blocks.get(0);
		} else {
			prev = blocks.get(0);
			found = blocks.get(1);
		}

		if (BlockUtils.isTransparent(this, found)) return noTarget(strCantBlink, data);

		Location loc = null;
		if (!passThroughCeiling.get(data) && found.getRelative(0, -1, 0).equals(prev) && prev.isPassable()) {
			Block under = prev.getRelative(0, -1, 0);
			if (under.isPassable()) loc = under.getLocation().add(0.5, 0, 0.5);
		} else if (found.getRelative(0, 1, 0).isPassable() && found.getRelative(0, 2, 0).isPassable()) {
			loc = found.getLocation().add(0, 1, 0).add(0.5, 0, 0.5);
		} else if (prev != null && prev.isPassable() && prev.getRelative(0, 1, 0).isPassable()) {
			loc = prev.getLocation().add(0.5, 0, 0.5);
		}
		if (loc == null) return noTarget(strCantBlink, data);

		SpellTargetLocationEvent targetEvent = new SpellTargetLocationEvent(this, data, loc);
		if (!targetEvent.callEvent()) return noTarget(strCantBlink, data);

		return blink(targetEvent.getSpellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		return data.hasCaster() ? blink(data) : new CastResult(PostCastAction.ALREADY_HANDLED, data);
	}

	public CastResult blink(SpellData data) {
		Location origin = data.caster().getLocation();
		Location target = data.location();

		target.setPitch(origin.getPitch());
		target.setYaw(origin.getYaw());

		data.caster().teleportAsync(target);

		data = data.location(target);
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
