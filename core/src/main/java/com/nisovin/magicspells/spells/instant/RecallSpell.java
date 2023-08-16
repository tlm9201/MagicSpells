package com.nisovin.magicspells.spells.instant;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class RecallSpell extends InstantSpell implements TargetedEntitySpell {

	private final ConfigData<Double> maxRange;

	private final ConfigData<Boolean> useBedLocation;
	private final ConfigData<Boolean> allowCrossWorld;

	private String strNoMark;
	private String strTooFar;
	private String strOtherWorld;
	private String strRecallFailed;

	private MarkSpell markSpell;
	private final String markSpellName;

	public RecallSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		maxRange = getConfigDataDouble("max-range", 0);

		useBedLocation = getConfigDataBoolean("use-bed-location", false);
		allowCrossWorld = getConfigDataBoolean("allow-cross-world", true);

		strNoMark = getConfigString("str-no-mark", "You have no mark to recall to.");
		strTooFar = getConfigString("str-too-far", "You mark is too far away.");
		strOtherWorld = getConfigString("str-other-world", "Your mark is in another world.");
		strRecallFailed = getConfigString("str-recall-failed", "Could not recall.");
		markSpellName = getConfigString("mark-spell", "mark");
	}

	@Override
	public void initialize() {
		super.initialize();

		Spell spell = MagicSpells.getSpellByInternalName(markSpellName);
		if (spell instanceof MarkSpell) markSpell = (MarkSpell) spell;
		else MagicSpells.error("RecallSpell '" + internalName + "' has an invalid mark-spell defined!");
	}

	@Override
	public CastResult cast(SpellData data) {
		Location markLocation = null;

		if (data.hasArgs() && data.args().length == 1 && data.caster().hasPermission("magicspells.advanced." + internalName)) {
			Player target = PlayerNameUtils.getPlayer(data.args()[0]);

			if (target != null) {
				if (useBedLocation.get(data)) markLocation = target.getBedSpawnLocation();
				else if (markSpell != null) markLocation = markSpell.getEffectiveMark(target);
			}
		} else markLocation = getRecallLocation(data.caster(), data);

		return recall(data, data.caster(), markLocation);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		return recall(data, data.target(), getRecallLocation(data.caster(), data));
	}

	private CastResult recall(SpellData data, LivingEntity entity, Location markLocation) {
		if (markLocation == null) {
			sendMessage(strNoMark, data.caster(), data);
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		Location from = entity.getLocation();

		if (!allowCrossWorld.get(data) && !entity.getWorld().equals(markLocation.getWorld())) {
			sendMessage(strOtherWorld, data.caster(), data);
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		double maxRange = this.maxRange.get(data);
		if (maxRange > 0 && markLocation.distanceSquared(from) > maxRange * maxRange) {
			sendMessage(strTooFar, data.caster(), data);
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		if (data.caster() instanceof Vehicle || !data.caster().isValid()) {
			MagicSpells.error("Recall teleport blocked for " + data.caster().getName());
			sendMessage(strRecallFailed, data.caster(), data);
			return new CastResult(PostCastAction.ALREADY_HANDLED, data);
		}

		entity.teleportAsync(markLocation);
		playSpellEffects(EffectPosition.CASTER, data.caster(), data);
		playSpellEffects(EffectPosition.TARGET, markLocation, data);
		playSpellEffects(EffectPosition.START_POSITION, from, data);
		playSpellEffects(EffectPosition.END_POSITION, markLocation, data);
		playSpellEffectsTrail(from, markLocation, data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private Location getRecallLocation(LivingEntity entity, SpellData data) {
		if (useBedLocation.get(data)) return entity instanceof Player player ? player.getBedSpawnLocation() : null;
		return markSpell == null ? null : markSpell.getEffectiveMark(entity);
	}

	public String getStrNoMark() {
		return strNoMark;
	}

	public void setStrNoMark(String strNoMark) {
		this.strNoMark = strNoMark;
	}

	public String getStrTooFar() {
		return strTooFar;
	}

	public void setStrTooFar(String strTooFar) {
		this.strTooFar = strTooFar;
	}

	public String getStrOtherWorld() {
		return strOtherWorld;
	}

	public void setStrOtherWorld(String strOtherWorld) {
		this.strOtherWorld = strOtherWorld;
	}

	public String getStrRecallFailed() {
		return strRecallFailed;
	}

	public void setStrRecallFailed(String strRecallFailed) {
		this.strRecallFailed = strRecallFailed;
	}

	public MarkSpell getMarkSpell() {
		return markSpell;
	}

	public void setMarkSpell(MarkSpell markSpell) {
		this.markSpell = markSpell;
	}

}
