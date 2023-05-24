package com.nisovin.magicspells.spells.targeted;

import java.util.*;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.block.BlockFace;
import org.bukkit.event.HandlerList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.ai.LookAtEntityGoal;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.util.managers.AttributeManager;
import com.nisovin.magicspells.spells.TargetedEntityFromLocationSpell;

public class SpawnEntitySpell extends TargetedSpell implements TargetedLocationSpell, TargetedEntityFromLocationSpell {

	private final List<Entity> entities;

	private EntityData entityData;

	private ItemStack mainHandItem;
	private ItemStack offHandItem;
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;

	private final ConfigData<Float> mainHandItemDropChance;
	private final ConfigData<Float> offHandItemDropChance;
	private final ConfigData<Float> helmetDropChance;
	private final ConfigData<Float> chestplateDropChance;
	private final ConfigData<Float> leggingsDropChance;
	private final ConfigData<Float> bootsDropChance;
	private final ConfigData<Float> yOffset;

	private final ConfigData<Integer> duration;
	private final ConfigData<Integer> fireTicks;
	private final ConfigData<Integer> targetInterval;

	private ConfigData<Double> targetRange;
	private ConfigData<Double> retargetRange;

	private final String location;
	private final String nameplateText;

	private final boolean noAI;
	private final boolean gravity;
	private final boolean removeAI;
	private final boolean setOwner;
	private final boolean removeMob;
	private final boolean cancelAttack;
	private final boolean invulnerable;
	private final boolean useCasterName;
	private final boolean centerLocation;
	private final boolean addLookAtPlayerAI;
	private final boolean allowSpawnInMidair;

	private Subspell attackSpell;
	private String attackSpellName;

	private Subspell spellOnSpawn;
	private String spellOnSpawnName;

	private List<PotionEffect> potionEffects;
	private Set<AttributeManager.AttributeInfo> attributes;

