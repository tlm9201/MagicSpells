package com.nisovin.magicspells.util.prompt;

import java.util.regex.Pattern;

import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;

public class MagicRegexPrompt extends RegexPrompt {
	
	private String promptText;
	
	private MagicPromptResponder responder;
	
	public MagicRegexPrompt(String pattern) {
		super(pattern);
	}
	
	public MagicRegexPrompt(Pattern pattern) {
		super(pattern);
	}

	@Override
	public String getPromptText(ConversationContext paramConversationContext) {
		return promptText;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext paramConversationContext, String paramString) {
		return responder.acceptValidatedInput(paramConversationContext, paramString);
	}
	
	
	public static MagicRegexPrompt fromConfigSection(ConfigurationSection section) {
		// Handle the regex
		String regexp = section.getString("regexp", null);
		if (regexp == null || regexp.isEmpty()) return null;

		MagicRegexPrompt ret = new MagicRegexPrompt(regexp);
		ret.responder = new MagicPromptResponder(section);
		ret.promptText = section.getString("prompt-text", "");
		return ret;
	}

}
