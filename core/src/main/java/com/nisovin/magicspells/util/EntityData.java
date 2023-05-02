package com.nisovin.magicspells.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiConsumer;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.bukkit.util.Consumer;
import org.bukkit.util.EulerAngle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

public class EntityData {

	private final Multimap<EntityType, Transformer<?, ?>> options = MultimapBuilder.enumKeys(EntityType.class).arrayListValues().build();

	private ConfigData<EntityType> entityType;

	private final ConfigData<BlockData> fallingBlockData;
	private final ConfigData<Material> dropItemMaterial;
	private final ConfigData<Vector> relativeOffset;

	// Legacy support for DisguiseSpell section format

	// Ageable
	private final ConfigData<Boolean> baby;

	// AbstractHorse & Pig
	private final ConfigData<Boolean> saddled;

	// ChestedHorse
	private final ConfigData<Boolean> chested;

	// Creeper
	private final ConfigData<Boolean> powered;

	// Enderman
	private final ConfigData<BlockData> carriedBlockData;

	// Horse
	private final ConfigData<Horse.Color> horseColor;
	private final ConfigData<Horse.Style> horseStyle;

	// Llama
	private final ConfigData<Llama.Color> llamaColor;

	// Parrot
	private final ConfigData<Parrot.Variant> parrotVariant;

	// Puffer Fish & Slime
	private final ConfigData<Integer> size;

	// Sheep
	private final ConfigData<Boolean> sheared;

	// Sheep & Tropical Fish & Wolf
	private final ConfigData<DyeColor> color;

	// Tameable
	private final ConfigData<Boolean> tamed;

	// Tropical Fish
	private final ConfigData<TropicalFish.Pattern> tropicalFishPattern;
	private final ConfigData<DyeColor> tropicalFishPatternColor;

	// Villager
	private final ConfigData<Villager.Profession> profession;

