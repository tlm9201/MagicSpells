package com.nisovin.magicspells.spelleffects.effecttypes;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

@Name("sound")
public class SoundEffect extends SpellEffect {

	protected ConfigData<String> sound;
	protected ConfigData<Float> pitch;
	protected ConfigData<Float> volume;
	protected ConfigData<SoundCategory> category;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		sound = ConfigDataUtil.getString(config, "sound", "entity.llama.spit");
		pitch = ConfigDataUtil.getFloat(config, "pitch", 1.0F);
		volume = ConfigDataUtil.getFloat(config, "volume", 1.0F);
		category = ConfigDataUtil.getEnum(config, "category", SoundCategory.class, SoundCategory.MASTER);
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		World world = location.getWorld();
		if (world == null) return null;

		world.playSound(location, sound.get(data), category.get(data), volume.get(data), pitch.get(data));
		return null;
	}

}
