package com.nisovin.magicspells.spells.targeted;

import org.bukkit.entity.Mob;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.events.SpellTargetEvent;

public class CreatureTargetSpell extends TargetedSpell {

	private String targetSpellName;
	private Subspell targetSpell;

	public CreatureTargetSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		targetSpellName = getConfigString("spell", "");
	}

	@Override
	public void initialize() {
		super.initialize();

		targetSpell = new Subspell(targetSpellName);
		if (!targetSpell.process()) {
			targetSpell = null;
			if (!targetSpellName.isEmpty()) MagicSpells.error("CreatureTargetSpell '" + internalName + "' has an invalid spell defined!");
		}
		targetSpellName = null;
	}

	@Override
	public CastResult cast(SpellData data) {
		if (!(data.caster() instanceof Mob mob)) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		LivingEntity target = mob.getTarget();
		if (target == null || !target.isValid()) return noTarget(data);

		SpellTargetEvent targetEvent = new SpellTargetEvent(this, data, target);
		if (!targetEvent.callEvent()) return noTarget(targetEvent.getSpellData());
		data = targetEvent.getSpellData();

		if (targetSpell != null) targetSpell.subcast(data);
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
