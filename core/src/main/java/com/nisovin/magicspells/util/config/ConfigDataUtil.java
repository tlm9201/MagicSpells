package com.nisovin.magicspells.util.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.slikey.exp4j.Expression;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.block.data.BlockData;
import org.bukkit.Particle.DustOptions;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.ColorUtil;
import com.nisovin.magicspells.util.ParticleUtil;

public class ConfigDataUtil {

	private static final Pattern ARGUMENT_MATCHER = Pattern.compile("%arg:(\\d+):(\\w+)%", Pattern.CASE_INSENSITIVE);
	private static final Pattern CASTER_MATCHER = Pattern.compile("%(var|castervar):(\\w+)%", Pattern.CASE_INSENSITIVE);
	private static final Pattern TARGET_MATCHER = Pattern.compile("%targetvar:(\\w+)%", Pattern.CASE_INSENSITIVE);

	@NotNull
	public static ConfigData<Integer> getInteger(@NotNull ConfigurationSection config, @NotNull String path, int def) {
		if (config.isInt(path)) {
			int value = config.getInt(path, def);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return (caster, target, power, args) -> def;

			return new FunctionData.IntegerData(ex, def);
		}

		return (caster, target, power, args) -> def;
	}

	@NotNull
	public static ConfigData<Integer> getInteger(@NotNull ConfigurationSection config, @NotNull String path, ConfigData<Integer> def) {
		if (config.isInt(path)) {
			int value = config.getInt(path);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return def;

			return new FunctionData.IntegerData(ex, def);
		}

		return def;
	}

	@NotNull
	public static ConfigData<Long> getLong(@NotNull ConfigurationSection config, @NotNull String path, long def) {
		if (config.isInt(path) || config.isLong(path)) {
			long value = config.getLong(path, def);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return (caster, target, power, args) -> def;

			return new FunctionData.LongData(ex, def);
		}

		return (caster, target, power, args) -> def;
	}

	@NotNull
	public static ConfigData<Long> getLong(@NotNull ConfigurationSection config, @NotNull String path, ConfigData<Long> def) {
		if (config.isInt(path) || config.isLong(path)) {
			long value = config.getLong(path);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return def;

			return new FunctionData.LongData(ex, def);
		}

		return def;
	}

	@NotNull
	public static ConfigData<Short> getShort(@NotNull ConfigurationSection config, @NotNull String path, short def) {
		if (config.isInt(path)) {
			short value = (short) config.getInt(path, def);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return (caster, target, power, args) -> def;

			return new FunctionData.ShortData(ex, def);
		}

		return (caster, target, power, args) -> def;
	}

	@NotNull
	public static ConfigData<Short> getShort(@NotNull ConfigurationSection config, @NotNull String path, ConfigData<Short> def) {
		if (config.isInt(path)) {
			short value = (short) config.getInt(path);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return def;

			return new FunctionData.ShortData(ex, def);
		}

		return def;
	}

	@NotNull
	public static ConfigData<Byte> getByte(@NotNull ConfigurationSection config, @NotNull String path, byte def) {
		if (config.isInt(path)) {
			byte value = (byte) config.getInt(path);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return (caster, target, power, args) -> def;

			return new FunctionData.ByteData(ex, def);
		}

		return (caster, target, power, args) -> def;
	}

	@NotNull
	public static ConfigData<Byte> getByte(@NotNull ConfigurationSection config, @NotNull String path, ConfigData<Byte> def) {
		if (config.isInt(path)) {
			byte value = (byte) config.getInt(path);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return def;

			return new FunctionData.ByteData(ex, def);
		}

		return def;
	}

	@NotNull
	public static ConfigData<Double> getDouble(@NotNull ConfigurationSection config, @NotNull String path, double def) {
		if (config.isInt(path) || config.isLong(path) || config.isDouble(path)) {
			double value = config.getDouble(path, def);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return (caster, target, power, args) -> def;

			return new FunctionData.DoubleData(ex, def);
		}

		return (caster, target, power, args) -> def;
	}

	@NotNull
	public static ConfigData<Double> getDouble(@NotNull ConfigurationSection config, @NotNull String path, ConfigData<Double> def) {
		if (config.isInt(path) || config.isLong(path) || config.isDouble(path)) {
			double value = config.getDouble(path);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return def;

			return new FunctionData.DoubleData(ex, def);
		}

		return def;
	}

	@NotNull
	public static ConfigData<Float> getFloat(@NotNull ConfigurationSection config, @NotNull String path, float def) {
		if (config.isInt(path) || config.isLong(path) || config.isDouble(path)) {
			float value = (float) config.getDouble(path, def);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path));
			if (ex == null) return (caster, target, power, args) -> def;

