package com.nisovin.magicspells.variables.variabletypes;

import java.util.Map;
import java.util.HashMap;

import com.nisovin.magicspells.util.Name;

import org.bukkit.configuration.ConfigurationSection;

@Name("playerstring")
public class PlayerStringVariable extends PlayerVariable {

	private final Map<String, String> data;
	
	public PlayerStringVariable() {
		data = new HashMap<>();
	}
	
	@Override
	public void loadExtraData(ConfigurationSection section) {
		super.loadExtraData(section);
		defaultStringValue = section.getString("default-value", "");
	}
	
	@Override
	public String getStringValue(String player) {
		return data.getOrDefault(player, defaultStringValue);
	}
	
	@Override
	public void parseAndSet(String player, String textValue) {
		data.put(player, textValue);
	}
	
	@Override
	public void reset(String player) {
		data.remove(player);
	}
	
}
