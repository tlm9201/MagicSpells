package com.nisovin.magicspells.handlers;

import java.util.EnumSet;
import java.util.logging.Level;

import com.nisovin.magicspells.MagicSpells;

public class DebugHandler {
	
	public static void debugEffectInfo(String s) {
		if (MagicSpells.isDebug()) MagicSpells.plugin.getLogger().log(Level.INFO, s);
	}
	
	public static void debugNull(Throwable t) {
		if (!MagicSpells.isDebugNull()) return;
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static <E extends Enum<E>> void debugBadEnumValue(Class<E> enumClass, String receivedValue) {
		MagicSpells.plugin.getLogger().log(Level.WARNING, "Bad enum value of '" + receivedValue + "' received for type '" + enumClass.getName() + "'");

		EnumSet<E> values = EnumSet.allOf(enumClass);
		if (values.size() > 100) return;
		MagicSpells.plugin.getLogger().log(Level.WARNING, "Valid enum values are: " + EnumSet.allOf(enumClass));
	}
	
	private static String throwableToString(Throwable t) {
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement e: t.getStackTrace()) {
			builder.append(e.toString());
			builder.append('\n');
		}
		return builder.toString();
	}
	
	public static void debugNumberFormat(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return;
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugIllegalState(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugGeneral(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugNoClassDefFoundError(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugIOException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugFileNotFoundException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugIllegalStateException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugIllegalArgumentException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugIllegalAccessException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugInvocationTargetException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugNoSuchMethodException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugClassNotFoundException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugSecurityException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debugNoSuchFieldException(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void debug(Throwable t) {
		if (!MagicSpells.isDebugNumberFormat()) return; //TODO setup a different config node
		MagicSpells.plugin.getLogger().log(Level.WARNING, t.toString() + '\n' + throwableToString(t));
	}
	
	public static void nullCheck(String s) {
		if (MagicSpells.isDebug()) MagicSpells.plugin.getLogger().warning(s);
	}
	
	public static void nullCheck(Throwable t) {
		nullCheck(t.toString() + '\n' + throwableToString(t));
	}
	
	public static boolean isNullCheckEnabled() {
		return MagicSpells.isDebug();
	}
	
	public static boolean isSpellPreImpactEventCheckEnabled() {
		return MagicSpells.isDebug();
	}
	
}