			return new FunctionData.FloatData(ex, def);
		}

		return (caster, target, power, args) -> def;
	}

	@NotNull
	public static ConfigData<Float> getFloat(@NotNull ConfigurationSection config, @NotNull String path, ConfigData<Float> def) {
		if (config.isInt(path) || config.isLong(path) || config.isDouble(path)) {
			float value = (float) config.getDouble(path);
			return (caster, target, power, args) -> value;
		}

		if (config.isString(path)) {
			Expression ex = FunctionData.buildExpression(config.getString(path, ""));
			if (ex == null) return def;

			return new FunctionData.FloatData(ex, def);
		}

		return def;
	}

	@NotNull
	public static ConfigData<String> getString(@NotNull ConfigurationSection config, @NotNull String path, @Nullable String def) {
		String value = config.getString(path, def);
		if (value == null) return (caster, target, power, args) -> null;

		return getString(value);
	}

	@NotNull
	public static ConfigData<String> getString(@Nullable String value) {
		if (value == null) return (caster, target, power, args) -> null;

		Matcher matcher = ARGUMENT_MATCHER.matcher(value);
		if (matcher.matches()) {
			try {
				int index = Integer.parseInt(matcher.group(1)) - 1;
				String defaultString = matcher.group(2);

				return new ConfigData<>() {

					@Override
					public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
						if (args != null && args.length > index) return args[index];
						else return defaultString;
					}

					@Override
					public boolean isConstant() {
						return false;
					}

				};
			} catch (NumberFormatException e) {
				return (caster, target, power, args) -> value;
			}
		}

		matcher = CASTER_MATCHER.matcher(value);
		if (matcher.matches()) {
			String variable = matcher.group(2);

			return new ConfigData<>() {

				@Override
				public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					if (!(caster instanceof Player player)) return null;
					return MagicSpells.getVariableManager().getStringValue(variable, player);
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		matcher = TARGET_MATCHER.matcher(value);
		if (matcher.matches()) {
			String variable = matcher.group(1);

			return new ConfigData<>() {

				@Override
				public String get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					if (!(target instanceof Player player)) return null;
					return MagicSpells.getVariableManager().getStringValue(variable, player);
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		boolean argReplacement = value.contains("%arg");
		boolean varReplacement = value.contains("%var") || value.contains("%playervar");

		boolean targeted = value.contains("%targetvar");
		boolean targetedReplacement = targeted || value.contains("%castervar");

		if (argReplacement || varReplacement || targetedReplacement)
			return new StringData(value, varReplacement, targetedReplacement, argReplacement, targeted);

		return (caster, target, power, args) -> value;
	}

	public static ConfigData<Boolean> getBoolean(@NotNull ConfigurationSection config, @NotNull String path, boolean def) {
		if (config.isBoolean(path)) {
			boolean val = config.getBoolean(path);
			return (caster, target, power, args) -> val;
		}

		if (config.isString(path)) {
			ConfigData<String> supplier = getString(config, path, Boolean.toString(def));
			return (caster, target, power, args) -> Boolean.parseBoolean(supplier.get(caster, target, power, args));
		}

		return (caster, target, power, args) -> def;
	}

	@NotNull
	public static <T extends Enum<T>> ConfigData<T> getEnum(@NotNull ConfigurationSection config,
															@NotNull String path,
															@NotNull Class<T> type,
															@Nullable T def) {
		String value = config.getString(path);
		if (value == null) return (caster, target, power, args) -> def;

		try {
			T val = Enum.valueOf(type, value.toUpperCase());
			return (caster, target, power, args) -> val;
		} catch (IllegalArgumentException e) {
			ConfigData<String> supplier = getString(value);

			return new ConfigData<>() {

				@Override
				public T get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					String val = supplier.get(caster, target, power, args);
					if (val == null) return def;

					try {
						return Enum.valueOf(type, val.toUpperCase());
					} catch (IllegalArgumentException ex) {
						return def;
					}
				}

				@Override
				public boolean isConstant() {
					return supplier.isConstant();
				}

			};
		}
	}

	@NotNull
	public static ConfigData<Particle> getParticle(@NotNull ConfigurationSection config, @NotNull String path, @Nullable Particle def) {
		String value = config.getString(path);
		if (value == null) return (caster, target, power, args) -> def;

		Particle val = ParticleUtil.ParticleEffect.getParticle(value);
		if (val != null) return (caster, target, power, args) -> val;

		ConfigData<String> supplier = getString(value);
		return new ConfigData<>() {

			@Override
			public Particle get(LivingEntity caster, LivingEntity target, float power, String[] args) {
				String val = supplier.get(caster, target, power, args);
				if (val == null) return def;

				Particle particle = ParticleUtil.ParticleEffect.getParticle(val);
				return particle == null ? def : particle;
			}

			@Override
			public boolean isConstant() {
				return supplier.isConstant();
			}

		};
	}

	@NotNull
	public static ConfigData<BlockData> getBlockData(@NotNull ConfigurationSection config, @NotNull String path, @Nullable BlockData def) {
		String value = config.getString(path);
		if (value == null) return (caster, target, power, args) -> def;

		try {
			BlockData val = Bukkit.createBlockData(value);
			return (caster, target, power, args) -> val;
		} catch (IllegalArgumentException e) {
			ConfigData<String> supplier = getString(value);

			return new ConfigData<>() {
				@Override
				public BlockData get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					String val = supplier.get(caster, target, power, args);
					if (val == null) return def;

					try {
						return Bukkit.createBlockData(val);
					} catch (IllegalArgumentException e) {
						return def;
					}
				}

				@Override
				public boolean isConstant() {
					return supplier.isConstant();
				}

			};
		}
	}

	@NotNull
	public static ConfigData<DustOptions> getDustOptions(@NotNull ConfigurationSection config,
														 @NotNull String colorPath,
														 @NotNull String sizePath,
														 @Nullable DustOptions def) {
		String colorHex = config.getString(colorPath);
		Color color = colorHex != null ? ColorUtil.getColorFromHexString(colorHex) : (def != null ? def.getColor() : null);

		if (color != null) {
			if (config.isInt(sizePath) || config.isLong(sizePath) || config.isDouble(sizePath)) {
				DustOptions options = new DustOptions(color, (float) config.getDouble(sizePath));
				return (caster, target, power, args) -> options;
			}

			ConfigData<Float> size = getFloat(config, sizePath, def == null ? 0 : def.getSize());
			return new ConfigData<>() {

				@Override
				public DustOptions get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					return new DustOptions(color, size.get(caster, target, power, args));
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}
		if (colorHex == null) return (caster, target, power, args) -> null;

		ConfigData<String> colorSupplier = getString(colorHex);
		if (config.isInt(sizePath)) {
			float size = (float) config.getDouble(sizePath);

			return new ConfigData<>() {

				@Override
				public DustOptions get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					Color c = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
					if (c == null) return def;

					return new DustOptions(c, size);
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		ConfigData<Float> size = getFloat(config, sizePath, def == null ? 0 : def.getSize());
		return new ConfigData<>() {

			@Override
			public DustOptions get(LivingEntity caster, LivingEntity target, float power, String[] args) {
				Color c = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
				if (c == null) return def;

				return new DustOptions(c, size.get(caster, target, power, args));
			}

			@Override
			public boolean isConstant() {
				return false;
			}

		};
	}

	@NotNull
	public static ConfigData<DustTransition> getDustTransition(@NotNull ConfigurationSection config,
															   @NotNull String colorPath,
															   @NotNull String toColorPath,
															   @NotNull String sizePath,
															   @Nullable DustTransition def) {
		Color color = null;
		String colorHex = config.getString(colorPath);
		if (colorHex != null) color = ColorUtil.getColorFromHexString(colorHex);
		else if (def != null) color = def.getColor();

		Color toColor = null;
		String toColorHex = config.getString(toColorPath);
		if (toColorHex != null) toColor = ColorUtil.getColorFromHexString(toColorHex);
		else if (def != null) toColor = def.getToColor();

		final Color finalColor = color;
		final Color finalToColor = toColor;

		if (finalColor != null && finalToColor != null) {
			if (config.isInt(sizePath) || config.isLong(sizePath) || config.isDouble(sizePath)) {
				DustTransition transition = new DustTransition(finalColor, finalToColor, (float) config.getDouble(sizePath));
				return (caster, target, power, args) -> transition;
			}

			if (!config.isSet(sizePath) && def != null) {
				DustTransition transition = new DustTransition(finalColor, finalToColor, def.getSize());
				return (caster, target, power, args) -> transition;
			}

			ConfigData<Float> size = getFloat(config, sizePath, def == null ? 0 : def.getSize());
			return new ConfigData<>() {

				@Override
				public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					return new DustTransition(finalColor, finalToColor, size.get(caster, target, power, args));
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		if (finalColor != null) {
			if (toColorHex == null) return (caster, target, power, args) -> null;

			ConfigData<String> toColorSupplier = getString(toColorHex);

			if (config.isInt(sizePath) || config.isLong(sizePath) || config.isDouble(sizePath)) {
				float size = (float) config.getDouble(sizePath);

				return new ConfigData<>() {

					@Override
					public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
						Color c = ColorUtil.getColorFromHexString(toColorSupplier.get(caster, target, power, args));
						if (c == null) {
							if (def != null) c = def.getToColor();
							else return null;
						}

						return new DustTransition(finalColor, c, size);
					}

					@Override
					public boolean isConstant() {
						return false;
					}

				};
			}

			if (!config.isSet(sizePath) && def != null) {
				float size = def.getSize();

				return new ConfigData<>() {

					@Override
					public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
						Color c = ColorUtil.getColorFromHexString(toColorSupplier.get(caster, target, power, args));
						if (c == null) c = def.getToColor();

						return new DustTransition(finalColor, c, size);
					}

					@Override
					public boolean isConstant() {
						return false;
					}

				};
			}

			ConfigData<Float> size = getFloat(config, sizePath, def == null ? 0 : def.getSize());
			return new ConfigData<>() {

				@Override
				public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					Color c = ColorUtil.getColorFromHexString(toColorSupplier.get(caster, target, power, args));
					if (c == null) {
						if (def != null) c = def.getToColor();
						else return null;
					}

					return new DustTransition(finalColor, c, size.get(caster, target, power, args));
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		if (finalToColor != null) {
			if (colorHex == null) return (caster, target, power, args) -> null;

			ConfigData<String> colorSupplier = getString(colorHex);

			if (config.isInt(sizePath) || config.isLong(sizePath) || config.isDouble(sizePath)) {
				float size = (float) config.getDouble(sizePath);

				return new ConfigData<>() {

					@Override
					public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
						Color c = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
						if (c == null) {
							if (def != null) c = def.getColor();
							else return null;
						}

						return new DustTransition(c, finalToColor, size);
					}

					@Override
					public boolean isConstant() {
						return false;
					}

				};
			}

			if (!config.isSet(sizePath) && def != null) {
				float size = def.getSize();

				return new ConfigData<>() {

					@Override
					public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
						Color c = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
						if (c == null) c = def.getToColor();

						return new DustTransition(c, finalToColor, size);
					}

					@Override
					public boolean isConstant() {
						return false;
					}

				};
			}

			ConfigData<Float> size = getFloat(config, sizePath, def == null ? 0 : def.getSize());
			return new ConfigData<>() {

				@Override
				public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					Color c = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
					if (c == null) {
						if (def != null) c = def.getColor();
						else return null;
					}

					return new DustTransition(c, finalToColor, size.get(caster, target, power, args));
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		ConfigData<String> colorSupplier = getString(colorHex);
		ConfigData<String> toColorSupplier = getString(toColorHex);

		if (config.isInt(sizePath) || config.isLong(sizePath) || config.isDouble(sizePath)) {
			float size = (float) config.getDouble(sizePath);

			return new ConfigData<>() {

				@Override
				public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					Color col = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
					if (col == null) {
						if (def != null) col = def.getColor();
						else return null;
					}

					Color toCol = ColorUtil.getColorFromHexString(toColorSupplier.get(caster, target, power, args));
					if (toCol == null) {
						if (def != null) toCol = def.getColor();
						else return null;
					}

					return new DustTransition(col, toCol, size);
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		if (!config.isSet(sizePath) && def != null) {
			float size = def.getSize();

			return new ConfigData<>() {

				@Override
				public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
					Color col = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
					if (col == null) return def;

					Color toCol = ColorUtil.getColorFromHexString(toColorSupplier.get(caster, target, power, args));
					if (toCol == null) return def;

					return new DustTransition(col, toCol, size);
				}

				@Override
				public boolean isConstant() {
					return false;
				}

			};
		}

		ConfigData<Float> size = getFloat(config, sizePath, def == null ? 0 : def.getSize());
		return new ConfigData<>() {

			@Override
			public DustTransition get(LivingEntity caster, LivingEntity target, float power, String[] args) {
				Color col = ColorUtil.getColorFromHexString(colorSupplier.get(caster, target, power, args));
				if (col == null) {
					if (def != null) col = def.getColor();
					else return null;
				}

				Color toCol = ColorUtil.getColorFromHexString(toColorSupplier.get(caster, target, power, args));
				if (toCol == null) {
					if (def != null) toCol = def.getColor();
					else return null;
				}

				return new DustTransition(col, toCol, size.get(caster, target, power, args));
			}

			@Override
			public boolean isConstant() {
				return false;
			}

		};
	}

}
