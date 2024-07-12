package com.nisovin.magicspells.util.itemreader;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.List;
import java.util.UUID;

import com.google.common.io.ByteStreams;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.collect.LinkedHashMultimap;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.AttributeUtil;
import com.nisovin.magicspells.util.ConfigReaderUtil;
import com.nisovin.magicspells.util.magicitems.MagicItemData;
import static com.nisovin.magicspells.util.magicitems.MagicItemData.MagicItemAttribute.ATTRIBUTES;

public class AttributeHandler {

	private static final String CONFIG_NAME = ATTRIBUTES.toString();

	public static void process(ConfigurationSection config, ItemMeta meta, MagicItemData data) {
		if (!config.isList(CONFIG_NAME)) return;

		Multimap<Attribute, AttributeModifier> modifiers = getAttributeModifiers(config.getList(CONFIG_NAME));
		if (modifiers.isEmpty()) return;

		meta.setAttributeModifiers(modifiers);
		data.setAttribute(ATTRIBUTES, modifiers);
	}

	public static void processItemMeta(@NotNull ItemMeta meta, @NotNull MagicItemData data) {
		if (!data.hasAttribute(ATTRIBUTES)) return;

		meta.setAttributeModifiers((Multimap<Attribute, AttributeModifier>) data.getAttribute(ATTRIBUTES));
	}

	public static void processMagicItemData(@NotNull ItemMeta meta, @NotNull MagicItemData data) {
		if (!meta.hasAttributeModifiers()) return;

		data.setAttribute(ATTRIBUTES, meta.getAttributeModifiers());
	}

	public static LinkedHashMultimap<Attribute, AttributeModifier> getAttributeModifiers(List<?> data) {
		return getAttributeModifiers(data, null);
	}

	@SuppressWarnings("UnstableApiUsage")
	public static LinkedHashMultimap<Attribute, AttributeModifier> getAttributeModifiers(List<?> data, String spellName) {
		LinkedHashMultimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();

		for (int i = 0; i < data.size(); i++) {
			Object object = data.get(i);

			switch (object) {
				case String string -> {
					String[] args = string.split(" ");
					if (args.length < 2 || args.length > 5) {
						MagicSpells.error("Invalid attribute modifier '" + string + "' - too many or too few arguments.");
						continue;
					}

					Attribute attribute = AttributeUtil.getAttribute(args[0]);
					if (attribute == null) {
						MagicSpells.error("Invalid attribute '" + args[0] + "' on attribute modifier '" + string + "'.");
						continue;
					}

					double value;
					try {
						value = Double.parseDouble(args[1]);
					} catch (NumberFormatException e) {
						MagicSpells.error("Invalid value '" + args[1] + "' on attribute modifier '" + string + "'.");
						continue;
					}

					AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
					if (args.length >= 3) {
						operation = AttributeUtil.getOperation(args[2]);

						if (operation == null) {
							MagicSpells.error("Invalid operation '" + args[2] + "' on attribute modifier '" + string + "'.");
							continue;
						}
					}

					EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
					if (args.length >= 4) {
						group = switch (args[3].toLowerCase()) {
							case "main_hand", "hand" -> EquipmentSlotGroup.MAINHAND;
							case "off_hand" -> EquipmentSlotGroup.OFFHAND;
							case "*" -> EquipmentSlotGroup.ANY;
							case String s -> EquipmentSlotGroup.getByName(s);
						};

						if (group == null) {
							MagicSpells.error("Invalid equipment slot group '" + args[3] + "' on attribute modifier '" + string + "'.");
							continue;
						}
					}

					NamespacedKey key;
					if (args.length == 5) {
						key = NamespacedKey.fromString(args[4], MagicSpells.getInstance());

						if (key == null) {
							MagicSpells.error("Invalid namespaced key '" + args[4] + "' on attribute modifier '" + string + "'.");
							continue;
						}
					} else {
						ByteArrayDataOutput output = ByteStreams.newDataOutput();
						if (spellName != null) output.writeUTF(spellName);
						output.writeInt(i);
						output.writeUTF(attribute.key().asString());
						output.writeDouble(value);
						output.writeDouble(operation.ordinal());
						output.writeUTF(group.toString());

						UUID uuid = java.util.UUID.nameUUIDFromBytes(output.toByteArray());
						key = new NamespacedKey(MagicSpells.getInstance(), uuid.toString());
					}

					modifiers.put(attribute, new AttributeModifier(key, value, operation, group));
				}
				case Map<?, ?> map -> {
					ConfigurationSection config = ConfigReaderUtil.mapToSection(map);

					String attributeString = config.getString("type");
					if (attributeString == null) {
						MagicSpells.error("No 'type' specified on attribute modifier.");
						continue;
					}

					Attribute attribute = AttributeUtil.getAttribute(attributeString);
					if (attribute == null) {
						MagicSpells.error("Invalid attribute '" + attributeString + "' specified for 'type' on attribute modifier.");
						continue;
					}

					Object amountObj = config.get("amount");
					if (!(amountObj instanceof Number amount)) {
						if (amountObj == null) MagicSpells.error("No 'amount' specified on attribute modifier.");
						else MagicSpells.error("Invalid value '" + amountObj + "' specified for 'amount' on attribute modifier.");

						continue;
					}

					String operationString = config.getString("operation");
					if (operationString == null) {
						MagicSpells.error("No 'operation' specified on attribute modifier.");
						continue;
					}

					AttributeModifier.Operation operation = AttributeUtil.getOperation(operationString);
					if (operation == null) {
						MagicSpells.error("Invalid operation '" + operationString + "' specified for 'operation' on attribute modifier.");
						continue;
					}

					String slotString = config.getString("slot");
					if (slotString == null) {
						MagicSpells.error("No 'slot' specified on attribute modifier.");
						continue;
					}

					EquipmentSlotGroup group = switch (slotString.toLowerCase()) {
						case "main_hand", "hand" -> EquipmentSlotGroup.MAINHAND;
						case "off_hand" -> EquipmentSlotGroup.OFFHAND;
						case "*" -> EquipmentSlotGroup.ANY;
						case String s -> EquipmentSlotGroup.getByName(s);
					};

					if (group == null) {
						MagicSpells.error("Invalid equipment slot group '" + slotString + "' specified for 'slot' on attribute modifier.");
						continue;
					}

					String idString = config.getString("id");
					if (idString == null) {
						MagicSpells.error("No 'id' specified on attribute modifier.");
						continue;
					}

					NamespacedKey id = NamespacedKey.fromString(idString, MagicSpells.getInstance());
					if (id == null) {
						MagicSpells.error("Invalid value '" + idString + "' specified for 'id' on attribute modifier.");
						continue;
					}

					modifiers.put(attribute, new AttributeModifier(id, amount.doubleValue(), operation, group));
				}
				default -> MagicSpells.error("Invalid attribute modifier '" + object + "'.");
			}
		}

		return modifiers;
	}

}