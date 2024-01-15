package com.nisovin.magicspells.castmodifiers.conditions;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.MagicLocation;
import com.nisovin.magicspells.handlers.DebugHandler;
import com.nisovin.magicspells.castmodifiers.Condition;

/*
 * Format: side;world,x,y,z,line__1\nline__2
 * "side" is optional and can be "front" (def) or "back".
 * world,x,y,z is optional, "x,y,z" must be integers.
 * "lines" should follow strict MiniMessage format.
 */
public class SignTextCondition extends Condition {

	private static final Pattern FORMAT = Pattern.compile("(?:(?<side>front|back);)?(?:(?<world>[^,]+),(?<x>-?\\d+),(?<y>-?\\d+),(?<z>-?\\d+),)?(?<lines>.+)", Pattern.DOTALL);

	private Side side = Side.FRONT;
	private MagicLocation location;
	private final List<String> text = new ArrayList<>();

	@Override
	public boolean initialize(@NotNull String var) {
		Matcher matcher = FORMAT.matcher(var);
		if (!matcher.find()) return false;
		String sideName = matcher.group("side");
		String world = matcher.group("world");
		String x = matcher.group("x");
		String y = matcher.group("y");
		String z = matcher.group("z");
		String lines = matcher.group("lines");

		if (sideName != null && sideName.equals("back")) side = Side.BACK;
		if (world != null && x != null && y != null && z != null) {
			try {
				location = new MagicLocation(world, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
			} catch (NumberFormatException e) {
				DebugHandler.debugNumberFormat(e);
				return false;
			}
		}
		for (String line : lines.split("\\\\n|\\n")) {
			text.add(line.replaceAll("__", " "));
		}
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return checkSignText(caster.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkSignText(target.getLocation());
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return checkSignText(location);
	}

	public boolean checkSignText(Location targetedLocation) {
		Location signLocation = location == null ? targetedLocation : location.getLocation();
		Block block = signLocation.getBlock();
		if (!block.getType().name().contains("SIGN")) return false;

		List<Component> lines = ((Sign) block.getState()).getSide(side).lines();
		for (int i = 0; i < lines.size(); i++) {
			String signLine = Util.getStrictStringFromComponent(lines.get(i));
			String checkLine = text.size() - 1 >= i ? text.get(i) : "";
			if (signLine.equals(checkLine)) continue;
			return false;
		}

		return true;
	}

}
