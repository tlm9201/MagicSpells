package com.nisovin.magicspells.util.magicitems;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Objects;
import java.util.Collection;

import com.google.common.collect.Multimap;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.FireworkEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffect;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.attribute.AttributeModifier;

import com.nisovin.magicspells.util.AttributeUtil.AttributeModifierData;

public class MagicItemData {

    private EnumMap<MagicItemAttribute, Object> itemAttributes = new EnumMap<>(MagicItemAttribute.class);
    private EnumSet<MagicItemAttribute> ignoredAttributes = EnumSet.noneOf(MagicItemAttribute.class);

    public Object getAttribute(MagicItemAttribute attr) {
        return itemAttributes.get(attr);
    }

    public void setAttribute(MagicItemAttribute attr, Object obj) {
        if (obj == null) return;
        if (!attr.getDataType().isAssignableFrom(obj.getClass())) return;

        itemAttributes.put(attr, obj);
    }

    public void removeAttribute(MagicItemAttribute attr) {
        itemAttributes.remove(attr);
    }

    public boolean hasAttribute(MagicItemAttribute atr) {
        return itemAttributes.containsKey(atr);
    }

    public EnumSet<MagicItemAttribute> getIgnoredAttributes() {
        return ignoredAttributes;
    }

    public void setIgnoredAttributes(EnumSet<MagicItemAttribute> ignoredAttributes) {
        this.ignoredAttributes = ignoredAttributes;
    }

    private boolean hasEqualAttributes(MagicItemData other) {
        Multimap<Attribute, AttributeModifier> attrSelf = (Multimap<Attribute, AttributeModifier>) itemAttributes.get(MagicItemAttribute.ATTRIBUTES);
        Multimap<Attribute, AttributeModifier> attrOther = (Multimap<Attribute, AttributeModifier>) other.itemAttributes.get(MagicItemAttribute.ATTRIBUTES);

        Set<Attribute> keysSelf = attrSelf.keySet();
        Set<Attribute> keysOther = attrOther.keySet();
        if (!keysSelf.equals(keysOther)) return false;
        
        for (Attribute attr : keysSelf) {
            Collection<AttributeModifier> modsSelf = attrSelf.get(attr);
            Collection<AttributeModifier> modsOther = attrOther.get(attr);
            if (modsSelf.size() != modsOther.size()) return false;

            HashMap<AttributeModifierData, Integer> freq = new HashMap<>();
            for (AttributeModifier mod : modsSelf) {
                AttributeModifierData data = new AttributeModifierData(mod);
                Integer count = freq.get(data);

                if (count == null) count = 0;
                freq.put(data, count + 1);
            }

            for (AttributeModifier mod : modsOther) {
                AttributeModifierData data = new AttributeModifierData(mod);
                Integer count = freq.get(data);

                if (count == null) return false;
                if (count == 1) freq.remove(data);
                else freq.put(data, count - 1);
            }
        }

        return true;
    }