	// DEBUG INFO: level 2, invalid potion effect on internalName spell data
	public SpawnEntitySpell(MagicConfig config, String spellName) {
		super(config, spellName);

		entities = new ArrayList<>();

		ConfigurationSection entitySection = getConfigSection("entity");
		if (entitySection != null) entityData = new EntityData(entitySection);

		// Equipment
		MagicItem magicMainHandItem = MagicItems.getMagicItemFromString(getConfigString("main-hand", ""));
		if (magicMainHandItem != null) {
			mainHandItem = magicMainHandItem.getItemStack();
			if (mainHandItem != null && mainHandItem.getType().isAir()) mainHandItem = null;
		}

		MagicItem magicOffHandItem = MagicItems.getMagicItemFromString(getConfigString("off-hand", ""));
		if (magicOffHandItem != null) {
			offHandItem = magicOffHandItem.getItemStack();
			if (offHandItem != null && offHandItem.getType().isAir()) offHandItem = null;
		}

		MagicItem magicHelmetItem = MagicItems.getMagicItemFromString(getConfigString("helmet", ""));
		if (magicHelmetItem != null) {
			helmet = magicHelmetItem.getItemStack();
			if (helmet != null && helmet.getType().isAir()) helmet = null;
		}

		MagicItem magicChestplateItem = MagicItems.getMagicItemFromString(getConfigString("chestplate", ""));
		if (magicChestplateItem != null) {
			chestplate = magicChestplateItem.getItemStack();
			if (chestplate != null && chestplate.getType().isAir()) chestplate = null;
		}

		MagicItem magicLeggingsItem = MagicItems.getMagicItemFromString(getConfigString("leggings", ""));
		if (magicLeggingsItem != null) {
			leggings = magicLeggingsItem.getItemStack();
			if (leggings != null && leggings.getType().isAir()) leggings = null;
		}

		MagicItem magicBootsItem = MagicItems.getMagicItemFromString(getConfigString("boots", ""));
		if (magicBootsItem != null) {
			boots = magicBootsItem.getItemStack();
			if (boots != null && boots.getType().isAir()) boots = null;
		}

		if (mainHandItem != null) mainHandItem.setAmount(1);
		if (offHandItem != null) offHandItem.setAmount(1);
		if (helmet != null) helmet.setAmount(1);
		if (chestplate != null) chestplate.setAmount(1);
		if (leggings != null) leggings.setAmount(1);
		if (boots != null) boots.setAmount(1);

		mainHandItemDropChance = getConfigDataFloat("main-hand-drop-chance", 0);
		offHandItemDropChance = getConfigDataFloat("off-hand-drop-chance", 0);
		helmetDropChance = getConfigDataFloat("helmet-drop-chance", 0);
		chestplateDropChance = getConfigDataFloat("chestplate-drop-chance", 0);
		leggingsDropChance = getConfigDataFloat("leggings-drop-chance", 0);
		bootsDropChance = getConfigDataFloat("boots-drop-chance", 0);
		yOffset = getConfigDataFloat("y-offset", 0.1F);

		duration = getConfigDataInt("duration", 0);
		fireTicks = getConfigDataInt("fire-ticks", 0);
		targetInterval = getConfigDataInt("target-interval", -1);

		targetRange = getConfigDataDouble("target-range", 20);
		retargetRange = getConfigDataDouble("retarget-range", 50);

		location = getConfigString("location", "target");
		nameplateText = getConfigString("nameplate-text", null);

		noAI = getConfigBoolean("no-ai", false);
		gravity = getConfigBoolean("gravity", true);
		setOwner = getConfigBoolean("set-owner", true);
		removeAI = getConfigBoolean("remove-ai", false);
		removeMob = getConfigBoolean("remove-mob", true);
		invulnerable = getConfigBoolean("invulnerable", false);
		useCasterName = getConfigBoolean("use-caster-name", false);
		centerLocation = getConfigBoolean("center-location", false);
		addLookAtPlayerAI = getConfigBoolean("add-look-at-player-ai", false);
		allowSpawnInMidair = getConfigBoolean("allow-spawn-in-midair", false);
		cancelAttack = getConfigBoolean("cancel-attack", true);

		attackSpellName = getConfigString("attack-spell", "");
		spellOnSpawnName = getConfigString("spell-on-spawn", null);

		// Attributes
		// - [AttributeName] [Number] [Operation]
		List<String> attributeList = getConfigStringList("attributes", null);
		if (attributeList != null && !attributeList.isEmpty()) attributes = MagicSpells.getAttributeManager().getAttributes(attributeList);

		List<String> list = getConfigStringList("potion-effects", null);
		if (list != null && !list.isEmpty()) {
			potionEffects = new ArrayList<>();

			String[] split;
			PotionEffectType type;
			int duration;
			int strength;
			boolean ambient;
			for (String data : list) {
				split = data.split(" ");
				try {
					type = Util.getPotionEffectType(split[0]);
					if (type == null) throw new Exception("");

					duration = 600;
					if (split.length > 1) duration = Integer.parseInt(split[1]);

					strength = 0;
					if (split.length > 2) strength = Integer.parseInt(split[2]);

					ambient = split.length > 3 && split[3].equalsIgnoreCase("ambient");
					potionEffects.add(new PotionEffect(type, duration, strength, ambient));
				} catch (Exception e) {
					MagicSpells.error("SpawnMonsterSpell '" + spellName + "' has an invalid potion effect defined: " + data);
				}
			}
		}
	}

	@Override
	public void initialize() {
		super.initialize();

		if (entityData == null || entityData.getEntityType() == null) {
			MagicSpells.error("SpawnEntitySpell '" + internalName + "' has an invalid entity defined!");
			entityData = null;
		}

		if (spellOnSpawnName != null) {
			spellOnSpawn = new Subspell(spellOnSpawnName);

			if (!spellOnSpawn.process()) {
				MagicSpells.error("SpawnEntitySpell '" + internalName + "' has an invalid spell-on-spawn '" + spellOnSpawnName + "' defined!");
				spellOnSpawn = null;
			}

			spellOnSpawnName = null;
		}

		attackSpell = new Subspell(attackSpellName);
		if (!attackSpellName.isEmpty() && !attackSpell.process()) {
			MagicSpells.error("SpawnEntitySpell '" + internalName + "' has an invalid attack-spell defined!");
			attackSpell = null;
		}
		attackSpellName = null;
	}

	@Override
	public void turnOff() {
		Iterator<Entity> it = entities.iterator();
		while (it.hasNext()) {
			Entity entity = it.next();

			it.remove();
			entity.remove();
		}
	}