	public EntityData(ConfigurationSection config) {
		entityType = ConfigDataUtil.getEnum(config, "entity", EntityType.class, null);

		relativeOffset = ConfigDataUtil.getVector(config, "relative-offset", new Vector(0, 0, 0));

		Multimap<Class<?>, Transformer<?, ?>> transformers = MultimapBuilder.linkedHashKeys().arrayListValues().build();

		// Ageable
		baby = addBoolean(transformers, config, "baby", false, Ageable.class, (ageable, baby) -> {
			if (baby) ageable.setBaby();
			else ageable.setAdult();
		});

		if (config.contains("age"))
			addInteger(transformers, config, "age", 0, Ageable.class, Ageable::setAge);

		// Tameable
		tamed = addBoolean(transformers, config, "tamed", false, Tameable.class, Tameable::setTamed);

		// AbstractHorse
		saddled = addBoolean(transformers, config, "saddled", false, AbstractHorse.class, (horse, saddled) -> {
			if (saddled) horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		});

		// Armor Stand
		addBoolean(transformers, config, "small", false, ArmorStand.class, ArmorStand::setSmall);
		addBoolean(transformers, config, "marker", false, ArmorStand.class, ArmorStand::setMarker);
		addBoolean(transformers, config, "visible", true, ArmorStand.class, ArmorStand::setVisible);
		addBoolean(transformers, config, "has-arms", true, ArmorStand.class, ArmorStand::setArms);
		addBoolean(transformers, config, "has-base-plate", true, ArmorStand.class, ArmorStand::setBasePlate);

		EulerAngle def = new EulerAngle(0, 0, 0);
		addEulerAngle(transformers, config, "head-angle", def, ArmorStand.class, ArmorStand::setHeadPose);
		addEulerAngle(transformers, config, "body-angle", def, ArmorStand.class, ArmorStand::setBodyPose);
		addEulerAngle(transformers, config, "left-arm-angle", def, ArmorStand.class, ArmorStand::setLeftArmPose);
		addEulerAngle(transformers, config, "right-arm-angle", def, ArmorStand.class, ArmorStand::setRightArmPose);
		addEulerAngle(transformers, config, "left-leg-angle", def, ArmorStand.class, ArmorStand::setLeftLegPose);
		addEulerAngle(transformers, config, "right-leg-angle", def, ArmorStand.class, ArmorStand::setRightLegPose);

		// Axolotl
		addEnum(transformers, config, "type", null, Axolotl.class, Axolotl.Variant.class, (axolotl, variant) -> {
			if (variant != null) axolotl.setVariant(variant);
		});

		// Cat
		addEnum(transformers, config, "type", null, Cat.class, Cat.Type.class, (cat, type) -> {
			if (type != null) cat.setCatType(type);
		});

		// ChestedHorse
		chested = addBoolean(transformers, config, "chested", false, ChestedHorse.class, ChestedHorse::setCarryingChest);

		// Creeper
		powered = addBoolean(transformers, config, "powered", false, Creeper.class, Creeper::setPowered);

		// Dropped Item
		dropItemMaterial = ConfigDataUtil.getMaterial(config, "material", null);

		// Enderman
		carriedBlockData = addBlockData(transformers, config, "material", null, Enderman.class, Enderman::setCarriedBlock);

		// Falling Block
		fallingBlockData = ConfigDataUtil.getBlockData(config, "material", null);

		// Fox
		addEnum(transformers, config, "type", null, Fox.class, Fox.Type.class, (fox, type) -> {
			if (type != null) fox.setFoxType(type);
		});

		// Frog
		addEnum(transformers, config, "type", null, Frog.class, Frog.Variant.class, (frog, variant) -> {
			if (variant != null) frog.setVariant(variant);
		});

		// Horse
		horseColor = addEnum(transformers, config, "color", null, Horse.class, Horse.Color.class, (horse, color) -> {
			if (color != null) horse.setColor(color);
		});
		horseStyle = addEnum(transformers, config, "style", null, Horse.class, Horse.Style.class, (horse, style) -> {
			if (style != null) horse.setStyle(style);
		});

		// Llama
		llamaColor = addEnum(transformers, config, "color", null, Llama.class, Llama.Color.class, (llama, color) -> {
			if (color != null) llama.setColor(color);
		});
		addMaterial(transformers, config, "material", null, Llama.class, (llama, material) -> {
			if (material != null) llama.getInventory().setDecor(new ItemStack(material));
		});

		// Mushroom Cow
		addEnum(transformers, config, "type", null, MushroomCow.class, MushroomCow.Variant.class, (mushroomCow, variant) -> {
			if (variant != null) mushroomCow.setVariant(variant);
		});

		// Panda
		addEnum(transformers, config, "main-gene", null, Panda.class, Panda.Gene.class, (panda, gene) -> {
			if (gene != null) panda.setMainGene(gene);
		});
		addEnum(transformers, config, "hidden-gene", null, Panda.class, Panda.Gene.class, (panda, gene) -> {
			if (gene != null) panda.setHiddenGene(gene);
		});

		// Parrot
		parrotVariant = addEnum(transformers, config, "type", null, Parrot.class, Parrot.Variant.class, (parrot, variant) -> {
			if (variant != null) parrot.setVariant(variant);
		});

		// Phantom
		addInteger(transformers, config, "size", 0, Phantom.class, Phantom::setSize);

		// Puffer Fish
		size = addInteger(transformers, config, "size", 0, PufferFish.class, PufferFish::setPuffState);

		// Rabbit
		addEnum(transformers, config, "type", null, Rabbit.class, Rabbit.Type.class, (rabbit, type) -> {
			if (type != null) rabbit.setRabbitType(type);
		});

		// Sheep
		sheared = addBoolean(transformers, config, "sheared", false, Sheep.class, Sheep::setSheared);
		color = addEnum(transformers, config, "color", null, Sheep.class, DyeColor.class, (sheep, color) -> {
			if (color != null) sheep.setColor(color);
		});

		// Slime
		addInteger(transformers, config, "size", 0, Slime.class, Slime::setSize);

		// Steerable
		addBoolean(transformers, config, "saddled", false, Steerable.class, Steerable::setSaddle);

		// Tropical Fish
		addEnum(transformers, config, "color", null, TropicalFish.class, DyeColor.class, (tropicalFish, color) -> {
			if (color != null) tropicalFish.setBodyColor(color);
		});
		tropicalFishPatternColor = addEnum(transformers, config, "pattern-color", null, TropicalFish.class, DyeColor.class, (tropicalFish, color) -> {
			if (color != null) tropicalFish.setPatternColor(color);
		});
		tropicalFishPattern = addEnum(transformers, config, "type", null, TropicalFish.class, TropicalFish.Pattern.class, (tropicalFish, pattern) -> {
			if (pattern != null) tropicalFish.setPattern(pattern);
		});

		// Villager
		profession = addEnum(transformers, config, "type", null, Villager.class, Villager.Profession.class, (villager, profession) -> {
			if (profession != null) villager.setProfession(profession);
		});

		// Wolf
		addBoolean(transformers, config, "angry", false, Wolf.class, Wolf::setAngry);
		addEnum(transformers, config, "color", null, Wolf.class, DyeColor.class, (wolf, color) -> {
			if (color != null) wolf.setCollarColor(color);
		});

		if (Bukkit.getMinecraftVersion().contains("1.19.4")) {
			// Display shared

			addEnum(transformers, config, "billboard", null, Display.class, Display.Billboard.class, (display, billboard) -> {
				if (billboard != null) display.setBillboard(billboard);
			});

			//###setBRIGHTNESS
			//###setGLOW_COLOR_OVERRIDE
			//###setTRANSFORMATION

			addFloat(transformers, config, "display-height", 0, Display.class, Display::setDisplayHeight);
			addFloat(transformers, config, "display-width", 0, Display.class, Display::setDisplayWidth);

			addInteger(transformers, config, "interpolation-delay", 0, Display.class, Display::setInterpolationDelay);
			addInteger(transformers, config, "interpolation-duration", 0, Display.class, Display::setInterpolationDuration);

			addFloat(transformers, config, "shadow-radius", 0, Display.class, Display::setShadowRadius);
			addFloat(transformers, config, "shadow-strength", 0, Display.class, Display::setShadowStrength);

			addFloat(transformers, config, "view-range", 0, Display.class, Display::setViewRange);

			// Text Display
			addEnum(transformers, config, "alignment", null, TextDisplay.class, TextDisplay.TextAlignment.class, (display, alignment) -> {
				if (alignment != null) display.setAlignment(alignment);
			});
			//###setBackgroundColor
			addBoolean(transformers, config, "default-background", false, TextDisplay.class, TextDisplay::setDefaultBackground);
			addInteger(transformers, config, "line-width", 0, TextDisplay.class, TextDisplay::setLineWidth);
			addBoolean(transformers, config, "see-through", false, TextDisplay.class, TextDisplay::setSeeThrough);
			addBoolean(transformers, config, "shadowed", false, TextDisplay.class, TextDisplay::setShadowed);
			addByte(transformers, config, "text-opacity", (byte) 0, TextDisplay.class, TextDisplay::setTextOpacity);
			//###text

			// Item Display
			addEnum(transformers, config, "item-display-transform", null, ItemDisplay.class, ItemDisplay.ItemDisplayTransform.class, (display, transform) -> {
				if (transform != null) display.setItemDisplayTransform(transform);
			});
			//###setItemstack

			// Block Display
			addBlockData(transformers, config, "block", null, BlockDisplay.class, BlockDisplay::setBlock);
		}

		for (EntityType entityType : EntityType.values()) {
			Class<? extends Entity> entityClass = entityType.getEntityClass();
			if (entityClass == null) continue;

			for (Class<?> transformerType : transformers.keys())
				if (transformerType.isAssignableFrom(entityClass))
					options.putAll(entityType, transformers.get(transformerType));
		}
	}

