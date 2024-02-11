package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.entity.Entity;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;

@Name("effectlibentity")
public class EffectLibEntityEffect extends EffectLibEffect {
	
	@Override
	protected Runnable playEffectEntity(final Entity e, final SpellData data) {
		if (!initialize()) return null;
		return manager.start(className, getParameters(data), e);
	}

}
