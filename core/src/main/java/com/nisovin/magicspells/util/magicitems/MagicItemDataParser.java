package com.nisovin.magicspells.util.magicitems;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.ArrayList;

import java.io.IOException;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonReader;
import com.google.common.collect.Multimap;
import com.google.gson.JsonSyntaxException;
import com.google.common.collect.HashMultimap;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.FireworkEffect;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.block.banner.PatternType;
import org.bukkit.attribute.AttributeModifier;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.AttributeUtil;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.handlers.EnchantmentHandler;
import com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.*;

public class MagicItemDataParser {

	private static final Gson gson = new Gson();
	private static final TypeAdapter<JsonElement> jsonElementTypeAdapter = gson.getAdapter(JsonElement.class);

	/* splits the saved magicItemData string by the "|" char
		itemType{data}|itemType{data}...
	*/
	public static final String DATA_REGEX = "(?=(?:(?:[^\"]*\"){2})*[^\"]*$)(?![^{]*})(?![^\\[]*\\])\\|+";

	public static MagicItemData parseMagicItemData(String str) {
		String[] args = str.split("\\{", 2);
		// check if it contains additional data
		if (args.length < 2) {
			// it doesnt, check if its a material type
			Material type = Util.getMaterial(str.trim());
			if (type == null) return null;

			MagicItemData magicItemData = new MagicItemData();
			magicItemData.setAttribute(TYPE, type);

			return magicItemData;
		}

		args[1] = "{" + args[1];

		Material type;

		type = Util.getMaterial(args[0].trim());
		if (type == null) return null;

		JsonReader jsonReader = new JsonReader(new StringReader(args[1]));
		jsonReader.setLenient(true);

		MagicItemData data = new MagicItemData();
		data.setAttribute(TYPE, type);

		if (type.isAir()) return data;

		try {
			while (jsonReader.peek() != JsonToken.END_DOCUMENT) {
				JsonElement jsonElement = jsonElementTypeAdapter.read(jsonReader);

				if (!jsonElement.isJsonObject()) continue;
				JsonObject jsonObject = jsonElement.getAsJsonObject();

				Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObject.entrySet();
				for (Map.Entry<String, JsonElement> entry : jsonEntries) {
					String key = entry.getKey();
					JsonElement value = entry.getValue();

					switch (key.toLowerCase()) {
						case "name":
							data.setAttribute(NAME, Util.colorize(value.getAsString()));
							break;
						case "amount":
							data.setAttribute(AMOUNT, value.getAsInt());
							break;
						case "durability":
							data.setAttribute(DURABILITY, value.getAsInt());
							break;
						case "repaircost":
							data.setAttribute(REPAIR_COST, value.getAsInt());
							break;
						case "custommodeldata":
							data.setAttribute(CUSTOM_MODEL_DATA, value.getAsInt());
							break;
						case "power":
							data.setAttribute(POWER, value.getAsInt());
							break;
						case "unbreakable":
							data.setAttribute(UNBREAKABLE, value.getAsBoolean());
							break;
						case "hidetooltip":
							data.setAttribute(HIDE_TOOLTIP, value.getAsBoolean());
							break;
						case "color":
							try {
								Color color = Color.fromRGB(Integer.parseInt(value.getAsString().replace("#", ""), 16));
								data.setAttribute(COLOR, color);
							} catch (NumberFormatException e) {
								DebugHandler.debugNumberFormat(e);
							}
							break;
						case "potiondata":
							String[] potionDataArgs = value.getAsString().split(" ");

							try {
								PotionType potionType = PotionType.valueOf(potionDataArgs[0].toUpperCase());
								boolean extended = false, upgraded = false;

								if (potionDataArgs.length > 1) {
									if (potionDataArgs[1].equalsIgnoreCase("extended")) extended = true;
									else if (potionDataArgs[1].equalsIgnoreCase("upgraded")) upgraded = true;
								}

								PotionData potionData = new PotionData(potionType, extended, upgraded);

								data.setAttribute(POTION_DATA, potionData);
							} catch (IllegalArgumentException e) {
								DebugHandler.debugIllegalArgumentException(e);
							}
							break;
						case "fireworkeffect":
							String[] effectString = value.getAsString().split(" ");

							if (effectString.length >= 4) {
								try {
									FireworkEffect.Type fireworkType = FireworkEffect.Type.valueOf(effectString[0].toUpperCase());
									boolean trail = Boolean.parseBoolean(effectString[1]);
									boolean flicker = Boolean.parseBoolean(effectString[2]);
									Color[] colors = Util.getColorsFromString(effectString[3]);
									Color[] fadeColors = null;

									if (effectString.length > 4) fadeColors = Util.getColorsFromString(effectString[4]);
									if (fadeColors == null) fadeColors = new Color[0];

									FireworkEffect effect = FireworkEffect.builder()
										.flicker(flicker)
										.trail(trail)
										.with(fireworkType)
										.withColor(colors)
										.withFade(fadeColors)
										.build();

									data.setAttribute(FIREWORK_EFFECT, effect);
								} catch (IllegalArgumentException e) {
									DebugHandler.debugBadEnumValue(FireworkEffect.Type.class, effectString[0].toUpperCase());
									MagicSpells.error("'" + value.getAsString() + "' could not be connected to a firework effect.");
								}
							} else MagicSpells.error("'" + value.getAsString() + "' could not be connected to a firework effect.");
							break;
						case "skullowner":
							data.setAttribute(SKULL_OWNER, value.getAsString());
							break;
						case "title":
							data.setAttribute(TITLE, Util.colorize(value.getAsString()));
							break;
						case "author":
							data.setAttribute(AUTHOR, Util.colorize(value.getAsString()));
							break;
						case "uuid":
							data.setAttribute(UUID, value.getAsString());
							break;
						case "texture":
							data.setAttribute(TEXTURE, value.getAsString());
							break;
						case "signature":
							data.setAttribute(SIGNATURE, value.getAsString());
							break;
						case "enchantments":
						case "enchants":
							if (!value.isJsonObject()) continue;

							Map<String, Object> objectMap;
							try {
								objectMap = gson.fromJson(value.getAsJsonObject().toString(), HashMap.class);

								Map<Enchantment, Integer> enchantments = new HashMap<>();
								for (String enchantString : objectMap.keySet()) {
									Enchantment enchantment = EnchantmentHandler.getEnchantment(enchantString);

									if (enchantment == null) {
										MagicSpells.error('\'' + enchantString + "' could not be connected to an enchantment");
										continue;
									}

									double v;
									try {
										v = Double.parseDouble(objectMap.get(enchantString).toString().trim());
									} catch (NumberFormatException e) {
										DebugHandler.debugNumberFormat(e);
										continue;
									}

									enchantments.put(enchantment, (int) v);
								}

								if (data.hasAttribute(FAKE_GLINT)) {
									boolean fakeGlint = (boolean) data.getAttribute(FAKE_GLINT);

									if (!enchantments.isEmpty() && fakeGlint) data.removeAttribute(FAKE_GLINT);
								}

								data.setAttribute(ENCHANTMENTS, enchantments);
							} catch (JsonSyntaxException exception) {
								MagicSpells.error("Invalid enchantment syntax!");
								continue;
							}
							break;
						case "fakeglint":
							if (data.hasAttribute(ENCHANTMENTS)) {
								Map<Enchantment, Integer> enchantments = (Map<Enchantment, Integer>) data.getAttribute(ENCHANTMENTS);
								boolean fakeGlint = value.getAsBoolean();

								if (enchantments.isEmpty() && fakeGlint) data.setAttribute(FAKE_GLINT, true);
							} else if (value.getAsBoolean()) data.setAttribute(FAKE_GLINT, true);
							break;
						case "attributes":
							if (!value.isJsonArray()) continue;

							Multimap<Attribute, AttributeModifier> itemAttributes = HashMultimap.create();
							JsonArray attributeArray = value.getAsJsonArray();
							for (JsonElement element : attributeArray) {
								String[] attributeArgs = element.getAsString().split(" ");
								if (attributeArgs.length < 2) continue;

								Attribute attribute = AttributeUtil.getAttribute(attributeArgs[0]);
								double val = Double.parseDouble(attributeArgs[1]);

								AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
								if (attributeArgs.length >= 3) operation = AttributeUtil.getOperation(attributeArgs[2]);

								EquipmentSlot slot = null;
								if (attributeArgs.length >= 4) {
									try {
										slot = EquipmentSlot.valueOf(attributeArgs[3].toUpperCase());
									} catch (Exception ignored) {}
								}

								AttributeModifier modifier = new AttributeModifier(java.util.UUID.randomUUID(), attributeArgs[0], val, operation, slot);
								itemAttributes.put(attribute, modifier);
							}

							data.setAttribute(ATTRIBUTES, itemAttributes);
							break;
						case "lore":
							if (!value.isJsonArray()) continue;

							List<String> lore = new ArrayList<>();
							JsonArray jsonArray = value.getAsJsonArray();
							for (JsonElement elementInside : jsonArray) {
								lore.add(Util.colorize(elementInside.getAsString()));
							}

							data.setAttribute(LORE, lore);
							break;
						case "pages":
							if (!value.isJsonArray()) continue;

							List<String> pages = new ArrayList<>();
							JsonArray pageArray = value.getAsJsonArray();
							for (JsonElement page : pageArray) {
								pages.add(Util.colorize(page.getAsString()));
							}

							data.setAttribute(PAGES, pages);
							break;
						case "patterns":
							if (!value.isJsonArray()) continue;

							List<Pattern> patterns = new ArrayList<>();
							JsonArray patternStrings = value.getAsJsonArray();
							for (JsonElement element : patternStrings) {
								String patternString = element.getAsString();
								String[] pattern = patternString.split(" ");

								if (pattern.length == 2) {
									PatternType patternType;
									DyeColor dyeColor;

									try {
										patternType = PatternType.valueOf(pattern[0]);
									} catch (IllegalArgumentException e) {
										DebugHandler.debugBadEnumValue(PatternType.class, pattern[0]);
										MagicSpells.error("'" + patternString + "' could not be connected to a pattern.");
										continue;
									}

									try {
										dyeColor = DyeColor.valueOf(pattern[1]);
									} catch (IllegalArgumentException e) {
										DebugHandler.debugBadEnumValue(DyeColor.class, pattern[1]);
										MagicSpells.error("'" + patternString + "' could not be connected to a pattern.");
										continue;
									}

									patterns.add(new Pattern(dyeColor, patternType));
								} else MagicSpells.error("'" + patternString + "' could not be connected to a pattern.");
							}

							data.setAttribute(PATTERNS, patterns);
							break;
						case "potioneffects":
							if (!value.isJsonArray()) continue;

							List<PotionEffect> potionEffects = new ArrayList<>();
							JsonArray potionEffectStrings = value.getAsJsonArray();

							for (JsonElement element : potionEffectStrings) {
								String potionEffectString = element.getAsString();
								PotionEffect eff = Util.buildPotionEffect(potionEffectString);

								if (eff != null) potionEffects.add(eff);
								else MagicSpells.error("'" + potionEffectString + "' could not be connected to a potion effect.");
							}

							data.setAttribute(POTION_EFFECTS, potionEffects);
							break;
						case "fireworkeffects":
							if (!value.isJsonArray()) continue;

							List<FireworkEffect> fireworkEffects = new ArrayList<>();
							JsonArray fireworkEffectStrings = value.getAsJsonArray();
							for (JsonElement eff : fireworkEffectStrings) {
								String[] effString = eff.getAsString().split(" ");

								if (effString.length == 4 || effString.length == 5) {
									try {
										FireworkEffect.Type fireworkType = FireworkEffect.Type.valueOf(effString[0].toUpperCase());
										boolean trail = Boolean.parseBoolean(effString[1]);
										boolean flicker = Boolean.parseBoolean(effString[2]);
										Color[] colors = Util.getColorsFromString(effString[3]);
										Color[] fadeColors = null;

										if (effString.length > 4) fadeColors = Util.getColorsFromString(effString[4]);
										if (fadeColors == null) fadeColors = new Color[0];

										FireworkEffect effect = FireworkEffect.builder()
											.flicker(flicker)
											.trail(trail)
											.with(fireworkType)
											.withColor(colors)
											.withFade(fadeColors)
											.build();

										fireworkEffects.add(effect);
									} catch (IllegalArgumentException e) {
										DebugHandler.debugBadEnumValue(FireworkEffect.Type.class, effString[0].toUpperCase());
										MagicSpells.error("'" + eff.getAsString() + "' could not be connected to a firework effect.");
									}
								} else MagicSpells.error("'" + eff.getAsString() + "' could not be connected to a firework effect.");
							}

							data.setAttribute(FIREWORK_EFFECTS, fireworkEffects);
							break;
						case "ignoredattributes":
							if (!value.isJsonArray()) continue;
							EnumSet<MagicItemAttribute> ignoredAttributes = data.getIgnoredAttributes();
							JsonArray ignoredAttributeStrings = value.getAsJsonArray();

							for (JsonElement element : ignoredAttributeStrings) {
								String ignoredAttribute = element.getAsString().toUpperCase();
								try {
									ignoredAttributes.add(MagicItemAttribute.valueOf(ignoredAttribute));
								} catch (IllegalArgumentException e) {
									DebugHandler.debugBadEnumValue(MagicItemAttribute.class, ignoredAttribute);
								}
							}
							break;
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

}
