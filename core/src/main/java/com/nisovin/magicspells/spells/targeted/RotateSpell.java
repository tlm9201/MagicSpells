package com.nisovin.magicspells.spells.targeted;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class RotateSpell extends TargetedSpell implements TargetedEntitySpell, TargetedLocationSpell {

	private static final float PITCH_BOUND = Math.nextUp(180f);

	private final ConfigData<Float> rotationYaw;
	private final ConfigData<Float> rotationPitch;

	private final ConfigData<Boolean> random;
	private final ConfigData<Boolean> affectPitch;
	private final ConfigData<Boolean> mimicDirection;

	private final ConfigData<String> face;

	public RotateSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		rotationYaw = getConfigDataFloat("rotation-yaw", 10);
		rotationPitch = getConfigDataFloat("rotation-pitch", 0);

		random = getConfigDataBoolean("random", false);
		affectPitch = getConfigDataBoolean("affect-pitch", false);
		mimicDirection = getConfigDataBoolean("mimic-direction", false);

		face = getConfigDataString("face", "");
	}

	@Override
	public CastResult cast(SpellData data) {
		TargetInfo<LivingEntity> info = getTargetedEntity(data);
		if (info.noTarget()) return noTarget(info);

		return castAtEntity(info.spellData());
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		changeDirection(data.caster(), data.location(), false, data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		String face = this.face.get(data);
		if (face.isEmpty()) {
			Location location = data.target().getLocation();
			float pitch = location.getPitch(), yaw = location.getYaw();

			if (random.get(data)) {
				yaw = Spell.random.nextFloat(360);
				if (affectPitch.get(data)) pitch = Spell.random.nextFloat(PITCH_BOUND) - 90;
			} else {
				yaw += rotationYaw.get(data);
				if (affectPitch.get(data)) pitch += rotationPitch.get(data);
			}

			data.target().setRotation(yaw, pitch);
		}

		if (!data.hasCaster()) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		switch (face) {
			case "caster" -> changeDirection(data.target(), data.caster().getLocation(), false, data);
			case "target" -> changeDirection(data.caster(), data.target().getLocation(), false, data);
			case "away-from-caster" -> changeDirection(data.target(), data.caster().getLocation(), true, data);
			case "away-from-target" -> changeDirection(data.caster(), data.target().getLocation(), true, data);
			default -> {
				return new CastResult(PostCastAction.ALREADY_HANDLED, data);
			}
		}

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private void changeDirection(LivingEntity entity, Location target, boolean away, SpellData data) {
		if (mimicDirection.get(data)) {
			float yaw = target.getYaw();
			float pitch = affectPitch.get(data) ? target.getPitch() : entity.getLocation().getPitch();

			entity.setRotation(away ? yaw + 180 : yaw, pitch);
			return;
		}

		Location location = entity.getLocation();
		float yaw, pitch = location.getPitch();

		Vector direction = target.toVector().subtract(location.toVector());
		location.setDirection(direction);

		yaw = location.getYaw();
		if (affectPitch.get(data)) pitch = location.getPitch();

		entity.setRotation(away ? yaw + 180 : yaw, pitch);
	}

}
