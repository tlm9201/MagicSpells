package com.nisovin.magicspells.handlers;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.DeprecationNotice;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;

public class DeprecationHandler {

	private final Multimap<DeprecationNotice, Spell> deprecations = HashMultimap.create();

	public void addDeprecation(@NotNull Spell spell, @NotNull DeprecationNotice deprecationNotice) {
		deprecations.put(deprecationNotice, spell);
	}

	public <T extends Spell> void addDeprecation(@NotNull T spell, @NotNull DeprecationNotice deprecationNotice, boolean check) {
		if (check) addDeprecation(spell, deprecationNotice);
	}

	public void printDeprecationNotices() {
		if (deprecations.isEmpty()) return;

		MagicSpells.error("Usage of deprecated features found. All such usages should be examined and replaced with supported alternatives.");

		for (Map.Entry<DeprecationNotice, Collection<Spell>> entry : deprecations.asMap().entrySet()) {
			DeprecationNotice notice = entry.getKey();
			Collection<Spell> spells = entry.getValue();

			MagicSpells.error("    " + notice.reason());
			MagicSpells.error("        Relevant spells: " + spells.stream()
				.map(Spell::getInternalName)
				.sorted()
				.collect(Collectors.joining(", ", "[", "]"))
			);
			MagicSpells.error("        Steps to take: " + notice.replacement());
			if (notice.context() != null) MagicSpells.error("        Context: " + notice.context());
		}
	}

}
