package com.nisovin.magicspells.spells.targeted;

import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.util.data.DataLivingEntity;

public class DataSpell extends TargetedSpell implements TargetedEntitySpell {

	private final ConfigData<Function<? super LivingEntity, String>> dataElement;
	private final ConfigData<String> variableName;

	public DataSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		variableName = getConfigDataString("variable-name", "");

		ConfigData<String> supplier = getConfigDataString("data-element", "uuid");
		if (supplier.isConstant()) {
			Function<? super LivingEntity, String> function = DataLivingEntity.getDataFunction(supplier.get());
			dataElement = data -> function;
		} else {
			dataElement = data -> DataLivingEntity.getDataFunction(supplier.get(data));
		}
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Player player)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);
		data = info.spellData();

		return setVariable(player, data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!(data.caster() instanceof Player player)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		return setVariable(player, data);
	}

	public CastResult setVariable(Player player, SpellData data) {
		Function<? super LivingEntity, String> dataElement = this.dataElement.get(data);
		if (dataElement == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		String value = dataElement.apply(data.target());
		String variableName = this.variableName.get(data);
		MagicSpells.getVariableManager().set(variableName, player, value);

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
