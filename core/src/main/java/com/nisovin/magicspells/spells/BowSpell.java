package com.nisovin.magicspells.spells;

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.math3.util.FastMath;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.ValidTargetList;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellTargetEvent;
import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;

public class BowSpell extends Spell {

	private static final String METADATA_KEY = "MSBowSpell";

	private List<String> bowNames;
	private List<String> disallowedBowNames;

	private ValidTargetList triggerList;

	private String bowName;
	private String spellOnShootName;
	private String spellOnHitEntityName;
	private String spellOnHitGroundName;

	private Subspell spellOnShoot;
	private Subspell spellOnHitEntity;
	private Subspell spellOnHitGround;

	private boolean cancelShot;
	private boolean useBowForce;
	private boolean removeArrow;
	private boolean cancelShotOnFail;

	private float minimumForce;
	private float maximumForce;

	public BowSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		List<String> names = getConfigStringList("bow-names", null);
		if (names != null) {
			bowNames = new ArrayList<>();
			names.forEach(str -> bowNames.add(Util.colorize(str)));
		} else bowName = Util.colorize(getConfigString("bow-name", ""));

		List<String> disallowedNames = getConfigStringList("disallowed-bow-names", null);
		if (disallowedNames != null) {
			disallowedBowNames = new ArrayList<>();
			disallowedNames.forEach(str -> disallowedBowNames.add(Util.colorize(str)));
		}

		if (config.isList("spells." + internalName + ".can-trigger")) {
			List<String> targets = getConfigStringList("can-trigger", new ArrayList<>());
			if (targets.isEmpty()) targets.add("players");
			triggerList = new ValidTargetList(this, targets);
		} else triggerList = new ValidTargetList(this, getConfigString("can-trigger", "players"));

		spellOnShootName = getConfigString("spell", "");
		spellOnHitEntityName = getConfigString("spell-on-hit-entity", "");
		spellOnHitGroundName = getConfigString("spell-on-hit-ground", "");

		cancelShot = getConfigBoolean("cancel-shot", true);
		useBowForce = getConfigBoolean("use-bow-force", true);
		removeArrow = getConfigBoolean("remove-arrow", false);
		cancelShotOnFail = getConfigBoolean("cancel-shot-on-fail", true);

		minimumForce = getConfigFloat("minimum-force", 0F);
		maximumForce = getConfigFloat("maximum-force", 0F);

