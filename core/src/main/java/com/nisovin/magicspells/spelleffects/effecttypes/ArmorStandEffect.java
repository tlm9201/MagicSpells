package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.EntityData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import org.bukkit.util.EulerAngle;

public class ArmorStandEffect extends SpellEffect {

	public static final String ENTITY_TAG = "MS_ARMOR_STAND";

	private EntityData entityData;

	private String strMagicItem;
	private String customName;

	private ItemStack headItem;
	private ItemStack mainhandItem;
	private ItemStack offhandItem;

	private boolean gravity;
	private boolean customNameVisible;

	private EulerAngle headPose;
	private EulerAngle mainhandPose;
	private EulerAngle offhandPose;

	private String[] poseArray;

	@Override
	protected void loadFromConfig(ConfigurationSection config) {
		ConfigurationSection section = config.getConfigurationSection("armorstand");
		if (section == null) return;

		entityData = new EntityData(section);
		entityData.setEntityType(EntityType.ARMOR_STAND);

		gravity = section.getBoolean("gravity", false);
		customNameVisible = section.getBoolean("name-visible", false);

		customName = section.getString("custom-name", "");

		poseArray = section.getString("head-pose", "0,0,0").split(",");
		headPose = new EulerAngle(Integer.parseInt(poseArray[0]), Integer.parseInt(poseArray[1]), Integer.parseInt(poseArray[2]));
		poseArray = section.getString("mainhand-pose", "0,0,0").split(",");
		mainhandPose = new EulerAngle(Integer.parseInt(poseArray[0]), Integer.parseInt(poseArray[1]), Integer.parseInt(poseArray[2]));
		poseArray = section.getString("offhand-pose", "0,0,0").split(",");
		offhandPose = new EulerAngle(Integer.parseInt(poseArray[0]), Integer.parseInt(poseArray[1]), Integer.parseInt(poseArray[2]));

		strMagicItem = section.getString("head", "");
		MagicItem magicItem = MagicItems.getMagicItemFromString(strMagicItem);
		if (magicItem != null) headItem = magicItem.getItemStack();

		strMagicItem = section.getString("mainhand", "");
		magicItem = MagicItems.getMagicItemFromString(strMagicItem);
		if (magicItem != null) mainhandItem = magicItem.getItemStack();

		strMagicItem = section.getString("offhand", "");
		magicItem = MagicItems.getMagicItemFromString(strMagicItem);
		if (magicItem != null) offhandItem = magicItem.getItemStack();

	}

	@Override
	protected ArmorStand playArmorStandEffectLocation(Location location) {
		ArmorStand armorStand = (ArmorStand) entityData.spawn(location);
		armorStand.addScoreboardTag(ENTITY_TAG);
		armorStand.setGravity(gravity);
		armorStand.setSilent(true);
		armorStand.setCustomName(customName);
		armorStand.setCustomNameVisible(customNameVisible);
		armorStand.setHeadPose(headPose);
		armorStand.setRightArmPose(mainhandPose);
		armorStand.setLeftArmPose(offhandPose);

		if (headItem != null) armorStand.setItem(EquipmentSlot.HEAD, headItem);
		if (mainhandItem != null) armorStand.setItem(EquipmentSlot.HAND, mainhandItem);
		if (offhandItem != null) armorStand.setItem(EquipmentSlot.OFF_HAND, offhandItem);
		return armorStand;
	}

}
