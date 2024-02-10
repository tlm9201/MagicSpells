package com.nisovin.magicspells.util.messagelistener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface MessageListener {

	void addPlayer(Player player, ListenerData data);

	void removePlayer(Player player);

	void turnOff();

	record ListenerData(boolean blockChatOutput, String strBlockedOutput, String storeChatOutput) {}

}
