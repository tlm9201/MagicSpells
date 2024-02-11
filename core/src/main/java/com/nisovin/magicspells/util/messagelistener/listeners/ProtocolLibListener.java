package com.nisovin.magicspells.util.messagelistener.listeners;

import java.util.*;

import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Converters;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.PacketType.Play.Server;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.messagelistener.MessageListener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ProtocolLibListener implements MessageListener {

	private PacketListener instance;
	private Map<UUID, RecordedData> listening;

	public ProtocolLibListener() {
		instance = new PacketListener();
		ProtocolLibrary.getProtocolManager().addPacketListener(instance);
		listening = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public void addPlayer(Player player, ListenerData listenerData) {
		listening.put(player.getUniqueId(), new RecordedData(listenerData));
	}

	@Override
	public void removePlayer(Player player) {
		RecordedData recordedData = listening.remove(player.getUniqueId());
		if (recordedData == null) return;

		String variable = recordedData.listenerData.storeChatOutput();
		String message = recordedData.recorded.toString();
		MagicSpells.getVariableManager().set(variable, player.getName(), message);
	}

	@Override
	public void turnOff() {
		ProtocolLibrary.getProtocolManager().removePacketListener(instance);
		instance = null;
		listening = null;
	}

	private class PacketListener extends PacketAdapter {

		PacketListener() {
			super(MagicSpells.getInstance(), Server.SYSTEM_CHAT, Server.DISGUISED_CHAT, Server.CHAT);
		}

		@Override
		public void onPacketSending(PacketEvent event) {
			PacketContainer container = event.getPacket();
			Player player = event.getPlayer();
			RecordedData recordedData = listening.get(player.getUniqueId());
			if (recordedData == null) return;
			ListenerData listenerData = recordedData.listenerData;

			// Ignore actionbar.
			if (container.getBooleans().read(0)) return;

			event.setCancelled(listenerData.blockChatOutput());

			// Collect message if appropriate.
			if (listenerData.storeChatOutput() == null) return;

			// Sometimes the message is an Adventure component, sometimes a string.
			Component component = event.getPacket().getModifier().withType(Component.class, Converters.passthrough(Component.class)).readSafely(0);
			if (component == null && container.getStrings().size() > 0) {
				String json = container.getStrings().read(0);
				component = GsonComponentSerializer.gson().deserialize(json);
			}

			StringBuilder recorded = recordedData.recorded;
			if (!recorded.isEmpty()) recorded.append("\\n");
			recorded.append(Util.getPlainString(component));
		}

	}

	private static final class RecordedData {

		private final ListenerData listenerData;
		private final StringBuilder recorded = new StringBuilder();

		private RecordedData(ListenerData listenerData) {
			this.listenerData = listenerData;
		}

	}

}
