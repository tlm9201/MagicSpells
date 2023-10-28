package com.nisovin.magicspells.spells.targeted;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

// TODO setup a system for registering "CleanseProvider"s
public class CleanseSpell extends TargetedSpell implements TargetedEntitySpell {

	private ValidTargetChecker checker;

	private final List<String> toCleanse;
	private final List<DotSpell> dotSpells;
	private final List<StunSpell> stunSpells;
	private final List<BuffSpell> buffSpells;
	private final List<LoopSpell> loopSpells;
	private final List<OrbitSpell> orbitSpells;
	private final List<TotemSpell> totemSpells;
	private final List<SilenceSpell> silenceSpells;
	private final List<LevitateSpell> levitateSpells;
	private final List<PotionEffectType> potionEffectTypes;

	private boolean fire;

	public CleanseSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		toCleanse = getConfigStringList("remove", Arrays.asList("fire", "hunger", "poison", "wither"));
		dotSpells = new ArrayList<>();
		stunSpells = new ArrayList<>();
		buffSpells = new ArrayList<>();
		loopSpells = new ArrayList<>();
		orbitSpells = new ArrayList<>();
		totemSpells = new ArrayList<>();
		silenceSpells = new ArrayList<>();
		levitateSpells = new ArrayList<>();
		potionEffectTypes = new ArrayList<>();
		fire = false;
	}

	@Override
	public void initialize() {
		super.initialize();

		for (String s : toCleanse) {
			if (s.equalsIgnoreCase("fire")) {
				fire = true;
				continue;
			}

			if (s.startsWith("buff:")) {
				if (s.replace("buff:", "").equalsIgnoreCase("*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof BuffSpell) buffSpells.add((BuffSpell) spell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("buff:", ""));
				if (spell instanceof BuffSpell) buffSpells.add((BuffSpell) spell);
				continue;
			}

			if (s.startsWith("dot:")) {
				if (s.replace("dot:", "").equalsIgnoreCase("*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof DotSpell) dotSpells.add((DotSpell) spell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("dot:", ""));
				if (spell instanceof DotSpell) dotSpells.add((DotSpell) spell);
				continue;
			}

			if (s.startsWith("stun:")) {
				if (s.replace("stun:", "").equalsIgnoreCase("*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof StunSpell) stunSpells.add((StunSpell) spell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("stun:", ""));
				if (spell instanceof StunSpell) stunSpells.add((StunSpell) spell);
				continue;
			}

			if (s.startsWith("orbit:")) {
				if (s.replace("orbit:", "").equalsIgnoreCase("*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof OrbitSpell) orbitSpells.add((OrbitSpell) spell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("orbit:", ""));
				if (spell instanceof OrbitSpell) orbitSpells.add((OrbitSpell) spell);
				continue;
			}

			if (s.startsWith("totem:")) {
				if (s.replace("totem:", "").equalsIgnoreCase("*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof TotemSpell) totemSpells.add((TotemSpell) spell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("totem:", ""));
				if (spell instanceof TotemSpell) totemSpells.add((TotemSpell) spell);
				continue;
			}

			if (s.startsWith("silence:")) {
				if (s.replace("silence:", "").equalsIgnoreCase("*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof SilenceSpell) silenceSpells.add((SilenceSpell) spell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("silence:", ""));
				if (spell instanceof SilenceSpell) silenceSpells.add((SilenceSpell) spell);
				continue;
			}

			if (s.startsWith("levitate:")) {
				if (s.replace("levitate:", "").equalsIgnoreCase("*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof LevitateSpell) levitateSpells.add((LevitateSpell) spell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("levitate:", ""));
				if (spell instanceof LevitateSpell) levitateSpells.add((LevitateSpell) spell);
				continue;
			}

			if (s.startsWith("loop:")) {
				if (s.equals("loop:*")) {
					for (Spell spell : MagicSpells.getSpellsOrdered()) {
						if (spell instanceof LoopSpell loopSpell) loopSpells.add(loopSpell);
					}
					continue;
				}
				Spell spell = MagicSpells.getSpellByInternalName(s.replace("loop:", ""));
				if (spell instanceof LoopSpell loopSpell) loopSpells.add(loopSpell);
				continue;
			}

			PotionEffectType type = Util.getPotionEffectType(s);
			if (type != null) potionEffectTypes.add(type);
		}

		checker = entity -> {
			if (fire && entity.getFireTicks() > 0) return true;

			for (PotionEffectType type : potionEffectTypes) {
				if (entity.hasPotionEffect(type)) return true;
			}

			for (BuffSpell spell : buffSpells) {
				if (spell.isActive(entity)) return true;
			}

			for (DotSpell spell : dotSpells) {
				if (spell.isActive(entity)) return true;
			}

			for (StunSpell spell : stunSpells) {
				if (spell.isStunned(entity)) return true;
			}

			for (OrbitSpell spell : orbitSpells) {
				if (spell.hasOrbit(entity)) return true;
			}

			for (TotemSpell spell : totemSpells) {
				if (spell.hasTotem(entity)) return true;
			}

			for (SilenceSpell spell : silenceSpells) {
				if (spell.isSilenced(entity)) return true;
			}

			for (LevitateSpell spell : levitateSpells) {
				if (spell.isBeingLevitated(entity)) return true;
			}

			for (LoopSpell spell : loopSpells) {
				if (spell.isActive(entity)) return true;
			}

			return false;
		};
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data, checker);
		if (info.noTarget()) return noTarget(info);
		data = info.spellData();

		return castAtEntity(data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		LivingEntity target = data.target();

		if (fire) target.setFireTicks(0);

		for (PotionEffectType type : potionEffectTypes) {
			target.removePotionEffect(type);
		}

		buffSpells.forEach(spell -> spell.turnOff(target));
		dotSpells.forEach(spell -> spell.cancelDot(target));
		stunSpells.forEach(spell -> spell.removeStun(target));
		loopSpells.forEach(spell -> spell.cancelLoops(target));
		orbitSpells.forEach(spell -> spell.removeOrbits(target));
		totemSpells.forEach(spell -> spell.removeTotems(target));
		silenceSpells.forEach(spell -> spell.removeSilence(target));
		levitateSpells.forEach(spell -> spell.removeLevitate(target));

		playSpellEffects(data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public ValidTargetChecker getValidTargetChecker() {
		return checker;
	}

}
