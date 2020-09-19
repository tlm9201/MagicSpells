package com.nisovin.magicspells.spells.passive;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.spells.PassiveSpell;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers.ResourcePackStatus;

// Trigger variable should be set to one of the following
// loaded
// declined
// failed
// accepted
public class ResourcePackListener extends PassiveListener {

	PacketListener listener;
	
	List<PassiveSpell> spellsLoaded = new ArrayList<>();
	List<PassiveSpell> spellsDeclined = new ArrayList<>();
	List<PassiveSpell> spellsFailed = new ArrayList<>();
	List<PassiveSpell> spellsAccepted = new ArrayList<>();
	
	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		addPacketListener();

		if (var == null) return;
		switch (var.toLowerCase()) {
			case "loaded":
				spellsLoaded.add(spell);
				break;
			case "declined":
				spellsDeclined.add(spell);
				break;
			case "failed":
				spellsFailed.add(spell);
				break;
			case "accepted":
				spellsAccepted.add(spell);
				break;
		}
	}
	
	void addPacketListener() {
		if (listener != null) return;
		listener = new PacketAdapter(MagicSpells.plugin, PacketType.Play.Client.RESOURCE_PACK_STATUS) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				ResourcePackStatus status = event.getPacket().getResourcePackStatus().read(0);
				switch (status) {
					case SUCCESSFULLY_LOADED:
						activate(player, spellsLoaded);
						break;
					case DECLINED:
						activate(player, spellsDeclined);
						break;
					case FAILED_DOWNLOAD:
						activate(player, spellsFailed);
						break;
					case ACCEPTED:
						activate(player, spellsAccepted);
						break;
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(listener);
	}
	
	void activate(Player player, List<PassiveSpell> spells) {
		Bukkit.getScheduler().runTask(MagicSpells.getInstance(), () -> {
			for (PassiveSpell spell : spells) {
				spell.activate(player);
			}
		});
	}

	@Override
	public void turnOff() {
		ProtocolLibrary.getProtocolManager().removePacketListener(listener);
		spellsLoaded.clear();
		spellsDeclined.clear();
		spellsFailed.clear();
		spellsAccepted.clear();
	}

}
