package com.nisovin.magicspells.spells;

import org.bukkit.Location;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.Subspell;
import com.nisovin.magicspells.util.config.ConfigData;

public class LocationSpell extends InstantSpell {

	private Subspell spellToCast;
	private String spellToCastName;

	private ConfigData<String> locationData;

	public LocationSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		spellToCastName = getConfigString("spell", "");
		locationData = getConfigDataString("location", "world,0,0,0");
	}

	@Override
	public void initialize() {
		super.initialize();

		spellToCast = initSubspell(spellToCastName, "LocationSpell '" + internalName + "' has an invalid spell defined!");
	}

	@Override
	public CastResult cast(SpellData data) {
		MagicLocation location = null;
		String s = locationData.get(data);
		try {
			String[] split = s.split(",");
			String world = split[0];
			double x = Double.parseDouble(split[1]);
			double y = Double.parseDouble(split[2]);
			double z = Double.parseDouble(split[3]);
			float yaw = 0;
			float pitch = 0;
			if (split.length > 4) yaw = Float.parseFloat(split[4]);
			if (split.length > 5) pitch = Float.parseFloat(split[5]);
			location = new MagicLocation(world, x, y, z, yaw, pitch);
		} catch (Exception ignored) {}
		if (location == null) return new CastResult(PostCastAction.ALREADY_HANDLED, data);

		data = data.location(location.getLocation());
		if (spellToCast != null) spellToCast.subcast(data);
		playSpellEffects(data);

		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

}