	@Override
	public PostCastAction castSpell(LivingEntity caster, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Location loc = null;
			LivingEntity target = null;

			switch (location.toLowerCase()) {
				case "focus" -> {
					loc = getRandomLocationFrom(caster.getLocation(), 3);
					TargetInfo<LivingEntity> info = getTargetedEntity(caster, power, args);
					if (info.noTarget()) return noTarget(caster, args, info);
					target = info.target();
					power = info.power();
				}
				case "target" -> {
					Block block = getTargetedBlock(caster, power, args);
					if (block != null && !block.getType().isAir()) {
						if (BlockUtils.isPathable(block)) loc = block.getLocation();
						else if (BlockUtils.isPathable(block.getRelative(BlockFace.UP))) loc = block.getRelative(BlockFace.UP).getLocation();
					}
				}
				case "caster" -> loc = caster.getLocation();
				case "random" -> loc = getRandomLocationFrom(caster.getLocation(), getRange(power));
				case "casteroffset" -> {
					String[] split = location.split(":");
					float y = Float.parseFloat(split[1]);
					loc = caster.getLocation().add(0, y, 0);
					loc.setPitch(0);
				}
			}

			if (loc == null || !spawnMob(caster, caster.getLocation(), loc, target, power, args))
				return noTarget(caster, args);
		}