    public boolean matches(MagicItemData data) {
        if (this == data) return true;

        Set<MagicItemAttribute> keysSelf = itemAttributes.keySet();
        Set<MagicItemAttribute> keysOther = data.itemAttributes.keySet();

        for (MagicItemAttribute attr : keysSelf) {
            if (ignoredAttributes.contains(attr)) continue;
            if (!keysOther.contains(attr)) return false;
        }

        for (MagicItemAttribute attr : keysSelf) {
            if (ignoredAttributes.contains(attr)) continue;

            if (attr == MagicItemAttribute.ATTRIBUTES) {
                if (!hasEqualAttributes(data)) return false;
            } else if (!itemAttributes.get(attr).equals(data.itemAttributes.get(attr))) return false;
        }

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

    public enum MagicItemAttribute {

        TYPE(Material.class),
        NAME(String.class),
        AMOUNT(Integer.class),
        DURABILITY(Integer.class),
        REPAIR_COST(Integer.class),
        CUSTOM_MODEL_DATA(Integer.class),
        POWER(Integer.class),
        UNBREAKABLE(Boolean.class),
        HIDE_TOOLTIP(Boolean.class),
        FAKE_GLINT(Boolean.class),
        POTION_TYPE(PotionType.class),
        COLOR(Color.class),
        FIREWORK_EFFECT(FireworkEffect.class),
        TITLE(String.class),
        AUTHOR(String.class),
        UUID(String.class),
        TEXTURE(String.class),
        SIGNATURE(String.class),
        SKULL_OWNER(OfflinePlayer.class),
        ENCHANTMENTS(Map.class),
        LORE(List.class),
        PAGES(List.class),
        POTION_EFFECTS(List.class),
        PATTERNS(List.class),
        FIREWORK_EFFECTS(List.class),
        ATTRIBUTES(Multimap.class);

        private final Class<?> dataType;
        private final String asString;

        MagicItemAttribute(Class<?> dataType) {
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

    private String escape(String str) {
        return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        boolean previous = false;

        if (hasAttribute(MagicItemAttribute.TYPE))
            output.append(((Material) getAttribute(MagicItemAttribute.TYPE)).name());

        output.append('{');
        if (hasAttribute(MagicItemAttribute.NAME)) {
            output.append("\"name\":\"").append(escape((String) getAttribute(MagicItemAttribute.NAME))).append('"');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.AMOUNT)) {
            int amount = (int) getAttribute(MagicItemAttribute.AMOUNT);

            if (previous) output.append(',');
            output.append("\"amount\":").append(amount);
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.DURABILITY)) {
            if (previous) output.append(',');
            output.append("\"durability\":").append((int) getAttribute(MagicItemAttribute.DURABILITY));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.REPAIR_COST)) {
            if (previous) output.append(',');
            output.append("\"repaircost\":").append((int) getAttribute(MagicItemAttribute.REPAIR_COST));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.CUSTOM_MODEL_DATA)) {
            if (previous) output.append(',');
            output.append("\"custommodeldata\":").append((int) getAttribute(MagicItemAttribute.CUSTOM_MODEL_DATA));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.POWER)) {
            if (previous) output.append(',');
            output.append("\"power\":").append((int) getAttribute(MagicItemAttribute.POWER));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.UNBREAKABLE)) {
            if (previous) output.append(',');
            output.append("\"unbreakable\":").append((boolean) getAttribute(MagicItemAttribute.UNBREAKABLE));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.HIDE_TOOLTIP)) {
            if (previous) output.append(',');
            output.append("\"hidetooltip\":").append((boolean) getAttribute(MagicItemAttribute.HIDE_TOOLTIP));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.COLOR)) {
            if (previous) output.append(',');
            Color color = (Color) getAttribute(MagicItemAttribute.COLOR);
            String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            output.append("\"color\":\"").append(hex).append('"');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.POTION_TYPE)) {
            if (previous) output.append(',');
            output.append("\"potiontype\":\"").append(((PotionType) getAttribute(MagicItemAttribute.POTION_TYPE)).name()).append('"');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.FIREWORK_EFFECT)) {
            FireworkEffect effect = (FireworkEffect) getAttribute(MagicItemAttribute.FIREWORK_EFFECT);

            if (previous) output.append(',');
            output.append("\"fireworkeffect\":\"");

            output
                .append(effect.getType())
                .append(' ')
                .append(effect.hasTrail())
                .append(' ')
                .append(effect.hasFlicker());

            boolean previousColor = false;
            if (!effect.getColors().isEmpty()) {
                output.append(' ');
                for (Color color : effect.getColors()) {
                    if (previousColor) output.append(',');
                    String hex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                    output.append(hex);
                    previousColor = true;
                }

                if (!effect.getFadeColors().isEmpty()) {
                    output.append(' ');
                    previousColor = false;
                    for (Color color : effect.getFadeColors()) {
                        if (previousColor) output.append(',');
                        String hex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                        output.append(hex);
                        previousColor = true;
                    }
                }
            }

            output.append('"');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.SKULL_OWNER)) {
            if (previous) output.append(',');
            output.append("\"skullowner\":").append(((OfflinePlayer) getAttribute(MagicItemAttribute.SKULL_OWNER)).getUniqueId());
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.TITLE)) {
            if (previous) output.append(',');
            output.append("\"title\":\"").append(escape((String) getAttribute(MagicItemAttribute.TITLE))).append('"');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.AUTHOR)) {
            if (previous) output.append(',');
            output.append("\"author\":\"").append(escape((String) getAttribute(MagicItemAttribute.AUTHOR))).append('"');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.UUID)) {
            if (previous) output.append(',');
            output.append("\"uuid\":").append(((String) getAttribute(MagicItemAttribute.UUID)));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.TEXTURE)) {
            if (previous) output.append(',');
            output.append("\"texture\":").append(((String) getAttribute(MagicItemAttribute.TEXTURE)));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.SIGNATURE)) {
            if (previous) output.append(',');
            output.append("\"signature\":").append(((String) getAttribute(MagicItemAttribute.SIGNATURE)));
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.ENCHANTMENTS)) {
            Map<Enchantment, Integer> enchantments = (Map<Enchantment, Integer>) getAttribute(MagicItemAttribute.ENCHANTMENTS);

            if (previous) output.append(',');
            output.append("\"enchantments\":{");
            boolean previousEnchantment = false;
            for (Enchantment enchantment : enchantments.keySet()) {
                if (previousEnchantment) output.append(',');

                output
                    .append('"')
                    .append(enchantment.getKey().getKey())
                    .append("\":")
                    .append(enchantments.get(enchantment));

                previousEnchantment = true;
            }
            output.append('}');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.FAKE_GLINT)) {
            if (previous) output.append(',');
            output.append("\"fakeglint\":").append((boolean) getAttribute(MagicItemAttribute.FAKE_GLINT));
            previous = true;
        }
        
        if (hasAttribute(MagicItemAttribute.ATTRIBUTES)) {
            if (previous) output.append(',');
            output.append("\"attributes\":[");

            Multimap<Attribute, AttributeModifier> attributes = (Multimap<Attribute, AttributeModifier>) getAttribute(MagicItemAttribute.ATTRIBUTES);
            boolean previousAttribute = false;
            for (Map.Entry<Attribute, AttributeModifier> entries : attributes.entries()) {
                if (previousAttribute) output.append(',');

                AttributeModifier modifier = entries.getValue();

                output.append('"');
                output.append(modifier.getName());
                output.append(' ');
                output.append(modifier.getAmount());
                output.append(' ');
                output.append(modifier.getOperation().name().toLowerCase());

                EquipmentSlot slot = modifier.getSlot();
                if (slot != null) {
                    output.append(' ');
                    output.append(slot.name().toLowerCase());
                }

                output.append('"');
                previousAttribute = true;
            }

            output.append(']');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.LORE)) {
            if (previous) output.append(',');
            output.append("\"lore\":[");

            List<String> lore = (List<String>) getAttribute(MagicItemAttribute.LORE);
            boolean previousLore = false;
            for (String line : lore) {
                if (previousLore) output.append(',');
                output.append('"').append(escape(line)).append('"');
                previousLore = true;
            }

            output.append(']');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.PAGES)) {
            if (previous) output.append(',');
            output.append("\"pages\":[");

            List<String> pages = (List<String>) getAttribute(MagicItemAttribute.PAGES);
            boolean previousPages = false;
            for (String page : pages) {
                if (previousPages) output.append(',');
                output.append('"').append(escape(page)).append('"');
                previousPages = true;
            }

            output.append(']');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.PATTERNS)) {
            List<Pattern> patterns = (List<Pattern>) getAttribute(MagicItemAttribute.PATTERNS);

            if (previous) output.append(',');
            output.append("\"patterns\":[");
            boolean previousPattern = false;
            for (Pattern pattern : patterns) {
                if (previousPattern) output.append(',');

                output
                    .append('"')
                    .append(pattern.getPattern().name())
                    .append(' ')
                    .append(pattern.getColor().name())
                    .append('"');

                previousPattern = true;
            }

            output.append(']');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.POTION_EFFECTS)) {
            List<PotionEffect> effects = (List<PotionEffect>) getAttribute(MagicItemAttribute.POTION_EFFECTS);

            if (previous) output.append(',');
            output.append("\"potioneffects\":[");
            boolean previousEffect = false;
            for (PotionEffect effect : effects) {
                if (previousEffect) output.append(',');

                output
                    .append('"')
                    .append(effect.getType().getName())
                    .append(' ')
                    .append(effect.getAmplifier())
                    .append(' ')
                    .append(effect.getDuration())
                    .append('"');

                previousEffect = true;
            }

            output.append(']');
            previous = true;
        }

        if (hasAttribute(MagicItemAttribute.FIREWORK_EFFECTS)) {
            List<FireworkEffect> effects = (List<FireworkEffect>) getAttribute(MagicItemAttribute.FIREWORK_EFFECTS);

            if (previous) output.append(',');
            output.append("\"fireworkeffects\":[");
            boolean previousEffect = false;
            for (FireworkEffect effect : effects) {
                if (previousEffect) output.append(',');

                output
                    .append('"')
                    .append(effect.getType())
                    .append(' ')
                    .append(effect.hasTrail())
                    .append(' ')
                    .append(effect.hasFlicker());

                boolean previousColor = false;
                if (!effect.getColors().isEmpty()) {
                    output.append(' ');
                    for (Color color : effect.getColors()) {
                        if (previousColor) output.append(',');
                        String hex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                        output.append(hex);
                        previousColor = true;
                    }

                    if (!effect.getFadeColors().isEmpty()) {
                        output.append(' ');
                        previousColor = false;
                        for (Color color : effect.getFadeColors()) {
                            if (previousColor) output.append(',');
                            String hex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                            output.append(hex);
                            previousColor = true;
                        }
                    }
                }

                output.append('"');
                previousEffect = true;
            }

            output.append(']');
            previous = true;
        }

        if (!ignoredAttributes.isEmpty()) {
            if (previous) output.append(",");
            output.append("\"ignoredattributes\":[");

            boolean previousAttribute = false;
            for (MagicItemAttribute attr : ignoredAttributes) {
                if (previousAttribute) output.append(',');

                output
                    .append('"')
                    .append(attr.name())
                    .append('"');

                previousAttribute = true;
            }

            output.append(']');
        }

        output.append('}');

        return output.toString();
    }

}
