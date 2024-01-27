package com.nisovin.magicspells.spells.targeted;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.NamespacedKey;
import org.bukkit.structure.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;

public class StructureSpell extends TargetedSpell implements TargetedLocationSpell {

	private final ConfigData<String> structureKey;
	private final ConfigData<StructureRotation> rotation;
	private final ConfigData<Mirror> mirror;
	private final ConfigData<Integer> palette;
	private final ConfigData<Float> integrity;
	private final ConfigData<Boolean> includeEntities;

	private final ConfigData<Vector> relativeOffset;

	private final ConfigData<Vector> absoluteOffset;

	private final ConfigData<Boolean> pointBlank;

	public StructureSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		structureKey = getConfigDataString("structure-key", "");

		rotation = getConfigDataEnum("rotation", StructureRotation.class, StructureRotation.NONE);
		mirror = getConfigDataEnum("mirror", Mirror.class, Mirror.NONE);
		palette = getConfigDataInt("palette", 0);
		integrity = getConfigDataFloat("integrity", 1);
		includeEntities = getConfigDataBoolean("include-entities", true);

		relativeOffset = getConfigDataVector("relative-offset", new Vector());
		absoluteOffset = getConfigDataVector("absolute-offset", new Vector());

		pointBlank = getConfigDataBoolean("point-blank", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		if (pointBlank.get(data)) {
			SpellTargetLocationEvent targetEvent = new SpellTargetLocationEvent(this, data, data.caster().getLocation());
			if (!targetEvent.callEvent()) return noTarget(targetEvent);
			data = targetEvent.getSpellData();
		} else {
			TargetInfo<Location> info = getTargetedBlockLocation(data);
			if (info.noTarget()) return noTarget(info);
			data = info.spellData();
		}

		return castAtLocation(data);
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		NamespacedKey key = null;
		try {
			key = NamespacedKey.fromString(structureKey.get(data));
		} catch (IllegalArgumentException ignore) {}
		if (key == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		Structure structure = Bukkit.getStructureManager().loadStructure(key);
		if (structure == null) {
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		Location target = data.location();

		Vector relativeOffset = this.relativeOffset.get(data);
		Util.applyRelativeOffset(target, relativeOffset);
		data = data.location(target);

		Vector absoluteOffset = this.absoluteOffset.get(data);
		target.add(absoluteOffset);
		data = data.location(target);

		structure.place(data.location(), includeEntities.get(data), rotation.get(data), mirror.get(data), palette.get(data), integrity.get(data), random);

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