		return PostCastAction.HANDLE_NORMALLY;
	}

	@Override
	public boolean castAtLocation(LivingEntity caster, Location target, float power, String[] args) {
		return switch (location.toLowerCase()) {
			case "target" -> spawnMob(caster, caster.getLocation(), target, null, power, args);
			case "caster" -> spawnMob(caster, caster.getLocation(), caster.getLocation(), null, power, args);
			case "random" -> {
				Location loc = getRandomLocationFrom(target, getRange(power));
				yield loc != null && spawnMob(caster, caster.getLocation(), loc, null, power, args);
			}
			case "offset" -> {
				String[] split = location.split(":");
				float y = Float.parseFloat(split[1]);
				Location loc = target.clone().add(0, y, 0);
				loc.setPitch(0);

				yield spawnMob(caster, caster.getLocation(), loc, null, power, args);
			}
			default -> false;
		};
	}

	@Override
	public boolean castAtLocation(LivingEntity caster, Location target, float power) {
		return castAtLocation(caster, target, power, null);
	}

	@Override
	public boolean castAtLocation(Location target, float power, String[] args) {
		return switch (location.toLowerCase()) {
			case "target", "caster" -> spawnMob(null, target, target, null, power, args);
			case "random" -> {
				Location loc = getRandomLocationFrom(target, getRange(power));
				yield loc != null && spawnMob(null, target, loc, null, power, args);
			}
			case "offset" -> {
				String[] split = location.split(":");
				float y = Float.parseFloat(split[1]);
				Location loc = target.clone().add(0, y, 0);
				loc.setPitch(0);

				yield spawnMob(null, target, loc, null, power, args);
			}
			default -> false;
		};
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		return castAtLocation(target, power, null);
	}

	@Override
	public boolean castAtEntityFromLocation(LivingEntity caster, Location from, LivingEntity target, float power, String[] args) {
		if (!validTargetList.canTarget(caster, target)) return false;
		if (location.equals("focus")) spawnMob(caster, from, from, target, power, args);
		else castAtLocation(caster, from, power, args);
		return true;
	}

	@Override
	public boolean castAtEntityFromLocation(LivingEntity caster, Location from, LivingEntity target, float power) {
		return castAtEntityFromLocation(caster, from, target, power, null);
	}

	@Override
	public boolean castAtEntityFromLocation(Location from, LivingEntity target, float power, String[] args) {
		if (!validTargetList.canTarget(target)) return false;
		if (location.equals("focus")) spawnMob(null, from, from, target, power, args);
		else castAtLocation(from, power, args);
		return true;
	}

	@Override
	public boolean castAtEntityFromLocation(Location from, LivingEntity target, float power) {
		return castAtEntityFromLocation(from, target, power, null);
	}

	private Location getRandomLocationFrom(Location location, int range) {
		World world = location.getWorld();
		int x;
		int y;
		int z;
		int attempts = 0;
		Block block;
		Block block2;

		while (attempts < 10) {
			x = location.getBlockX() + random.nextInt(range << 1) - range;
			y = location.getBlockY() + 2;
			z = location.getBlockZ() + random.nextInt(range << 1) - range;

			block = world.getBlockAt(x, y, z);
			if (block.getType() == Material.WATER) return block.getLocation();
			if (BlockUtils.isPathable(block)) {
				if (allowSpawnInMidair) return block.getLocation();
				int c = 0;
				while (c < 5) {
					block2 = block.getRelative(BlockFace.DOWN);
					if (BlockUtils.isPathable(block2)) block = block2;
					else return block.getLocation();
					c++;
				}
			}

			attempts++;
		}
		return null;
	}

	private boolean spawnMob(LivingEntity caster, Location source, Location loc, LivingEntity target, float power, String[] args) {
		if (entityData == null) return false;
		SpellData data = new SpellData(caster, target, power, args);

		loc.add(0, yOffset.get(data), 0);
		if (centerLocation) loc = loc.toCenterLocation();

		Entity entity = entityData.spawn(loc, data, mob -> prepMob(caster, target, mob, power, args));

		if (entity == null) return false;

		entities.add(entity);

		int duration = this.duration.get(data);

		if (duration > 0) {
			MagicSpells.scheduleDelayedTask(() -> {
				entity.remove();
				entities.remove(entity);
			}, duration);
		}

		if (spellOnSpawn != null) {
			if (entity instanceof LivingEntity livingEntity) spellOnSpawn.subcast(caster, livingEntity, power);
			else spellOnSpawn.subcast(caster, entity.getLocation(), power);
		}

		int targetInterval = this.targetInterval.get(data);
		if (targetInterval > 0 && entity instanceof Mob mob) new Targeter(caster, mob, power, args, targetInterval);

		if (attackSpell != null && entity instanceof LivingEntity livingEntity) {
			AttackMonitor monitor = new AttackMonitor(caster, livingEntity, target, power, args);
			MagicSpells.registerEvents(monitor);

			if (duration > 0) MagicSpells.scheduleDelayedTask(() -> HandlerList.unregisterAll(monitor), duration);
		}

		if (caster != null) playSpellEffects(caster, source, entity, data);
		else playSpellEffects(source, entity, data);

		return true;
	}

	private void prepMob(LivingEntity caster, LivingEntity target, Entity entity, float power, String[] args) {
		SpellData data = new SpellData(caster, target, power, args);

		entity.setGravity(gravity);
		entity.setInvulnerable(invulnerable);

		int fireTicks = this.fireTicks.get(data);
		if (fireTicks > 0) entity.setFireTicks(fireTicks);

		if (useCasterName && caster != null) {
			if (caster instanceof Player player) entity.customName(player.displayName());
			else entity.customName(caster.name());
			entity.setCustomNameVisible(true);
		} else if (nameplateText != null) {
			entity.customName(Util.getMiniMessage(MagicSpells.doReplacements(nameplateText, caster, target, args)));
			entity.setCustomNameVisible(true);
		}

		if (setOwner && entity instanceof Tameable tameable && tameable.isTamed() && caster instanceof AnimalTamer tamer)
			tameable.setOwner(tamer);

		if (entity instanceof Enderman enderman && mainHandItem != null) {
			ItemMeta meta = mainHandItem.getItemMeta();

			if (meta instanceof BlockDataMeta blockMeta)
				enderman.setCarriedBlock(blockMeta.getBlockData(mainHandItem.getType()));
		}

		if (entity instanceof LivingEntity livingEntity) {
			EntityEquipment equipment = livingEntity.getEquipment();
			if (equipment != null) {
				equipment.setItemInMainHand(mainHandItem);
				equipment.setItemInOffHand(offHandItem);
				equipment.setHelmet(helmet);
				equipment.setChestplate(chestplate);
				equipment.setLeggings(leggings);
				equipment.setBoots(boots);

				equipment.setItemInMainHandDropChance(mainHandItemDropChance.get(data) / 100);
				equipment.setItemInOffHandDropChance(offHandItemDropChance.get(data) / 100);
				equipment.setHelmetDropChance(helmetDropChance.get(data) / 100);
				equipment.setChestplateDropChance(chestplateDropChance.get(data) / 100);
				equipment.setLeggingsDropChance(leggingsDropChance.get(data) / 100);
				equipment.setBootsDropChance(bootsDropChance.get(data) / 100);
			}

			if (potionEffects != null) livingEntity.addPotionEffects(potionEffects);
			if (attributes != null) MagicSpells.getAttributeManager().addEntityAttributes(livingEntity, attributes);

			if (removeAI) {
				if (addLookAtPlayerAI && livingEntity instanceof Mob mob) {
					MobGoals mobGoals = Bukkit.getMobGoals();

					mobGoals.removeAllGoals(mob);
					mobGoals.addGoal(mob, 1, new LookAtEntityGoal(mob, HumanEntity.class, 10F, 1F));
				} else livingEntity.setAI(false);
			}

			if (noAI) livingEntity.setAI(false);
			if (livingEntity instanceof Mob mob) mob.setTarget(target);
		}
	}

	@EventHandler
	private void onEntityRemove(EntityRemoveFromWorldEvent event) {
		if (removeMob) entities.remove(event.getEntity());
	}

	private class AttackMonitor implements Listener {

		private final LivingEntity caster;
		private final LivingEntity monster;
		private final String[] args;
		private final float power;

		private LivingEntity target;

		private AttackMonitor(LivingEntity caster, LivingEntity monster, LivingEntity target, float power, String[] args) {
			this.caster = caster;
			this.monster = monster;
			this.target = target;
			this.power = power;
			this.args = args;
		}

		@EventHandler(ignoreCancelled = true)
		private void onDamage(EntityDamageByEntityEvent event) {
			Entity damager = event.getDamager();
			if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Entity entity)
				damager = entity;

			if (damager != monster) return;

			if (event.getEntity() instanceof LivingEntity damaged) {
				attackSpell.subcast(caster, monster.getLocation(), damaged, power);
				event.setCancelled(cancelAttack);
			}
		}

		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		private void onTarget(EntityTargetEvent event) {
			if (event.getEntity() != monster) return;

			Entity newTarget = event.getTarget();
			if (!validTargetList.canTarget(caster, newTarget)) {
				if (target == null) retarget(null);
				event.setTarget(target);
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		private void onDeath(EntityDeathEvent event) {
			LivingEntity entity = event.getEntity();
			if (entity == monster) {
				HandlerList.unregisterAll(this);
				return;
			}

			if (entity != target) return;

			retarget(entity);
			if (target != null) MobUtil.setTarget(monster, target);
		}

		@EventHandler(ignoreCancelled = true)
		public void onRemove(EntityRemoveFromWorldEvent event) {
			Entity entity = event.getEntity();
			if (entity == monster) {
				HandlerList.unregisterAll(this);
				return;
			}

			if (entity != target) return;

			retarget(target);
			if (target != null) MobUtil.setTarget(monster, target);
		}

		private void retarget(LivingEntity ignore) {
			double range = retargetRange.get(caster, null, power, args);
			double rangeSquared = range * range;
			double distanceSquared;

			for (LivingEntity entity : monster.getLocation().getNearbyLivingEntities(range)) {
				if (!entity.isValid()) continue;
				if (entity.equals(ignore)) continue;
				if (!validTargetList.canTarget(caster, entity)) continue;

				distanceSquared = monster.getLocation().distanceSquared(entity.getLocation());
				if (distanceSquared > rangeSquared) continue;

				target = entity;
				break;
			}
		}

	}

	private class Targeter implements Runnable {

		private final Mob mob;

		private final LivingEntity caster;
		private final String[] args;
		private final float power;
		private final int taskId;

		private Targeter(LivingEntity caster, Mob mob, float power, String[] args, int interval) {
			this.caster = caster;
			this.power = power;
			this.args = args;
			this.mob = mob;

			this.taskId = MagicSpells.scheduleRepeatingTask(this, 1, interval);
		}

		@Override
		public void run() {
			if (!mob.isValid()) {
				MagicSpells.cancelTask(taskId);
				return;
			}

			double range = targetRange.get(caster, null, power, args);

			List<LivingEntity> targets = new ArrayList<>(mob.getLocation().getNearbyLivingEntities(range));
			Iterator<LivingEntity> iterator = targets.iterator();
			while (iterator.hasNext()) {
				if (validTargetList.canTarget(caster, iterator.next())) continue;
				iterator.remove();
			}

			if (targets.isEmpty()) return;

			LivingEntity target = targets.get(random.nextInt(targets.size()));
			mob.setTarget(target);
		}

	}

}
