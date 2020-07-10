package com.nisovin.magicspells.util.magicitems;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import java.io.IOException;
import java.io.StringReader;

import com.google.gson.*;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonReader;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.bukkit.enchantments.Enchantment;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.handlers.EnchantmentHandler;
import com.nisovin.magicspells.handlers.PotionEffectHandler;

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
			magicItemData.setType(type);
			return magicItemData;
		}

		args[1] = "{" + args[1];

		Material type;
		String name = null;
		int amount = 1;
		int durability = -1;
		int customModelData = 0;
		boolean unbreakable = false;
		Color color = null;
		PotionType potionType = PotionType.UNCRAFTABLE;
		String title = null;
		String author = null;
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		List<String> lore = null;

		type = Util.getMaterial(args[0].trim());
		if (type == null) return null;

		JsonReader jsonReader = new JsonReader(new StringReader(args[1]));
		jsonReader.setLenient(true);

		try {
			while (jsonReader.peek() != JsonToken.END_DOCUMENT) {
				JsonElement jsonElement = jsonElementTypeAdapter.read(jsonReader);

				if (!jsonElement.isJsonObject()) continue;
				JsonObject jsonObject = jsonElement.getAsJsonObject();

				Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObject.entrySet();
				for (Map.Entry<String, JsonElement> entry : jsonEntries) {
					String key = entry.getKey();
					JsonElement value = entry.getValue();

					if (key.equalsIgnoreCase("name")) name = value.getAsString();

					if (key.equalsIgnoreCase("amount")) amount = value.getAsInt();

					if (key.equalsIgnoreCase("durability")) durability = value.getAsInt();

					if (key.equalsIgnoreCase("custommodeldata")) customModelData = value.getAsInt();

					if (key.equalsIgnoreCase("unbreakable")) unbreakable = value.getAsBoolean();

					if (key.equalsIgnoreCase("color")) {
						try {
							color = Color.fromRGB(Integer.parseInt(value.getAsString().replace("#", ""), 16));
						} catch (NumberFormatException e) {
							DebugHandler.debugNumberFormat(e);
						}
					}

					if (key.equalsIgnoreCase("potion")) potionType = PotionEffectHandler.getPotionType(value.getAsString());

					if (key.equalsIgnoreCase("title")) title = value.getAsString();

					if (key.equalsIgnoreCase("author")) author = value.getAsString();

					if (key.equalsIgnoreCase("enchants") || key.equalsIgnoreCase("enchantments")) {
						if (!value.isJsonObject()) continue;

						Map<Object, Object> objectMap;
						try {
							objectMap = gson.fromJson(value.getAsJsonObject().toString(), HashMap.class);
						} catch (JsonSyntaxException exception) {
							MagicSpells.error("Invalid enchantment syntax!");
							continue;
						}

						if (objectMap == null) continue;
						for (Object o : objectMap.keySet()) {
							Enchantment enchantment = EnchantmentHandler.getEnchantment(o.toString());
							int v = (int) Double.parseDouble(objectMap.get(o).toString().trim());
							enchantments.put(enchantment, v);
						}
					}

					if (key.equalsIgnoreCase("lore")) {
						if (!value.isJsonArray()) continue;
						lore = new ArrayList<>();
						JsonArray jsonArray = value.getAsJsonArray();
						for (JsonElement elementInside : jsonArray) {
							lore.add(elementInside.getAsString());
						}
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		MagicItemData data = new MagicItemData();

		data.setType(type);
		data.setName(name);
		data.setAmount(amount);
		data.setDurability(durability);
		data.setCustomModelData(customModelData);
		data.setUnbreakable(unbreakable);
		data.setColor(color);
		data.setPotionType(potionType);
		data.setTitle(title);
		data.setAuthor(author);
		data.setEnchantments(enchantments);
		data.setLore(lore);

		return data;
	}

}
