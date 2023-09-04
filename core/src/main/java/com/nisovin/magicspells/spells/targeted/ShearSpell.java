package com.nisovin.magicspells.spells.targeted;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

//This spell currently support the shearing of sheep at the moment.
//Future tweaks for the shearing of other mobs will be added.

public class ShearSpell extends TargetedSpell implements TargetedEntitySpell {

	private static final ValidTargetChecker SHEEP = entity -> entity instanceof Sheep sheep && !sheep.isSheared() && sheep.isAdult();

	private final ConfigData<DyeColor> woolColor;

	private final ConfigData<Integer> minWool;
	private final ConfigData<Integer> maxWool;

	private final ConfigData<Double> dropOffset;

	private final ConfigData<Boolean> forceWoolColor;
	private final ConfigData<Boolean> randomWoolColor;

	public ShearSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		woolColor = getConfigDataEnum("wool-color", DyeColor.class, null);

		minWool = getConfigDataInt("min-wool-drop", 1);
		maxWool = getConfigDataInt("max-wool-drop", 3);

		dropOffset = getConfigDataDouble("drop-offset", 1);

		forceWoolColor = getConfigDataBoolean("force-wool-color", false);
		randomWoolColor = getConfigDataBoolean("random-wool-color", false);
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data, SHEEP);
		if (info.noTarget()) return noTarget(info);

		return shear((Sheep) info.target(), info.spellData());
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.target() instanceof Sheep sheep) || sheep.isSheared() || !sheep.isAdult())
			return noTarget(data);

		return shear(sheep, data);
	}

	private CastResult shear(Sheep sheep, SpellData data) {
		DyeColor color;
		if (forceWoolColor.get(data)) {
			if (randomWoolColor.get(data)) {
				DyeColor[] colors = DyeColor.values();
				color = colors[random.nextInt(colors.length)];
			} else {
				color = woolColor.get(data);
				if (color == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);
			}
		} else {
			color = sheep.getColor();
			if (color == null) color = DyeColor.WHITE;
		}

		Material wool = Material.getMaterial(color.name() + "_WOOL");
		if (wool == null) wool = Material.WHITE_WOOL;

		int minWool = this.minWool.get(data);
		int maxWool = this.maxWool.get(data);

		int count;
		if (maxWool != 0) count = random.nextInt((maxWool - minWool) + 1) + minWool;
		else count = random.nextInt(minWool + 1);

		sheep.setSheared(true);
		sheep.getWorld().dropItemNaturally(sheep.getLocation().add(0, dropOffset.get(data), 0), new ItemStack(wool, count));

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
