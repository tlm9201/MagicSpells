package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class TransmuteSpell extends TargetedSpell implements TargetedLocationSpell {

	private final List<BlockData> blockTypes;

	private final ConfigData<BlockData> transmuteType;

	public TransmuteSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		blockTypes = new ArrayList<>();

		List<String> list = getConfigStringList("transmutable-types", null);
		if (list != null && !list.isEmpty()) {
			for (String s : list) {
				try {
					BlockData data = Bukkit.createBlockData(s.toLowerCase());
					blockTypes.add(data);
				} catch (IllegalArgumentException e) {
					MagicSpells.error("Invalid transmutable type '" + s + "' in TransmuteSpell '" + internalName + "'.");
				}
			}
		} else blockTypes.add(Material.IRON_BLOCK.createBlockData());

		transmuteType = getConfigDataBlockData("transmute-type", Material.GOLD_BLOCK.createBlockData());
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<Location> info = getTargetedBlockLocation(data, false);
		if (info.noTarget()) return noTarget(info);

		return castAtLocation(info.spellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		Location location = data.location();

		Block block = location.getBlock();
		if (!canTransmute(block)) return noTarget(data);

		data = data.location(location.toCenterLocation());

		block.setBlockData(transmuteType.get(data));
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private boolean canTransmute(Block block) {
		BlockData bd = block.getBlockData();

		for (BlockData data : blockTypes)
			if (bd.matches(data))
				return true;

		return false;
	}

}
