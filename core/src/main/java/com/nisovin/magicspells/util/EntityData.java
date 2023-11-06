package com.nisovin.magicspells.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

import org.joml.Vector3f;
import org.joml.Quaternionf;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.kyori.adventure.text.Component;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.bukkit.util.EulerAngle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.util.config.FunctionData;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.config.ConfigDataUtil;
import com.nisovin.magicspells.util.magicitems.MagicItems;

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

		addOptBoolean(transformers, config, "glowing", Entity.class, Entity::setGlowing);
		addOptBoolean(transformers, config, "visible-by-default", Entity.class, Entity::setVisibleByDefault);

		// Ageable
		baby = addBoolean(transformers, config, "baby", false, Ageable.class, (ageable, baby) -> {
			if (baby) ageable.setBaby();
			else ageable.setAdult();
		});

		addOptInteger(transformers, config, "age", Ageable.class, Ageable::setAge);

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
		addOptEnum(transformers, config, "type", Axolotl.class, Axolotl.Variant.class, Axolotl::setVariant);

		// Cat
		addOptEnum(transformers, config, "type", Cat.class, Cat.Type.class, Cat::setCatType);

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
		addOptEnum(transformers, config, "type", Fox.class, Fox.Type.class, Fox::setFoxType);

		// Frog
		addOptEnum(transformers, config, "type", Frog.class, Frog.Variant.class, Frog::setVariant);

		// Horse
		horseColor = addOptEnum(transformers, config, "color", Horse.class, Horse.Color.class, Horse::setColor);
		horseStyle = addOptEnum(transformers, config, "style", Horse.class, Horse.Style.class, Horse::setStyle);

		// Llama
		llamaColor = addOptEnum(transformers, config, "color", Llama.class, Llama.Color.class, Llama::setColor);
		addOptMaterial(transformers, config, "material", Llama.class, (llama, material) -> llama.getInventory().setDecor(new ItemStack(material)));

		// Mushroom Cow
		addOptEnum(transformers, config, "type", MushroomCow.class, MushroomCow.Variant.class, MushroomCow::setVariant);

		// Panda
		addOptEnum(transformers, config, "main-gene", Panda.class, Panda.Gene.class, Panda::setMainGene);
		addOptEnum(transformers, config, "hidden-gene", Panda.class, Panda.Gene.class, Panda::setHiddenGene);

		// Parrot
		parrotVariant = addOptEnum(transformers, config, "type", Parrot.class, Parrot.Variant.class, Parrot::setVariant);

		// Phantom
		addInteger(transformers, config, "size", 0, Phantom.class, Phantom::setSize);

		// Puffer Fish
		size = addInteger(transformers, config, "size", 0, PufferFish.class, PufferFish::setPuffState);

		// Rabbit
		addOptEnum(transformers, config, "type", Rabbit.class, Rabbit.Type.class, Rabbit::setRabbitType);

		// Sheep
		sheared = addBoolean(transformers, config, "sheared", false, Sheep.class, Sheep::setSheared);
		color = addOptEnum(transformers, config, "color", Sheep.class, DyeColor.class, Sheep::setColor);

		// Shulker
		addOptEnum(transformers, config, "color", Shulker.class, DyeColor.class, Shulker::setColor);

		// Slime
		addInteger(transformers, config, "size", 0, Slime.class, Slime::setSize);

		// Steerable
		addBoolean(transformers, config, "saddled", false, Steerable.class, Steerable::setSaddle);

		// Tropical Fish
		addOptEnum(transformers, config, "color", TropicalFish.class, DyeColor.class, TropicalFish::setBodyColor);
		tropicalFishPatternColor = addOptEnum(transformers, config, "pattern-color", TropicalFish.class, DyeColor.class, TropicalFish::setPatternColor);
		tropicalFishPattern = addOptEnum(transformers, config, "type", TropicalFish.class, TropicalFish.Pattern.class, TropicalFish::setPattern);

		// Villager
		profession = addOptEnum(transformers, config, "type", Villager.class, Villager.Profession.class, Villager::setProfession);

		// Wolf
		addBoolean(transformers, config, "angry", false, Wolf.class, Wolf::setAngry);
		addOptEnum(transformers, config, "color", Wolf.class, DyeColor.class, Wolf::setCollarColor);

		// Display
		ConfigData<Quaternionf> leftRotation = getQuaternion(config, "transformation.left-rotation");
		ConfigData<Quaternionf> rightRotation = getQuaternion(config, "transformation.right-rotation");
		ConfigData<Vector3f> translation = getVector(config, "transformation.translation");
		ConfigData<Vector3f> scale = getVector(config, "transformation.scale");
		ConfigData<Transformation> transformation = data -> null;
		if (checkNull(leftRotation) && checkNull(rightRotation) && checkNull(translation) && checkNull(scale)) {
			if (leftRotation.isConstant() && rightRotation.isConstant() && translation.isConstant() && scale.isConstant()) {
				Quaternionf lr = leftRotation.get();
				Quaternionf rr = rightRotation.get();
				Vector3f t = translation.get();
				Vector3f s = scale.get();

				Transformation transform = new Transformation(t, lr, s, rr);
				transformation = data -> transform;
			} else {
				transformation = data -> {
					Quaternionf lr = leftRotation.get(data);
					if (lr == null) return null;

					Quaternionf rr = rightRotation.get(data);
					if (rr == null) return null;

					Vector3f t = translation.get(data);
					if (t == null) return null;

					Vector3f s = scale.get(data);
					if (s == null) return null;

					return new Transformation(t, lr, s, rr);
				};
			}
		}
		transformers.put(Display.class, new Transformer<>(transformation, Display::setTransformation, true));

		addOptInteger(transformers, config, "teleport-duration", Display.class, Display::setTeleportDuration);
		addOptInteger(transformers, config, "interpolation-duration", Display.class, Display::setInterpolationDuration);
		addOptFloat(transformers, config, "view-range", Display.class, Display::setViewRange);
		addOptFloat(transformers, config, "shadow-radius", Display.class, Display::setShadowRadius);
		addOptFloat(transformers, config, "shadow-strength", Display.class, Display::setShadowStrength);
		addOptFloat(transformers, config, "width", Display.class, Display::setDisplayWidth);
		addOptFloat(transformers, config, "height", Display.class, Display::setDisplayHeight);
		addOptInteger(transformers, config, "interpolation-delay", Display.class, Display::setInterpolationDelay);
		addOptEnum(transformers, config, "billboard", Display.class, Display.Billboard.class, Display::setBillboard);
		addOptARGBColor(transformers, config, "glow-color-override", Display.class, Display::setGlowColorOverride);

		ConfigData<Integer> blockLight = ConfigDataUtil.getInteger(config, "brightness.block");
		ConfigData<Integer> skyLight = ConfigDataUtil.getInteger(config, "brightness.sky");
		ConfigData<Display.Brightness> brightness = data -> null;
		if (checkNull(blockLight) && checkNull(skyLight)) {
			if (blockLight.isConstant() && skyLight.isConstant()) {
				int bl = blockLight.get();
				int sl = skyLight.get();

				if (0 <= bl && bl <= 15 && 0 <= sl && sl <= 15) {
					Display.Brightness b = new Display.Brightness(bl, sl);
					brightness = data -> b;
				}
			} else {
				brightness = data -> {
					Integer bl = blockLight.get(data);
					if (bl == null || bl < 0 || bl > 15) return null;

					Integer sl = skyLight.get(data);
					if (sl == null || sl < 0 || sl > 15) return null;

					return new Display.Brightness(bl, sl);
				};
			}
		}
		transformers.put(Display.class, new Transformer<>(brightness, Display::setBrightness, true));

		// BlockDisplay
		addOptBlockData(transformers, config, "block", BlockDisplay.class, BlockDisplay::setBlock);

		// ItemDisplay
		MagicItem magicItem = MagicItems.getMagicItemFromString(config.getString("item"));
		if (magicItem != null) {
			ItemStack item = magicItem.getItemStack();
			transformers.put(ItemDisplay.class, new Transformer<>(data -> item, ItemDisplay::setItemStack));
		}

		addOptEnum(transformers, config, "item-display-transform", ItemDisplay.class, ItemDisplay.ItemDisplayTransform.class, ItemDisplay::setItemDisplayTransform);

		// TextDisplay
		addOptComponent(transformers, config, "text", TextDisplay.class, TextDisplay::text);
		addOptInteger(transformers, config, "line-width", TextDisplay.class, TextDisplay::setLineWidth);
		addOptARGBColor(transformers, config, "background", TextDisplay.class, TextDisplay::setBackgroundColor);
		addOptByte(transformers, config, "text-opacity", TextDisplay.class, TextDisplay::setTextOpacity);
		addOptBoolean(transformers, config, "shadow", TextDisplay.class, TextDisplay::setShadowed);
		addOptBoolean(transformers, config, "see-through", TextDisplay.class, TextDisplay::setSeeThrough);
		addOptBoolean(transformers, config, "default-background", TextDisplay.class, TextDisplay::setDefaultBackground);
		addOptEnum(transformers, config, "alignment", TextDisplay.class, TextDisplay.TextAlignment.class, TextDisplay::setAlignment);

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
		startLoc.add(horizOffset.multiply(relativeOffset.getZ()));
		startLoc.add(startLoc.getDirection().clone().multiply(relativeOffset.getX()));
		startLoc.setY(startLoc.getY() + relativeOffset.getY());

		EntityType entityType = this.entityType.get(data);
		if (entityType == null || (!entityType.isSpawnable() && entityType != EntityType.FALLING_BLOCK && entityType != EntityType.DROPPED_ITEM))
			return null;

		boolean[] displayHack = new boolean[] {false, false};
		Entity entity = switch (entityType) {
			case FALLING_BLOCK -> {
				BlockData blockData = fallingBlockData.get(data);
				if (blockData == null) yield null;

				Entity e = startLoc.getWorld().spawnFallingBlock(startLoc, blockData);
				if (consumer != null) consumer.accept(e);

				yield e;
			}
			case DROPPED_ITEM -> {
				Material material = dropItemMaterial.get(data);
				if (material == null) yield null;

				Entity e = startLoc.getWorld().dropItem(startLoc, new ItemStack(material));
				if (consumer != null) consumer.accept(e);

				yield e;
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

					if (e instanceof Display) {
						String version = org.bukkit.Bukkit.getMinecraftVersion();
						if ("1.19.4".equals(version) || "1.20".equals(version)) {
							displayHack[0] = true;
							displayHack[1] = e.isVisibleByDefault();

							e.setVisibleByDefault(false);
						}
					}
				});
			}
		};

		if (displayHack[0]) {
			entity.teleport(startLoc);
			entity.setVisibleByDefault(displayHack[1]);
		}

		return entity;
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

	private <T> ConfigData<BlockData> addBlockData(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, BlockData def, Class<T> type, BiConsumer<T, BlockData> setter) {
		ConfigData<BlockData> supplier = ConfigDataUtil.getBlockData(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));

		return supplier;
	}

	private <T> void addEulerAngle(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, EulerAngle def, Class<T> type, BiConsumer<T, EulerAngle> setter) {
		ConfigData<EulerAngle> supplier = ConfigDataUtil.getEulerAngle(config, name, def);
		transformers.put(type, new Transformer<>(supplier, setter));
	}

	private <T> void addOptBoolean(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, Boolean> setter) {
		ConfigData<Boolean> supplier = ConfigDataUtil.getBoolean(config, name);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	private <T> void addOptByte(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, Byte> setter) {
		ConfigData<Byte> supplier = ConfigDataUtil.getByte(config, name);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	private <T> void addOptInteger(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, Integer> setter) {
		ConfigData<Integer> supplier = ConfigDataUtil.getInteger(config, name);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	private <T> void addOptFloat(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, Float> setter) {
		ConfigData<Float> supplier = ConfigDataUtil.getFloat(config, name);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	private <T, E extends Enum<E>> ConfigData<E> addOptEnum(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, Class<E> enumType, BiConsumer<T, E> setter) {
		ConfigData<E> supplier = ConfigDataUtil.getEnum(config, name, enumType, null);
		transformers.put(type, new Transformer<>(supplier, setter, true));

		return supplier;
	}

	private <T> void addOptMaterial(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, Material> setter) {
		ConfigData<Material> supplier = ConfigDataUtil.getMaterial(config, name, null);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	private <T> void addOptARGBColor(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, Color> setter) {
		ConfigData<Color> supplier = ConfigDataUtil.getARGBColor(config, name, null);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	private <T> void addOptComponent(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, Component> setter) {
		ConfigData<Component> supplier = ConfigDataUtil.getComponent(config, name, null);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	private <T> void addOptBlockData(Multimap<Class<?>, Transformer<?, ?>> transformers, ConfigurationSection config, String name, Class<T> type, BiConsumer<T, BlockData> setter) {
		ConfigData<BlockData> supplier = ConfigDataUtil.getBlockData(config, name, null);
		transformers.put(type, new Transformer<>(supplier, setter, true));
	}

	public ConfigData<Vector3f> getVector(ConfigurationSection config, String path) {
		if (config.isString(path)) {
			String value = config.getString(path);
			if (value == null) return data -> null;

			String[] vec = value.split(",");
			if (vec.length != 3) return data -> null;

			try {
				Vector3f vector = new Vector3f(Float.parseFloat(vec[0]), Float.parseFloat(vec[1]), Float.parseFloat(vec[2]));
				return data -> vector;
			} catch (NumberFormatException e) {
				return data -> null;
			}
		}

		ConfigData<Float> x;
		ConfigData<Float> y;
		ConfigData<Float> z;

		if (config.isConfigurationSection(path)) {
			ConfigurationSection section = config.getConfigurationSection(path);
			if (section == null) return data -> null;

			x = ConfigDataUtil.getFloat(section, "x");
			y = ConfigDataUtil.getFloat(section, "y");
			z = ConfigDataUtil.getFloat(section, "z");
		} else if (config.isList(path)) {
			List<?> value = config.getList(path);
			if (value == null || value.size() != 3) return data -> null;

			Object xObj = value.get(0);
			Object yObj = value.get(1);
			Object zObj = value.get(2);

			if (xObj instanceof Number number) {
				float val = number.floatValue();
				x = data -> val;
			} else if (xObj instanceof String string) {
				x = FunctionData.build(string, Double::floatValue);
			} else x = null;

			if (yObj instanceof Number number) {
				float val = number.floatValue();
				y = data -> val;
			} else if (yObj instanceof String string) {
				y = FunctionData.build(string, Double::floatValue);
			} else y = null;

			if (zObj instanceof Number number) {
				float val = number.floatValue();
				z = data -> val;
			} else if (zObj instanceof String string) {
				z = FunctionData.build(string, Double::floatValue);
			} else z = null;

			if (x == null || y == null || z == null) return data -> null;
		} else {
			return data -> null;
		}

		if (!checkNull(x) || !checkNull(y) || !checkNull(z)) return data -> null;

		if (x.isConstant() && y.isConstant() && z.isConstant()) {
			Vector3f vector = new Vector3f(x.get(), y.get(), z.get());
			return data -> vector;
		}

		return new ConfigData<>() {

			@Override
			public Vector3f get(@NotNull SpellData data) {
				Float vx = x.get(data);
				if (vx == null) return null;

				Float vy = y.get(data);
				if (vy == null) return null;

				Float vz = z.get(data);
				if (vz == null) return null;

				return new Vector3f(vx, vy, vz);
			}

			@Override
			public boolean isConstant() {
				return false;
			}

		};

	}

	private ConfigData<Quaternionf> getQuaternion(ConfigurationSection config, String path) {
		if (config.isString(path)) {
			String value = config.getString(path);
			if (value == null) return data -> null;

			String[] quat = value.split(",");
			if (quat.length != 4) return data -> null;

			try {
				Quaternionf rot = new Quaternionf(Float.parseFloat(quat[0]), Float.parseFloat(quat[1]), Float.parseFloat(quat[2]), Float.parseFloat(quat[3]));
				return data -> rot;
			} catch (NumberFormatException e) {
				return data -> null;
			}
		}

		ConfigData<Float> angle = ConfigDataUtil.getFloat(config, path + ".angle");
		ConfigData<Vector3f> axis = getVector(config, path + ".axis");
		if (checkNull(angle) && checkNull(axis)) {
			if (angle.isConstant() && axis.isConstant()) {
				Vector3f ax = axis.get();
				float ang = angle.get();

				Quaternionf rot = new Quaternionf();
				rot.setAngleAxis(ang, ax.x, ax.y, ax.z);

				return data -> rot;
			}

			return new ConfigData<>() {

				@Override
				public Quaternionf get(@NotNull SpellData data) {
					Float ang = angle.get(data);
					if (ang == null) return null;

					Vector3f ax = axis.get(data);
					if (ax == null) return null;

					return new Quaternionf().setAngleAxis(ang, ax.x, ax.y, ax.z);
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		ConfigData<Float> x;
		ConfigData<Float> y;
		ConfigData<Float> z;
		ConfigData<Float> w;

		if (config.isConfigurationSection(path)) {
			ConfigurationSection section = config.getConfigurationSection(path);
			if (section == null) return data -> null;

			x = ConfigDataUtil.getFloat(section, "x");
			y = ConfigDataUtil.getFloat(section, "y");
			z = ConfigDataUtil.getFloat(section, "z");
			w = ConfigDataUtil.getFloat(section, "w");
		} else if (config.isList(path)) {
			List<?> value = config.getList(path);
			if (value == null || value.size() != 4) return data -> null;

			Object xObj = value.get(0);
			Object yObj = value.get(1);
			Object zObj = value.get(2);
			Object wObj = value.get(3);

			if (xObj instanceof Number number) {
				float val = number.floatValue();
				x = data -> val;
			} else if (xObj instanceof String string) {
				x = FunctionData.build(string, Double::floatValue);
			} else x = null;

			if (yObj instanceof Number number) {
				float val = number.floatValue();
				y = data -> val;
			} else if (yObj instanceof String string) {
				y = FunctionData.build(string, Double::floatValue);
			} else y = null;

			if (zObj instanceof Number number) {
				float val = number.floatValue();
				z = data -> val;
			} else if (zObj instanceof String string) {
				z = FunctionData.build(string, Double::floatValue);
			} else z = null;

			if (wObj instanceof Number number) {
				float val = number.floatValue();
				w = data -> val;
			} else if (zObj instanceof String string) {
				w = FunctionData.build(string, Double::floatValue);
			} else w = null;

			if (x == null || y == null || z == null || w == null) return data -> null;
		} else {
			return data -> null;
		}

		if (!checkNull(x) || !checkNull(y) || !checkNull(z) || !checkNull(w)) return data -> null;

		if (x.isConstant() && y.isConstant() && z.isConstant() && w.isConstant()) {
			Quaternionf rot = new Quaternionf(x.get(), y.get(), z.get(), w.get());
			return data -> rot;
		}

		return new ConfigData<>() {

			@Override
			public Quaternionf get(@NotNull SpellData data) {
				Float qx = x.get(data);
				if (qx == null) return null;

				Float qy = y.get(data);
				if (qy == null) return null;

				Float qz = z.get(data);
				if (qz == null) return null;

				Float qw = w.get(data);
				if (qw == null) return null;

				return new Quaternionf(qx, qy, qz, qw);
			}

			@Override
			public boolean isConstant() {
				return false;
			}

		};

	}

	private boolean checkNull(ConfigData<?> data) {
		return !data.isConstant() || data.get() != null;
	}

	public Multimap<EntityType, Transformer<?, ?>> getOptions() {
		return options;
	}

	public ConfigData<EntityType> getEntityType() {
		return entityType;
	}

	public ConfigData<Vector> getRelativeOffset() {
		return relativeOffset;
	}

	public void setEntityType(ConfigData<EntityType> entityType) {
		this.entityType = entityType;
	}

	@ApiStatus.Internal
	public ConfigData<Material> getDroppedItemStack() {
		return dropItemMaterial;
	}

	@ApiStatus.Internal
	public ConfigData<BlockData> getFallingBlockData() {
		return fallingBlockData;
	}

	@ApiStatus.Internal
	public ConfigData<Boolean> getBaby() {
		return baby;
	}

	@ApiStatus.Internal
	public ConfigData<Boolean> getChested() {
		return chested;
	}

	@ApiStatus.Internal
	public ConfigData<Boolean> getPowered() {
		return powered;
	}

	@ApiStatus.Internal
	public ConfigData<BlockData> getCarriedBlockData() {
		return carriedBlockData;
	}

	@ApiStatus.Internal
	public ConfigData<Horse.Color> getHorseColor() {
		return horseColor;
	}

	@ApiStatus.Internal
	public ConfigData<Horse.Style> getHorseStyle() {
		return horseStyle;
	}

	@ApiStatus.Internal
	public ConfigData<Boolean> getSaddled() {
		return saddled;
	}

	@ApiStatus.Internal
	public ConfigData<Llama.Color> getLlamaColor() {
		return llamaColor;
	}

	@ApiStatus.Internal
	public ConfigData<Parrot.Variant> getParrotVariant() {
		return parrotVariant;
	}

	@ApiStatus.Internal
	public ConfigData<Integer> getSize() {
		return size;
	}

	@ApiStatus.Internal
	public ConfigData<Boolean> getSheared() {
		return sheared;
	}

	@ApiStatus.Internal
	public ConfigData<DyeColor> getColor() {
		return color;
	}

	@ApiStatus.Internal
	public ConfigData<Boolean> getTamed() {
		return tamed;
	}

	@ApiStatus.Internal
	public ConfigData<TropicalFish.Pattern> getTropicalFishPattern() {
		return tropicalFishPattern;
	}

	@ApiStatus.Internal
	public ConfigData<DyeColor> getTropicalFishPatternColor() {
		return tropicalFishPatternColor;
	}

	@ApiStatus.Internal
	public ConfigData<Villager.Profession> getProfession() {
		return profession;
	}

	public record Transformer<T, C>(ConfigData<C> supplier, BiConsumer<T, C> setter, boolean optional) {

		public Transformer(ConfigData<C> supplier, BiConsumer<T, C> setter) {
			this(supplier, setter, false);
		}

		public void apply(T entity, SpellData data) {
			C value = supplier.get(data);
			if (!optional || value != null) setter.accept(entity, value);
		}

	}

}
