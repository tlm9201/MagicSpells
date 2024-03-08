package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.Collection;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.spells.targeted.LoopSpell.Loop;

@Name("ownedloopactive")
public class OwnedLoopActiveCondition extends LoopActiveCondition {

	@Override
	public boolean check(LivingEntity caster) {
		return check(caster, caster);
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		int count = 0;

		Collection<Loop> loops = loop.getActiveLoops().get(target.getUniqueId());
		for (Loop loop : loops)
			if (caster.equals(loop.getCaster()))
				count++;

		return compare(count, value);
	}

}
