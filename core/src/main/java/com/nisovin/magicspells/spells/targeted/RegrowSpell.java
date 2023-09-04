package com.nisovin.magicspells.spells.targeted;

import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

//This spell currently support the shearing of sheep at the moment.
//Future tweaks for the shearing of other mobs will be added.

public class RegrowSpell extends TargetedSpell implements TargetedEntitySpell {

	private static final ValidTargetChecker SHEEP = entity -> entity instanceof Sheep sheep && sheep.isSheared() && sheep.isAdult();

	private final ConfigData<DyeColor> woolColor;

	private final ConfigData<Boolean> forceWoolColor;
	private final ConfigData<Boolean> randomWoolColor;

	public RegrowSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		woolColor = getConfigDataEnum("wool-color", DyeColor.class, null);

		forceWoolColor = getConfigDataBoolean("force-wool-color", false);
		randomWoolColor = getConfigDataBoolean("random-wool-color", false);

	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data, SHEEP);
		if (info.noTarget()) return noTarget(info);

		return grow((Sheep) info.target(), info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.target() instanceof Sheep sheep) || !sheep.isSheared() || !sheep.isAdult())
			return noTarget(data);

		return grow(sheep, data);
	}

	public CastResult grow(Sheep sheep, SpellData data) {
		if (forceWoolColor.get(data)) {
			DyeColor color = randomWoolColor.get(data) ? randomizeDyeColor() : woolColor.get(data);
			if (color == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

			sheep.setColor(color);
		}

		sheep.setSheared(false);
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private DyeColor randomizeDyeColor() {
		DyeColor[] allDyes = DyeColor.values();
		int dyePosition = random.nextInt(allDyes.length);
		return allDyes[dyePosition];
	}

	@Override
	public ValidTargetChecker getValidTargetChecker() {
		return SHEEP;
	}

}
