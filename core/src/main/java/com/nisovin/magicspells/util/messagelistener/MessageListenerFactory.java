package com.nisovin.magicspells.util.messagelistener;

import org.bukkit.Bukkit;

import com.nisovin.magicspells.util.messagelistener.listeners.*;

public class MessageListenerFactory {

	private MessageListenerFactory() {}

	public static MessageListener create() {
		boolean hasProtocolLib = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
		if (hasProtocolLib) return new ProtocolLibListener();
		else return new ConversationBlocker();
	}

}
