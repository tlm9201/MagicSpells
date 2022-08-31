package com.nisovin.magicspells.util;

import org.bukkit.entity.Entity;

public record TargetInfo<E extends Entity>(E target, float power, boolean cancelled) {

	public boolean empty() {
		return target == null;
	}

	public boolean noTarget() {
		return cancelled || target == null;
	}

}
