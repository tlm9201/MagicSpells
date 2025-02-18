package com.nisovin.magicspells.spells;

import com.nisovin.magicspells.events.SpellApplyDamageEvent;

/**
 * @deprecated "Spell damage type" string is now on {@link SpellApplyDamageEvent}.
 */
@Deprecated(forRemoval = true)
public interface DamageSpell {

	@Deprecated(forRemoval = true)
	String getSpellDamageType();

}
