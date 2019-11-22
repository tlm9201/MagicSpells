package com.nisovin.magicspells.util;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.function.Predicate;

import com.nisovin.magicspells.Spell;

public class SpellUtil {
	
	// Currently will only work with direct permission nodes, doesn't handle child nodes yet
	// NOTE: allSpells should be a thread safe collection for read access
	public static Collection<Spell> getSpellsByPermissionNames(final Collection<Spell> allSpells, final Set<String> names) {
		Predicate<Spell> predicate = spell -> names.contains(spell.getPermissionName());
		return getSpellsByX(allSpells, predicate);
	}
	
	// NOTE: allSpells should be a thread safe collection for read access
	// NOTE: streams do work for making the collection thread safe
	public static Collection<Spell> getSpellsByX(final Collection<Spell> allSpells, final Predicate<Spell> predicate) {
		return allSpells
			.parallelStream()
			.filter(predicate)
			.collect(Collectors.toCollection(ArrayList::new));
	}
	
}
