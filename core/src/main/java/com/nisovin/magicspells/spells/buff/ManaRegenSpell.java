package com.nisovin.magicspells.spells.buff;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.events.ManaChangeEvent;

public class ManaRegenSpell extends BuffSpell { 

	private final Set<UUID> players;

	private int regenModAmt;

	public ManaRegenSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		regenModAmt = getConfigInt("regen-mod-amt", 3);

		players = new HashSet<>();
	}

	@Override
	public boolean castBuff(LivingEntity entity, float power, String[] args) {
		if (!(entity instanceof Player)) return false;
		players.add(entity.getUniqueId());
		return true;
	}

	@Override
	public boolean isActive(LivingEntity entity) {
		return players.contains(entity.getUniqueId());
	}

	@Override
	public void turnOffBuff(LivingEntity entity) {
		players.remove(entity.getUniqueId());
	}

	@Override
	protected void turnOff() {
		players.clear();
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onManaRegenTick(ManaChangeEvent event) {
		Player player = event.getPlayer();
		if (isExpired(player)) {
			turnOff(player);
			return;
		}

		if (!isActive(player)) return;
		if (!event.getReason().equals(ManaChangeReason.REGEN)) return;
		
		int newAmt = event.getNewAmount() + regenModAmt;
		if (newAmt > event.getMaxMana()) newAmt = event.getMaxMana();
		else if (newAmt < 0) newAmt = 0;

		addUseAndChargeCost(player);
		event.setNewAmount(newAmt);
	}

	public Set<UUID> getPlayers() {
		return players;
	}

	public int getRegenModAmt() {
		return regenModAmt;
	}

	public void setRegenModAmt(int regenModAmt) {
		this.regenModAmt = regenModAmt;
	}

}
