package com.nisovin.magicspells.util.magicitems;

import java.util.Map;
import java.util.List;
import java.util.EnumMap;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;

import com.google.common.collect.Multimap;

public class MagicItemData {

    private EnumMap<ItemAttribute, Object> itemAttributes = new EnumMap<>(ItemAttribute.class);

    public Object getItemAttribute(ItemAttribute attr) {
        return itemAttributes.get(attr);
    }

    public void setItemAttribute(ItemAttribute attr, Object obj) {
        if (obj == null) return;
        if (!attr.getDataType().isAssignableFrom(obj.getClass())) return;

        itemAttributes.put(attr, obj);
    }

    public boolean hasItemAttribute(ItemAttribute atr) {
        return itemAttributes.containsKey(atr);
    }

    public boolean conforms(MagicItemData data) {
        for (ItemAttribute attr : itemAttributes.keySet())
            if (!itemAttributes.get(attr).equals(data.itemAttributes.get(attr)))
                return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MagicItemData)) return false;

        return itemAttributes.equals(((MagicItemData) o).itemAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemAttributes);
    }

    @Override
    public MagicItemData clone() {
        MagicItemData data = new MagicItemData();

        data.itemAttributes = new EnumMap<>(itemAttributes);

        return data;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        boolean previous = false;

        if (hasItemAttribute(ItemAttribute.TYPE))
            output.append(((Material) getItemAttribute(ItemAttribute.TYPE)).name());

        output.append("{");
        if (hasItemAttribute(ItemAttribute.NAME)) {
            output.append("name:'").append(((String) getItemAttribute(ItemAttribute.NAME)).replaceAll("'", "\\\\'")).append("'");
            previous = true;
        }

        if (hasItemAttribute(ItemAttribute.AMOUNT)) {
            int amount = (int) getItemAttribute(ItemAttribute.AMOUNT);

            if (amount > 1) {
                if (previous) output.append(",");
                output.append("amount:").append(amount);
                previous = true;
            }
        }

        if (hasItemAttribute(ItemAttribute.DURABILITY)) {
            if (previous) output.append(",");
            output.append("durability:").append((int) getItemAttribute(ItemAttribute.DURABILITY));
            previous = true;
        }

        if (hasItemAttribute(ItemAttribute.CUSTOM_MODEL_DATA)) {
            int customModelData = (int) getItemAttribute(ItemAttribute.CUSTOM_MODEL_DATA);

            if (customModelData > 0) {
                if (previous) output.append(",");
                output.append("customModelData:").append(customModelData);
                previous = true;
            }
        }

        if (hasItemAttribute(ItemAttribute.UNBREAKABLE)) {
            if (previous) output.append(",");
            output.append("unbreakable:").append((boolean) getItemAttribute(ItemAttribute.UNBREAKABLE));
            previous = true;
        }

        if (hasItemAttribute(ItemAttribute.COLOR)) {
            if (previous) output.append(",");
            Color color = (Color) getItemAttribute(ItemAttribute.COLOR);
            String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            output.append("color:\"").append(hex).append("\"");
            previous = true;
        }

        if (hasItemAttribute(ItemAttribute.POTION_TYPE)) {
            if (previous) output.append(",");
            output.append("potion:\"").append(((PotionType) getItemAttribute(ItemAttribute.POTION_TYPE)).name()).append("\"");
            previous = true;
        }

        if (hasItemAttribute(ItemAttribute.TITLE)) {
            if (previous) output.append(",");
            output.append("title:\"").append((String) getItemAttribute(ItemAttribute.TITLE)).append("\"");
            previous = true;
        }

        if (hasItemAttribute(ItemAttribute.AUTHOR)) {
            if (previous) output.append(",");
            output.append("author:\"").append((String) getItemAttribute(ItemAttribute.AUTHOR)).append("\"");
            previous = true;
        }

        if (hasItemAttribute(ItemAttribute.ENCHANTMENTS)) {
            Map<Enchantment, Integer> enchantments = (Map<Enchantment, Integer>) getItemAttribute(ItemAttribute.ENCHANTMENTS);

            if (!enchantments.isEmpty()) {
                if (previous) output.append(",");
                output.append("enchants:{");
                boolean previousEnchantment = false;
                for (Enchantment enchantment : enchantments.keySet()) {
                    if (previousEnchantment) output.append(",");
                    output.append(enchantment.getKey().getKey()).append(":").append(enchantments.get(enchantment));
                    previousEnchantment = true;
                }
                output.append("}");
                previous = true;
            }
        }

        if (hasItemAttribute(ItemAttribute.LORE)) {
            List<String> lore = (List<String>) getItemAttribute(ItemAttribute.LORE);

            if (!lore.isEmpty()) {
                if (previous) output.append(",");
                output.append("lore:[");
                boolean previousLore = false;
                for (String line : lore) {
                    if (previousLore) output.append(",");
                    output.append("\"").append(line).append("\"");
                    previousLore = true;
                }
                output.append("]");
                previous = true;
            }
        }

        output.append("}");

        return output.toString();
    }

    public enum ItemAttribute {

        TYPE(Material.class),
        NAME(String.class),
        AMOUNT(Integer.class),
        DURABILITY(Integer.class),
        REPAIR_COST(Integer.class),
        CUSTOM_MODEL_DATA(Integer.class),
        POWER(Integer.class),
        UNBREAKABLE(Boolean.class),
        HIDE_TOOLTIP(Boolean.class),
        COLOR(Color.class),
        POTION_TYPE(PotionType.class),
        FIREWORK_EFFECT(FireworkEffect.class),
        SKULL_OWNER(OfflinePlayer.class),
        TITLE(String.class),
        AUTHOR(String.class),
        UUID(String.class),
        TEXTURE(String.class),
        SIGNATURE(String.class),
        ENCHANTMENTS(Map.class),
        ATTRIBUTES(Multimap.class),
        LORE(List.class),
        PAGES(List.class),
        PATTERNS(List.class),
        POTION_EFFECTS(List.class),
        FIREWORK_EFFECTS(List.class);

        private final Class<?> dataType;
        private final String asString;

        ItemAttribute(Class<?> dataType) {
            this.dataType = dataType;
            asString = name().toLowerCase().replace('_', '-');
        }

        public Class<?> getDataType() {
            return dataType;
        }

        @Override
        public String toString() {
            return asString;
        }

    }

}
