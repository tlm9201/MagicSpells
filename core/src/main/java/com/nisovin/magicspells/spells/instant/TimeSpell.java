package com.nisovin.magicspells.spells.instant;

import com.nisovin.magicspells.MagicSpells;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.CastResult;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.InstantSpell;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spells.TargetedLocationSpell;

public class TimeSpell extends InstantSpell implements TargetedLocationSpell {

	private final ConfigData<Integer> timeToSet;

	private String strAnnounce;
		
	public TimeSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		timeToSet = getConfigDataInt("time-to-set", 0);
		strAnnounce = getConfigString("str-announce", "The sun suddenly appears in the sky.");
	}

	@Override
	public CastResult cast(SpellData data) {
		setTime(data.caster().getWorld(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtLocation(SpellData data) {
		setTime(data.location().getWorld(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	private void setTime(World world, SpellData data) {
		Bukkit.getGlobalRegionScheduler().run(MagicSpells.getInstance(), (task) -> world.setTime(timeToSet.get(data)));
		for (Player p : world.getPlayers()) sendMessage(strAnnounce, p, data);
		playSpellEffects(data);
	}

	public String getStrAnnounce() {
		return strAnnounce;
	}

	public void setStrAnnounce(String strAnnounce) {
		this.strAnnounce = strAnnounce;
	}

}
