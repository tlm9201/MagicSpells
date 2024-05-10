package com.nisovin.magicspells.commands;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import co.aikar.commands.HelpEntry;
import co.aikar.commands.RootCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.RegisteredCommand;

import org.bukkit.command.CommandSender;

import com.nisovin.magicspells.MagicSpells;

public class CommandHelpFilter {

	private static final Map<String, String> magicPerms = new HashMap<>();

	public static void mapPerms() {
		for (RootCommand rootCommand : MagicSpells.getCommandManager().getRegisteredRootCommands()) {
			for (RegisteredCommand<?> command : rootCommand.getSubCommands().values()) {
				HelpPermission annotation = command.getAnnotation(HelpPermission.class);
				if (annotation == null) continue;
				magicPerms.put(command.getCommand(), annotation.permission().getNode());
			}
		}
	}

	public static void filter(CommandSender sender, CommandHelp help) {
		// Filter help entries by permissions.
		Iterator<HelpEntry> iterator = help.getHelpEntries().iterator();
		while (iterator.hasNext()) {
			String perm = magicPerms.get(iterator.next().getCommand());
			if (perm == null) continue;
			if (sender.hasPermission(perm)) continue;
			iterator.remove();
		}
	}

}
