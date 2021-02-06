package com.nisovin.magicspells.util.prompt;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.configuration.ConfigurationSection;

public class MagicFixedSetPrompt extends FixedSetPrompt {

	private String promptText;
	
	private MagicPromptResponder responder;
	
	public MagicFixedSetPrompt(List<String> options) {
		super();
		super.fixedSet = new ArrayList<>(options);
	}
	
	public MagicFixedSetPrompt(String... options) {
		super(options);
	}
	
	@Override
	public String getPromptText(ConversationContext context) {
		return promptText;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		return responder.acceptValidatedInput(context, input);
	}
	
	public static MagicFixedSetPrompt fromConfigSection(ConfigurationSection section) {
		// Get the options
		List<String> options = section.getStringList("options");
		if (options.isEmpty()) return null;

		MagicFixedSetPrompt ret = new MagicFixedSetPrompt(options);
		ret.responder = new MagicPromptResponder(section);
		ret.promptText = section.getString("prompt-text", "");
		return ret;
	}
	
}