	@Nullable
	public Entity spawn(@NotNull Location location) {
		return spawn(location, null, null);
	}

	@Nullable
	public Entity spawn(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
		return spawn(location, null, consumer);
	}

	@Nullable
	public Entity spawn(@NotNull Location location, @Nullable SpellData data, @Nullable Consumer<Entity> consumer) {
		Location startLoc = location.clone();
		Vector dir = startLoc.getDirection().normalize();
		Vector relativeOffset = this.relativeOffset.get(data);

		Vector horizOffset = new Vector(-dir.getZ(), 0, dir.getX()).normalize();
		startLoc.add(horizOffset.multiply(relativeOffset.getZ())).getBlock().getLocation();
		startLoc.add(startLoc.getDirection().clone().multiply(relativeOffset.getX()));
		startLoc.setY(startLoc.getY() + relativeOffset.getY());

		EntityType entityType = this.entityType.get(data);
		if (entityType == null || (!entityType.isSpawnable() && entityType != EntityType.FALLING_BLOCK && entityType != EntityType.DROPPED_ITEM))
			return null;

		return switch (entityType) {
			case FALLING_BLOCK -> {
				BlockData blockData = fallingBlockData.get(data);
				if (blockData == null) yield null;

				Entity entity = startLoc.getWorld().spawnFallingBlock(startLoc, blockData);
				if (consumer != null) consumer.accept(entity);

				yield entity;
			}
			case DROPPED_ITEM -> {
				Material material = dropItemMaterial.get(data);
				if (material == null) yield null;

				Entity entity = startLoc.getWorld().dropItem(startLoc, new ItemStack(material));
				if (consumer != null) consumer.accept(entity);

				yield entity;
			}
			default -> {
				Class<? extends Entity> entityClass = entityType.getEntityClass();
				if (entityClass == null) yield null;

				yield startLoc.getWorld().spawn(startLoc, entityClass, e -> {
					Collection<Transformer<?, ?>> transformers = options.get(entityType);
					//noinspection rawtypes
					for (Transformer transformer : transformers)
						//noinspection unchecked
						transformer.apply(e, data);

					if (consumer != null) consumer.accept(e);
				});
			}
		};
	}