		if (minimumForce < 0F) minimumForce = 0F;
		else if (minimumForce > 1F) minimumForce = 1F;
		if (maximumForce < 0F) maximumForce = 0F;
		else if (maximumForce > 1F) maximumForce = 1F;
	}

	@Override
	public void initialize() {
		super.initialize();

		spellOnShoot = initSubspell(spellOnShootName, "BowSpell '" + internalName + "' has an invalid spell defined!");
		spellOnHitEntity = initSubspell(spellOnHitEntityName, "BowSpell '" + internalName + "' has an invalid spell-on-hit-entity defined!");
		spellOnHitGround = initSubspell(spellOnHitGroundName, "BowSpell '" + internalName + "' has an invalid spell-on-hit-ground defined!");
	}

	@Override
	public void turnOff() {
		super.turnOff();
	}

	@Override
	public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
		return PostCastAction.ALREADY_HANDLED;
	}

	@Override
	public boolean canCastWithItem() {
		return false;
	}

	@Override
	public boolean canCastByCommand() {
		return false;
	}

	@EventHandler
	public void onArrowLaunch(EntityShootBowEvent event) {
		if (!cancelShot && event.isCancelled()) return;
		if (!(event.getProjectile() instanceof Arrow)) return;

		LivingEntity caster = event.getEntity();
		if (!triggerList.canTarget(caster, true)) return;

		if (caster instanceof Player) {
			Spellbook spellbook = MagicSpells.getSpellbook((Player) caster);
			if (!spellbook.hasSpell(this)) return;
			if (!spellbook.canCast(this)) return;
		}

		ItemStack inHand = event.getBow();
		if (inHand == null || inHand.getType() != Material.BOW) return;

		String name = inHand.getItemMeta().getDisplayName();
		if (bowNames != null && !bowNames.contains(name)) return;
		if (disallowedBowNames != null && disallowedBowNames.contains(name)) return;
		if (bowName != null && !bowName.isEmpty() && !bowName.equals(name)) return;

		float force = (float) (FastMath.floor(event.getForce() * 100F) / 100F);
		if (minimumForce != 0 && force < minimumForce) return;
		if (maximumForce != 0 && force > maximumForce) return;

		SpellCastEvent castEvent = preCast(caster, 1f, null);
		if (castEvent == null) {
			if (cancelShotOnFail) event.setCancelled(true);
			return;
		}

		if (castEvent.getSpellCastState() == SpellCastState.NORMAL) {
			if (cancelShot) event.setCancelled(true);
			if (!event.isCancelled()) {
				Entity projectile = event.getProjectile();

				ArrowData arrowData = new ArrowData(castEvent.getPower(), spellOnHitEntity, spellOnHitGround, this);
				List<ArrowData> arrowDataList = null;
				if (projectile.hasMetadata(METADATA_KEY)) {
					List<MetadataValue> metas = projectile.getMetadata(METADATA_KEY);
					for (MetadataValue meta : metas) {
						if (!MagicSpells.plugin.equals(meta.getOwningPlugin())) continue;

						arrowDataList = (List<ArrowData>) meta.value();
						if (arrowDataList != null) arrowDataList.add(arrowData);
						break;
					}
				}

				if (arrowDataList == null) {
					arrowDataList = new ArrayList<>();
					arrowDataList.add(arrowData);

					projectile.setMetadata(METADATA_KEY, new FixedMetadataValue(MagicSpells.plugin, arrowDataList));
				}

				playSpellEffects(EffectPosition.PROJECTILE, projectile);
				playTrackingLinePatterns(EffectPosition.DYNAMIC_CASTER_PROJECTILE_LINE, caster.getLocation(), projectile.getLocation(), caster, projectile);
			}

			if (spellOnShoot != null) spellOnShoot.cast(caster, castEvent.getPower());
		} else if (cancelShotOnFail) event.setCancelled(true);

		postCast(castEvent, PostCastAction.HANDLE_NORMALLY);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onArrowHitGround(ProjectileHitEvent event) {
		if (event.getHitBlock() == null) return;

		Projectile proj = event.getEntity();
		if (!proj.hasMetadata(METADATA_KEY)) return;

		List<MetadataValue> metas = proj.getMetadata(METADATA_KEY);
		for (MetadataValue meta : metas) {
			if (!MagicSpells.plugin.equals(meta.getOwningPlugin())) continue;

			ProjectileSource shooter = proj.getShooter();
			if (!(shooter instanceof LivingEntity)) break;
			LivingEntity caster = (LivingEntity) shooter;

			List<ArrowData> arrowDataList = (List<ArrowData>) meta.value();
			if (arrowDataList == null || arrowDataList.isEmpty()) break;

			for (ArrowData data : arrowDataList) {
				if (data.groundSpell == null) continue;

				SpellTargetLocationEvent targetLocationEvent = new SpellTargetLocationEvent(data.spell, caster, proj.getLocation(), data.power);
				EventUtil.call(targetLocationEvent);
				if (targetLocationEvent.isCancelled()) {
					break;
				}

				if (data.groundSpell.isTargetedLocationSpell())
					data.groundSpell.castAtLocation(caster, targetLocationEvent.getTargetLocation(), targetLocationEvent.getPower());
				else data.groundSpell.cast(caster, targetLocationEvent.getPower());
			}

			break;
		}

		proj.removeMetadata(METADATA_KEY, MagicSpells.plugin);
		if (removeArrow) proj.remove();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onArrowHitEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (!(damager instanceof Arrow)) return;
		if (!damager.hasMetadata(METADATA_KEY)) return;

		List<MetadataValue> metas = damager.getMetadata(METADATA_KEY);
		for (MetadataValue meta : metas) {
			if (!MagicSpells.plugin.equals(meta.getOwningPlugin())) continue;

			Entity damaged = event.getEntity();
			if (!(damaged instanceof LivingEntity)) break;
			LivingEntity target = (LivingEntity) damaged;

			ProjectileSource shooter = ((Arrow) damager).getShooter();
			if (!(shooter instanceof LivingEntity)) break;
			LivingEntity caster = (LivingEntity) shooter;

			List<ArrowData> arrowDataList = (List<ArrowData>) meta.value();
			if (arrowDataList == null || arrowDataList.isEmpty()) break;

			for (ArrowData data : arrowDataList) {
				if (data.entitySpell == null) continue;

				SpellTargetEvent targetEvent = new SpellTargetEvent(this, caster, target, data.power);
				EventUtil.call(targetEvent);
				if (targetEvent.isCancelled()) {
					continue;
				}
				target = targetEvent.getTarget();

				if (data.entitySpell.isTargetedEntityFromLocationSpell())
					data.entitySpell.castAtEntityFromLocation(caster, caster.getLocation(), target, targetEvent.getPower());
				else if (data.entitySpell.isTargetedLocationSpell())
					data.entitySpell.castAtLocation(caster, target.getLocation(), targetEvent.getPower());
				else if (data.entitySpell.isTargetedEntitySpell())
					data.entitySpell.castAtEntity(caster, target, targetEvent.getPower());
				else data.entitySpell.cast(caster, targetEvent.getPower());
			}

			break;
		}

		damager.removeMetadata(METADATA_KEY, MagicSpells.plugin);
		if (removeArrow) damager.remove();
	}

	private static class ArrowData {

		private final Subspell entitySpell;
		private final Subspell groundSpell;
		private final Spell spell;
		private final float power;

		ArrowData(float power, Subspell entitySpell, Subspell groundSpell, Spell spell) {
			this.power = power;
			this.entitySpell = entitySpell;
			this.groundSpell = groundSpell;
			this.spell = spell;
		}

	}

}
