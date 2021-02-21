package com.nisovin.magicspells.spells.targeted.ext;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import ru.xezard.glow.data.glow.Glow;

import org.apache.commons.lang.RandomStringUtils;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.TargetInfo;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

// NOTE: ProtocolLib is required for this spell class.
public class GlowSpell extends TargetedSpell implements TargetedEntitySpell {

	private final Map<UUID, Glow> glowingEntities;

	private final boolean toggle;
	private final int duration;
	private final boolean clientSide;
	private final boolean async;
	private final int updatePeriod;
	private final List<ChatColor> colors;

	public GlowSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		glowingEntities = new HashMap<>();

		toggle = getConfigBoolean("toggle", false);
		duration = getConfigInt("duration", 0);
		clientSide = getConfigBoolean("client-side", false);
		async = getConfigBoolean("async", true);
		updatePeriod = getConfigInt("update-period", 0);

		colors = new ArrayList<>();
		List<String> colorNames = getConfigStringList("colors", null);
		if (colorNames == null || colorNames.isEmpty()) {
			colorNames = new ArrayList<>();
			colorNames.add(getConfigString("color", "white"));
		}
		for (String colorName : colorNames) {
			colorName = colorName.trim().replaceAll(" ", "_").toUpperCase();
			try {
				colors.add(ChatColor.valueOf(colorName));
			} catch (IllegalArgumentException e) {
				MagicSpells.log("GlowSpell '" + internalName + "' has an invalid color defined': " + colorName);
			}
		}

	}

	@Override
	public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL && livingEntity instanceof Player) {
			TargetInfo<LivingEntity> targetInfo = getTargetedEntity(livingEntity, power);
			if (targetInfo == null) return noTarget(livingEntity);
			LivingEntity target = targetInfo.getTarget();

			sendMessages(livingEntity, target, args);
			glow(livingEntity, target, targetInfo.getPower());
			return PostCastAction.NO_MESSAGES;
		}
		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		if (!validTargetList.canTarget(caster, target)) return false;
		glow(caster, target, power);
		return true;
	}

	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		if (!validTargetList.canTarget(target)) return false;
		glow(null, target, power);
		return true;
	}

	@Override
	public void turnOff() {
		glowingEntities.values().forEach(Glow::destroy);
		glowingEntities.clear();
	}

	private boolean isGlowing(LivingEntity entity) {
		return glowingEntities.containsKey(entity.getUniqueId());
	}

	private void glow(LivingEntity caster, LivingEntity target, float power) {
		int duration = Math.round(this.duration * power);
		UUID uuid = target.getUniqueId();

		// Handle reapply and toggle.
		if (isGlowing(target)) {
			glowingEntities.remove(uuid).destroy();
			if (toggle) return;
		}

		Glow glow = Glow.builder()
				.plugin(MagicSpells.getInstance())
				// We should name the team something random, but within name length bounds.
				.name(RandomStringUtils.random(16, true, true))
				// From config
				.asyncAnimation(async)
				.updatePeriod(updatePeriod)
				.animatedColor(colors)
				.build();
		glow.addHolders(target);
		if (clientSide && caster instanceof Player) glow.display((Player) caster);
		else glow.display(Bukkit.getOnlinePlayers());

		glowingEntities.put(uuid, glow);
		if (duration > 0) {
			MagicSpells.scheduleDelayedTask(() -> {
				// Safeguard
				if (glow.getViewers().isEmpty()) return;
				glow.destroy();
				glowingEntities.remove(uuid);
			}, duration);
		}

		// Play effects.
		if (caster != null) playSpellEffects(caster, target);
		else playSpellEffects(EffectPosition.TARGET, target);
		playSpellEffectsBuff(target, entity -> entity instanceof LivingEntity && isGlowing((LivingEntity) entity));
	}

}