	private <T> ConfigData<Boolean> addBoolean(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, boolean def, Class<T> type, BiConsumer<T, Boolean> setter) {
		ConfigData<Boolean> supplier = ConfigDataUtil.getBoolean(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T> ConfigData<Integer> addInteger(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, int def, Class<T> type, BiConsumer<T, Integer> setter) {
		ConfigData<Integer> supplier = ConfigDataUtil.getInteger(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T> ConfigData<Float> addFloat(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, float def, Class<T> type, BiConsumer<T, Float> setter) {
		ConfigData<Float> supplier = ConfigDataUtil.getFloat(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T> ConfigData<Byte> addByte(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, byte def, Class<T> type, BiConsumer<T, Byte> setter) {
		ConfigData<Byte> supplier = ConfigDataUtil.getByte(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T> ConfigData<Material> addMaterial(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Material def, Class<T> type, BiConsumer<T, Material> setter) {
		ConfigData<Material> supplier = ConfigDataUtil.getMaterial(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T> ConfigData<BlockData> addBlockData(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, BlockData def, Class<T> type, BiConsumer<T, BlockData> setter) {
		ConfigData<BlockData> supplier = ConfigDataUtil.getBlockData(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T, E extends Enum<E>> ConfigData<E> addEnum(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, E def, Class<T> type, Class<E> enumType, BiConsumer<T, E> setter) {
		ConfigData<E> supplier = ConfigDataUtil.getEnum(config, name, enumType, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T> ConfigData<EulerAngle> addEulerAngle(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, EulerAngle def, Class<T> type, BiConsumer<T, EulerAngle> setter) {
		ConfigData<EulerAngle> supplier = ConfigDataUtil.getEulerAngle(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	public Multimap<EntityType, Transformer<?, ?>> getOptions() {
		return options;
	}

	public ConfigData<EntityType> getEntityType() {
		return entityType;
	}

	public void setEntityType(ConfigData<EntityType> entityType) {
		this.entityType = entityType;
	}

	public ConfigData<Material> getDroppedItemStack() {
		return dropItemMaterial;
	}

	public ConfigData<BlockData> getFallingBlockData() {
		return fallingBlockData;
	}

	@Deprecated
	public ConfigData<Boolean> getBaby() {
		return baby;
	}

	@Deprecated
	public ConfigData<Boolean> getChested() {
		return chested;
	}

	@Deprecated
	public ConfigData<Boolean> getPowered() {
		return powered;
	}

	@Deprecated
	public ConfigData<BlockData> getCarriedBlockData() {
		return carriedBlockData;
	}

	@Deprecated
	public ConfigData<Horse.Color> getHorseColor() {
		return horseColor;
	}

	@Deprecated
	public ConfigData<Horse.Style> getHorseStyle() {
		return horseStyle;
	}

	@Deprecated
	public ConfigData<Boolean> getSaddled() {
		return saddled;
	}

	@Deprecated
	public ConfigData<Llama.Color> getLlamaColor() {
		return llamaColor;
	}

	@Deprecated
	public ConfigData<Parrot.Variant> getParrotVariant() {
		return parrotVariant;
	}

	@Deprecated
	public ConfigData<Integer> getSize() {
		return size;
	}

	@Deprecated
	public ConfigData<Boolean> getSheared() {
		return sheared;
	}

	@Deprecated
	public ConfigData<DyeColor> getColor() {
		return color;
	}

	@Deprecated
	public ConfigData<Boolean> getTamed() {
		return tamed;
	}

	@Deprecated
	public ConfigData<TropicalFish.Pattern> getTropicalFishPattern() {
		return tropicalFishPattern;
	}

	@Deprecated
	public ConfigData<DyeColor> getTropicalFishPatternColor() {
		return tropicalFishPatternColor;
	}

	@Deprecated
	public ConfigData<Villager.Profession> getProfession() {
		return profession;
	}

	public record Transformer<T, C>(ConfigData<C> supplier, BiConsumer<T, C> setter) {

		public void apply(T entity, SpellData data) {
			setter.accept(entity, supplier.get(data));
		}

	}

}