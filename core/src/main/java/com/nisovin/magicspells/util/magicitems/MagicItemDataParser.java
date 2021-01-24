package com.nisovin.magicspells.util.magicitems;

import java.util.Map;
import java.util.Set;
import java.util.List;
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
import com.google.gson.JsonSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.handlers.EnchantmentHandler;
import com.nisovin.magicspells.handlers.PotionEffectHandler;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.ItemAttribute.*;

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
			magicItemData.setItemAttribute(TYPE, type);

			return magicItemData;
		}

		args[1] = "{" + args[1];

		Material type;

		type = Util.getMaterial(args[0].trim());
		if (type == null) return null;

		JsonReader jsonReader = new JsonReader(new StringReader(args[1]));
		jsonReader.setLenient(true);

		MagicItemData data = new MagicItemData();
		data.setItemAttribute(TYPE, type);

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
							data.setItemAttribute(NAME, value.getAsString());
							break;
						case "amount":
							data.setItemAttribute(AMOUNT, value.getAsInt());
							break;
						case "durability":
							data.setItemAttribute(DURABILITY, value.getAsInt());
							break;
						case "custommodeldata":
							data.setItemAttribute(CUSTOM_MODEL_DATA, value.getAsInt());
							break;
						case "unbreakable":
							data.setItemAttribute(UNBREAKABLE, value.getAsBoolean());
							break;
						case "color":
							try {
								Color color = Color.fromRGB(Integer.parseInt(value.getAsString().replace("#", ""), 16));
								data.setItemAttribute(COLOR, color);
							} catch (NumberFormatException e) {
								DebugHandler.debugNumberFormat(e);
							}
							break;
						case "potiontype":
							data.setItemAttribute(POTION_TYPE, PotionEffectHandler.getPotionType(value.getAsString()));
							break;
						case "title":
							data.setItemAttribute(TITLE, value.getAsString());
							break;
						case "author":
							data.setItemAttribute(AUTHOR, value.getAsString());
							break;
						case "enchantments":
						case "enchants":
							if (!value.isJsonObject()) continue;

							Map<Object, Object> objectMap;
							try {
								objectMap = gson.fromJson(value.getAsJsonObject().toString(), HashMap.class);

								Map<Enchantment, Integer> enchantments = new HashMap<>();
								for (Object o : objectMap.keySet()) {
									Enchantment enchantment = EnchantmentHandler.getEnchantment(o.toString());
									int v = (int) Double.parseDouble(objectMap.get(o).toString().trim());
									enchantments.put(enchantment, v);
								}

								data.setItemAttribute(ENCHANTMENTS, enchantments);
							} catch (JsonSyntaxException exception) {
								MagicSpells.error("Invalid enchantment syntax!");
								continue;
							}
							break;
						case "lore":
							if (!value.isJsonArray()) continue;

							List<String> lore = new ArrayList<>();
							JsonArray jsonArray = value.getAsJsonArray();
							for (JsonElement elementInside : jsonArray) {
								lore.add(elementInside.getAsString());
							}
							data.setItemAttribute(LORE, lore);
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
