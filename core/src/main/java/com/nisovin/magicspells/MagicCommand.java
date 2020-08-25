package com.nisovin.magicspells;

import java.util.*;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.util.regex.Pattern;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import co.aikar.commands.*;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.util.TxtUtil;
import com.nisovin.magicspells.util.RegexUtil;
import com.nisovin.magicspells.util.VariableMod;
import com.nisovin.magicspells.variables.Variable;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.handlers.MagicXpHandler;
import com.nisovin.magicspells.util.magicitems.MagicItem;
import com.nisovin.magicspells.util.magicitems.MagicItems;
import com.nisovin.magicspells.spells.TargetedEntitySpell;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.variables.PlayerStringVariable;

@CommandAlias("ms|magicspells")
public class MagicCommand extends BaseCommand {

	private static final File PLUGIN_FOLDER = MagicSpells.getInstance().getDataFolder();

	private static final Pattern QUOTATIONS_PATTERN = Pattern.compile("^[\"']|[\"']$");

	public MagicCommand() {
		PaperCommandManager commandManager = MagicSpells.getCommandManager();

		// Creating conditions.
		commandManager.getCommandConditions().addCondition("mana_is_enabled", context -> {
			if (!MagicSpells.enableManaBars()) throw new ConditionFailedException("The Mana system is not enabled.");
		});
		commandManager.getCommandConditions().addCondition("issuer_is_op", context -> {
			if (!context.getIssuer().isPlayer()) return;
			if (context.getIssuer().getPlayer().isOp()) return;
			throw new ConditionFailedException("You don't have permission to execute this command.");
		});

		// Create command completions.
		commandManager.getCommandCompletions().registerAsyncCompletion("spells", context -> {
			Set<String> spells = new HashSet<>();
			if (context.getConfig() != null && context.getConfig().equals("*")) spells.add("*");
			// Collect spell names.
			for (Spell spell : MagicSpells.spells()) {
				String name = spell.getName();
				// In case that was the ingame name, add the internal name too.
				if (!name.equals(spell.getInternalName())) {
					spells.add("\"" + Util.decolorize(name) + "\"");
					name = spell.getInternalName();
				}
				spells.add(name);
				// Add aliases.
				String[] aliases = spell.getAliases();
				if (aliases != null && aliases.length > 0) Collections.addAll(spells, aliases);
			}
			return spells;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("players+", context -> {
			Set<String> players = new HashSet<>();
			players.add("*");
			for (Player player : Bukkit.getOnlinePlayers()) {
				players.add(player.getName());
			}
			return players;
		});
		commandManager.getCommandCompletions().registerAsyncCompletion("variables", context ->
			MagicSpells.getVariableManager().getVariables().keySet()
		);
		commandManager.getCommandCompletions().registerAsyncCompletion("magic_items", context ->
			MagicItems.getMagicItemKeys()
		);
		commandManager.getCommandCompletions().registerAsyncCompletion("looking_at", context -> {
			Set<String> completions = new HashSet<>();
			if (!context.getIssuer().isPlayer()) return completions;
			String config = context.getConfig();
			if (config == null || config.isEmpty()) return completions;
			Player player = context.getPlayer();
			Block block = player.getTargetBlockExact(6, FluidCollisionMode.SOURCE_ONLY);
			if (block == null) return null;
			if (block.getType().isAir()) return completions;
			Location location = block.getLocation();
			String num = "";
			switch (config.toLowerCase()) {
				case "x":
					num += location.getX();
					break;
				case "y":
					num += location.getY();
					break;
				case "z":
					num += location.getZ();
					break;
				case "pitch":
					num += player.getLocation().getPitch();
					break;
				case "yaw":
					num += player.getLocation().getYaw();
					break;
			}
			if (!num.isEmpty()) completions.add(num);
			return completions;
		});
	}

	private static Spell getSpell(CommandIssuer issuer, String name) {
		Spell spell = MagicSpells.getSpellByInternalName(name);
		if (spell == null) {
			// Remove quotations for ingame name - previous handling.
			name = RegexUtil.replaceAll(QUOTATIONS_PATTERN, name, "");
			spell = MagicSpells.getSpellByInGameName(name);
		}
		if (spell == null) {
			issuer.sendMessage(MagicSpells.getTextColor() + "No matching spell found: '" + name + "'");
		}
		return spell;
	}

	private static Player getPlayerFromIssuer(CommandIssuer issuer) {
		if (issuer.isPlayer()) return issuer.getIssuer();
		issuer.sendMessage(MagicSpells.getTextColor() + "You must be a player in order to perform this command.");
		return null;
	}

	private static LivingEntity getEntity(String input) {
		if (input == null || input.isEmpty()) return null;
		// Try an ID.
		UUID uuid = null;
		try {
			uuid = UUID.fromString(input);
		}
		catch (IllegalArgumentException ignored) {}
		if (uuid != null) {
			Entity entity = Bukkit.getEntity(uuid);
			return entity instanceof LivingEntity ? (LivingEntity) entity : null;
		}
		// Try to find a player instead.
		Player player = Bukkit.getPlayer(input);
		return player != null && player.isOnline() ? player : null;
	}

	private static String[] getCustomArgs(String[] args, int lastParameter) {
		if (args.length <= lastParameter) return null;
		List<String> spellArgs = new ArrayList<>();
		for (String string : Arrays.copyOfRange(args, lastParameter, args.length)) {
			// Skip custom parameters.
			if (string.startsWith("-p:")) continue;

			spellArgs.add(string);
		}
		return spellArgs.toArray(new String[0]);
	}

	private static float getPowerFromArgs(String[] args) {
		float power = 1F;
		for (String string : args) {
			if (!string.startsWith("-p:")) continue;
			power = ACFUtil.parseFloat(string.substring(3), 1F);
			break;
		}
		return power;
	}

	@HelpCommand
	@Syntax("[command]")
	@Description("Display command help.")
	public void doHelp(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("reload")
	@CommandCompletion("@players @nothing")
	@Conditions("issuer_is_op")
	@Syntax("[player]")
	@Description("Reloads MagicSpells. If player is specified, then it reloads their spellbook.")
	public static void onReload(CommandIssuer issuer, String[] args) {
		MagicSpells plugin = MagicSpells.getInstance();
		if (args.length == 0) {
			plugin.unload();
			plugin.load();
			issuer.sendMessage(MagicSpells.getTextColor() + "MagicSpells plugin reloaded.");
		}
		else {
			Player player = ACFBukkitUtil.findPlayerSmart(issuer, args[0]);
			if (player == null) return;
			MagicSpells.getSpellbooks().put(player.getName(), new Spellbook(player, plugin));
			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " spellbook has been reloaded.");
		}
	}

	@Subcommand("reloadeffectlib")
	@Conditions("issuer_is_op")
	@Description("Reloads EffectLib, the shaded version inside MagicSpells.")
	public static void onReloadEffectLib(CommandIssuer issuer) {
		MagicSpells.disposeEffectlib();
		MagicSpells.setupEffectlib();
		issuer.sendMessage(MagicSpells.getTextColor() + "Effectlib reloaded.");
	}

	@Subcommand("resetcd")
	@CommandCompletion("@players+ @spells:* @nothing")
	@Conditions("issuer_is_op")
	@Syntax("[player] [spell]")
	@Description("Reset cooldown of all all players or a player for a spell or all spells.")
	public static void onResetCD(CommandIssuer issuer, String[] args) {
		args = Util.splitParams(args);
		Player player = null;
		Spell spell = null;

		if (args.length > 0) {
			if (!args[0].equals("*")) {
				player = ACFBukkitUtil.findPlayerSmart(issuer, args[0]);
				if (player == null) return;
			}

			if (args.length > 1 && !args[1].equals("*")) {
				spell = getSpell(issuer, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
				if (spell == null) return;
			}
		}

		Set<Spell> spells = new HashSet<>();
		if (spell == null) spells.addAll(MagicSpells.getSpells().values());
		else spells.add(spell);
		for (Spell s : spells) {
			if (player == null) s.getCooldowns().clear();
			else s.setCooldown(player, 0);
		}
		issuer.sendMessage(MagicSpells.getTextColor() + "Cooldowns reset" + (player == null ? "" : " for " + player.getName()) + (spell == null ? "" : " for spell " + spell.getName()) + ".");
	}

	@Subcommand("mana")
	@Conditions("mana_is_enabled")
	public class ManaCommands extends BaseCommand {

		@Subcommand("show")
		@CommandAlias("mana")
		//@Conditions("issuer_is_op")
		@Description("Display your mana.")
		public void onShow(CommandIssuer issuer) {
			Player player = getPlayerFromIssuer(issuer);
			if (player == null) return;
			MagicSpells.getManaHandler().showMana(player, true);
		}

		@Subcommand("reset")
		@CommandCompletion("@players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("[player]")
		@Description("Reset mana of yourself or another player.")
		public void onReset(CommandIssuer issuer, @Optional OnlinePlayer onlinePlayer) {
			Player player = onlinePlayer == null ? getPlayerFromIssuer(issuer) : onlinePlayer.getPlayer();
			if (player == null) return;
			MagicSpells.getManaHandler().createManaBar(player);
			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " mana was reset.");
		}

		@Subcommand("setmax")
		@CommandCompletion("@players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("[player] <amount>")
		@Description("Set the max mana of yourself or another player.")
		public void onSetMax(CommandIssuer issuer, String[] args) {
			if (args.length < 1) throw new InvalidCommandArgument();
			int amount;
			Player player = null;
			if (ACFUtil.isInteger(args[0])) {
				amount = Integer.parseInt(args[0]);
			}
			else {
				if (args.length < 2) throw new InvalidCommandArgument();
				player = ACFBukkitUtil.findPlayerSmart(issuer, args[0]);
				if (player == null) throw new InvalidCommandArgument();

				if (!ACFUtil.isInteger(args[1])) throw new InvalidCommandArgument();
				amount = Integer.parseInt(args[1]);
			}
			if (player == null) player = getPlayerFromIssuer(issuer);
			if (player == null) return;

			MagicSpells.getManaHandler().setMaxMana(player, amount);
			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " max mana set to " + amount + ".");
		}

		@Subcommand("add")
		@CommandCompletion("@players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("[player] <amount>")
		@Description("Add mana to yourself or another player.")
		public void onAdd(CommandIssuer issuer, String[] args) {
			if (args.length < 1) throw new InvalidCommandArgument();
			int amount;
			Player player = null;
			if (ACFUtil.isInteger(args[0])) {
				amount = Integer.parseInt(args[0]);
			}
			else {
				if (args.length < 2) throw new InvalidCommandArgument();
				player = ACFBukkitUtil.findPlayerSmart(issuer, args[0]);
				if (player == null) throw new InvalidCommandArgument();

				if (!ACFUtil.isInteger(args[1])) throw new InvalidCommandArgument();
				amount = Integer.parseInt(args[1]);
			}
			if (player == null) player = getPlayerFromIssuer(issuer);
			if (player == null) return;

			MagicSpells.getManaHandler().addMana(player, amount, ManaChangeReason.OTHER);
			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " mana was modified by " + amount + ".");
		}

		@Subcommand("set")
		@CommandCompletion("@players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("[player] <amount>")
		@Description("Set your or another player's mana to a new value.")
		public void onSet(CommandIssuer issuer, String[] args) {
			if (args.length < 1) throw new InvalidCommandArgument();
			int amount;
			Player player = null;
			if (ACFUtil.isInteger(args[0])) {
				amount = Integer.parseInt(args[0]);
			}
			else {
				if (args.length < 2) throw new InvalidCommandArgument();
				player = ACFBukkitUtil.findPlayerSmart(issuer, args[0]);
				if (player == null) throw new InvalidCommandArgument();

				if (!ACFUtil.isInteger(args[1])) throw new InvalidCommandArgument();
				amount = Integer.parseInt(args[1]);
			}
			if (player == null) player = getPlayerFromIssuer(issuer);
			if (player == null) return;

			MagicSpells.getManaHandler().setMana(player, amount, ManaChangeReason.OTHER);
			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " mana was set to " + amount + ".");
		}

		@Subcommand("updaterank")
		@CommandCompletion("@players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("[player]")
		@Description("Update your or another player's mana rank.")
		public void onUpdateManaRank(CommandIssuer issuer, @Optional OnlinePlayer onlinePlayer) {
			Player player = onlinePlayer == null ? getPlayerFromIssuer(issuer) : onlinePlayer.getPlayer();
			if (player == null) return;
			boolean updated = MagicSpells.getManaHandler().updateManaRankIfNecessary(player);
			MagicSpells.getManaHandler().showMana(player);
			String status = updated ? "updated" : "already correct";
			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " mana rank was " + status + ".");
		}

	}

	@Subcommand("variable")
	public class VariableCommands extends BaseCommand {

		@Subcommand("show")
		@CommandCompletion("@variables @players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("<variable> [player]")
		@Description("Display value of a variable.")
		public void onShowVariable(CommandIssuer issuer, String[] args) {
			Variable variable;
			Player player;
			if (args.length == 0) throw new InvalidCommandArgument();

			variable = MagicSpells.getVariableManager().getVariable(args[0]);
			if (variable == null) throw new ConditionFailedException("No matching variable found: '" + args[0] + "'");

			if (args.length == 1) player = getPlayerFromIssuer(issuer);
			else player = ACFBukkitUtil.findPlayerSmart(issuer, args[1]);
			if (player == null) return;

			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " variable value for " + args[0] + " is: " + variable.getStringValue(player));
		}

		@Subcommand("modify")
		@CommandCompletion("@variables @players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("<variable> <player> <varMod>")
		@Description("Modify a variable's value.")
		public void onModifyVariable(CommandIssuer issuer, String[] args) {
			if (args.length < 3) throw new InvalidCommandArgument();

			String variableName = args[0];
			Variable variable = MagicSpells.getVariableManager().getVariable(variableName);
			if (variable == null) throw new ConditionFailedException("No matching variable found: '" + variableName + "'");

			Player player = null;
			if (ACFBukkitUtil.isValidName(args[1])) {
				player = Bukkit.getPlayer(args[1]);
				if (player == null || !player.isOnline()) throw new ConditionFailedException("No matching player found: '" + args[1] + "'");
			}
			String playerName = player == null ? "-" : player.getName();

			VariableMod variableMod = new VariableMod(args[2]);
			VariableMod.Operation op = variableMod.getOperation();
			String oldValue = MagicSpells.getVariableManager().getStringValue(variableName, playerName);
			if (op.equals(VariableMod.Operation.SET) && variable instanceof PlayerStringVariable) {
				MagicSpells.getVariableManager().set(variableName, playerName, variableMod.getValue());
			}
			else {
				double value = variableMod.getValue(player, null);
				MagicSpells.getVariableManager().set(variableName, playerName, op.applyTo(variable.getValue(playerName), value));
			}

			String message = player == null ? "Value" : TxtUtil.getPossessiveName(playerName) + " value";
			issuer.sendMessage(MagicSpells.getTextColor() + message + " of '" + variableName + "' was modified: '" + oldValue + "' to '" + MagicSpells.getVariableManager().getStringValue(variableName, playerName) + "'.");
		}

	}

	@Subcommand("magicitem")
	@CommandCompletion("@magic_items @players @nothing")
	@Conditions("issuer_is_op")
	@Syntax("<magicItem> [amount] [player]")
	@Description("Give a user a Magic Item.")
	public static void onMagicItem(CommandIssuer issuer, String[] args) {
		if (args.length == 0) throw new InvalidCommandArgument();
		MagicItem magicItem = MagicItems.getMagicItemByInternalName(args[0]);
		if (magicItem == null) throw new ConditionFailedException("No matching Magic Item found: '" + args[0] + "'");

		int amount = 1;
		Player player = null;
		if (args.length > 1) {
			if (ACFUtil.isInteger(args[1])) {
				amount = Integer.parseInt(args[1]);
			}
			else {
				player = ACFBukkitUtil.findPlayerSmart(issuer, args[1]);
				if (player == null) throw new InvalidCommandArgument();
			}

			if (args.length > 2) {
				player = ACFBukkitUtil.findPlayerSmart(issuer, args[2]);
				if (player == null) return;
			}
		}
		if (player == null) player = getPlayerFromIssuer(issuer);
		if (player == null) return;

		ItemStack item = magicItem.getItemStack();
		item.setAmount(amount);
		player.getInventory().addItem(item);
		issuer.sendMessage(MagicSpells.getTextColor() + player.getName() + " received a magic item (" + args[0] + " x" + amount + ").");
	}

	@Subcommand("util")
	public class UtilCommands extends BaseCommand {

		@Subcommand("download")
		@Conditions("issuer_is_op")
		@Syntax("<url> <fileName>")
		@Description("Download a file from a specified URL and save it with the specified name. (The spell file prefix is not automatically added.)")
		public void onDownload(CommandIssuer issuer, String[] args) {
			if (args.length < 2) throw new InvalidCommandArgument();
			String fileName = args[1] + ".yml";
			File file = new File(PLUGIN_FOLDER, fileName);
			if (file.exists()) throw new ConditionFailedException("The file '" + fileName + "' already exists!");
			boolean downloaded = Util.downloadFile(args[0], file);
			if (downloaded) issuer.sendMessage(MagicSpells.getTextColor() + "SUCCESS! You will need reload the plugin to load new spells.");
			else throw new ConditionFailedException("The file could not be downloaded.");
		}

		@Subcommand("update")
		@Conditions("issuer_is_op")
		@Syntax("<url> <fileName>")
		@Description("This behaves the same as the download command, except it can overwrite existing files.")
		public void onUpdate(CommandIssuer issuer, String[] args) {
			if (args.length < 2) throw new InvalidCommandArgument();
			String fileName = args[1];
			File updateFile = new File(PLUGIN_FOLDER, "update-" + fileName + ".yml");
			boolean downloaded = Util.downloadFile(args[0], updateFile);
			if (!downloaded) throw new ConditionFailedException("Update file failed to download.");
			// Delete the existing file.
			File oldFile = new File(PLUGIN_FOLDER, fileName + ".yml");
			if (oldFile.exists()) {
				boolean deleted = oldFile.delete();
				if (!deleted) throw new ConditionFailedException("Old file could not be deleted. Aborting update, please delete the update file: '" + updateFile.getName() + "'");
				issuer.sendMessage(MagicSpells.getTextColor() + "Old file successfully deleted.");
			}
			// Rename the update to the original file's name.
			boolean renamed = updateFile.renameTo(new File(PLUGIN_FOLDER, fileName + ".yml"));
			if (!renamed) throw new ConditionFailedException("Failed to rename the update file, update failed");
			issuer.sendMessage(MagicSpells.getTextColor() + "Successfully renamed the update file to '" + fileName + ".yml'. You will need reload the plugin to load new spells.");
		}

		@Subcommand("saveskin")
		@CommandCompletion("@players @nothing")
		@Conditions("issuer_is_op")
		@Syntax("[player]")
		@Description("Save a player's current skin data to a readable file.")
		public void onSaveSkin(CommandIssuer issuer, @Optional Player player) {
			if (player == null) player = getPlayerFromIssuer(issuer);
			if (player == null) return;
			String data = MagicSpells.getVolatileCodeHandler().getSkinData(player);

			File folder = new File(MagicSpells.getInstance().getDataFolder(), "skins");
			if (!folder.exists()) folder.mkdir();
			File file = new File(folder, System.currentTimeMillis() + ".yml");
			try {
				Writer writer = new FileWriter(file);
				writer.write(data);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			issuer.sendMessage(MagicSpells.getTextColor() + TxtUtil.getPossessiveName(player.getName()) + " skin was saved.");
		}

	}

	@Subcommand("profilereport")
	@Conditions("issuer_is_op")
	@Description("Save profile report to a file.")
	public static void onProfiler(CommandIssuer issuer) {
		MagicSpells.profilingReport();
		issuer.sendMessage(MagicSpells.getTextColor() + "Created profiling report.");
	}

	@Subcommand("debug")
	@Conditions("issuer_is_op")
	@Description("Toggle MagicSpells debug mode.")
	public static void onDebug(CommandIssuer issuer) {
		MagicSpells.setDebug(!MagicSpells.isDebug());
		issuer.sendMessage(MagicSpells.getTextColor() + "MagicSpells debug mode " + (MagicSpells.isDebug() ? "enabled" : "disabled") + ".");
	}

	@Subcommand("magicxp")
	@CommandAlias("magicxp")
	//@Conditions("issuer_is_op")
	@Description("Display your MagicXp.")
	public void onShow(CommandIssuer issuer) {
		Player player = getPlayerFromIssuer(issuer);
		if (player == null) return;
		MagicXpHandler xpHandler = MagicSpells.getMagicExpHandler();
		if (xpHandler == null) throw new ConditionFailedException("The ManaXp system is not enabled.");
		xpHandler.showXpInfo(player);
	}

	@Subcommand("cast")
	public class CastCommands extends BaseCommand {

		@Subcommand("self")
		@CommandAlias("c|cast")
		@CommandCompletion("@spells -p: @nothing")
		//@Conditions("issuer_is_op")
		@Syntax("<spell> [-p:(power)] [spellArgs]")
		@Description("Cast a spell. (You can optionally define power: -p:1.0)")
		public void onCastSelf(CommandIssuer issuer, String[] args) {
			args = Util.splitParams(args);
			if (args[0].isEmpty()) throw new InvalidCommandArgument();
			Spell spell = getSpell(issuer, args[0]);
			if (spell == null) return;
			float power = getPowerFromArgs(args);
			String[] spellArgs = getCustomArgs(args, 1);

			CommandSender sender = issuer.getIssuer();
			// Console
			if (sender instanceof ConsoleCommandSender) {
				boolean casted = spell.castFromConsole(issuer.getIssuer(), spellArgs);
				if (casted) issuer.sendMessage("Spell casted!");
				else issuer.sendMessage("Spell failed to cast.");
				return;
			}
			// Player
			if (sender instanceof Player) {
				Player player = ((Player) sender).getPlayer();
				if (player == null) return;
				if (!spell.canCastByCommand()) {
					MagicSpells.sendMessage(MagicSpells.getTextColor() + "You cannot cast this spell by commands.", player, null);
					return;
				}
				if ((spell.isHelperSpell() && !MagicSpells.getSpellbook(player).hasSpell(spell))) {
					MagicSpells.sendMessage(MagicSpells.getTextColor() + MagicSpells.getStrUnknownSpell(), player, null);
					return;
				}
				if (!spell.isValidItemForCastCommand(player.getInventory().getItemInMainHand())) {
					MagicSpells.sendMessage(spell.getStrWrongCastItem(), player, null);
					return;
				}
				spell.cast(player, power, spellArgs);
				return;
			}
			// LivingEntity
			if (sender instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) sender;
				if (!spell.canCastByCommand()) return;
				if (spell.isHelperSpell() && !livingEntity.isOp()) return;
				if (!spell.isValidItemForCastCommand(livingEntity.getActiveItem())) return;
				spell.cast(livingEntity, power, spellArgs);
			}
		}

		@Subcommand("as")
		@CommandCompletion("@players @spells -p: @nothing")
		@Conditions("issuer_is_op")
		@Syntax("<player/UUID> <spell> (-p:[power]) [spellArgs]")
		@Description("Force a player to cast a spell.")
		public void onCastAs(CommandIssuer issuer, String[] args) {
			args = Util.splitParams(args);
			if (args.length < 2) throw new InvalidCommandArgument();
			LivingEntity target = getEntity(args[0]);
			if (target == null) throw new ConditionFailedException("Entity not found.");
			Spell spell = getSpell(issuer, args[1]);
			if (spell == null) return;
			float power = getPowerFromArgs(args);
			String[] spellArgs = getCustomArgs(args, 2);
			spell.cast(target, power, spellArgs);
		}

		@Subcommand("on")
		@CommandCompletion("@players @spells @nothing")
		@Conditions("issuer_is_op")
		@Syntax("<player/UUID> <spell>")
		@Description("Cast a spell on an entity.")
		public void onCastOn(CommandIssuer issuer, String[] args) {
			args = Util.splitParams(args);
			if (args.length < 2) throw new InvalidCommandArgument();
			LivingEntity target = getEntity(args[0]);
			if (target == null) throw new ConditionFailedException("Entity not found.");
			Spell spell = getSpell(issuer, args[1]);
			if (spell == null) return;
			if (!(spell instanceof TargetedEntitySpell)) {
				throw new ConditionFailedException("Spell is not a targeted entity spell.");
			}

			TargetedEntitySpell newSpell = ((TargetedEntitySpell) spell);
			boolean casted;
			// Handle with or without caster.
			if (issuer.getIssuer() instanceof LivingEntity) casted = newSpell.castAtEntity(issuer.getIssuer(), target, 1F);
			else casted = newSpell.castAtEntity(target, 1F);
			if (!casted) throw new ConditionFailedException("Spell probably cannot be cast from console.");
		}

		@Subcommand("at")
		@CommandCompletion("@spells @worlds @looking_at:X @looking_at:Y @looking_at:Z @looking_at:pitch @looking_at:yaw @nothing")
		@Conditions("issuer_is_op")
		@Syntax("<spell> [world] <x> <y> <z> [pitch] [yaw]")
		@Description("Cast a spell on an entity.")
		public void onCastAt(CommandIssuer issuer, String[] args) {
			args = Util.splitParams(args);
			if (args.length < 4) throw new InvalidCommandArgument();
			Spell spell = getSpell(issuer, args[0]);
			if (spell == null) return;
			if (!(spell instanceof TargetedEntitySpell)) {
				throw new ConditionFailedException("Spell is not a targeted location spell.");
			}

			World world = null;
			float pitch = 0, yaw = 0;
			int i = 0;
			// Has world parameter.
			if (!ACFUtil.isDouble(args[1])) {
				if (args.length < 5) throw new InvalidCommandArgument();
				world = Bukkit.getWorld(args[1]);
				i = 1;
			}
			// Use issuer's world.
			else if (issuer.getIssuer() instanceof LivingEntity) {
				Location location = ((LivingEntity) issuer.getIssuer()).getLocation();
				world = location.getWorld();
				// If only coordinates were specified.
				if (args.length < 5) {
					pitch = location.getPitch();
					yaw = location.getYaw();
				}
			}
			// This fails if the world wasn't specified and the issuer is console, or if the world was invalid.
			if (world == null) throw new ConditionFailedException("No world found.");

			// Parse coordinates.
			if (!ACFUtil.isDouble(args[1 + i])) throw new InvalidCommandArgument();
			if (!ACFUtil.isDouble(args[2 + i])) throw new InvalidCommandArgument();
			if (!ACFUtil.isDouble(args[3 + i])) throw new InvalidCommandArgument();
			double x = ACFUtil.parseDouble(args[1 + i]);
			double y = ACFUtil.parseDouble(args[2 + i]);
			double z = ACFUtil.parseDouble(args[3 + i]);
			if (args.length > 4 + i) {
				if (!ACFUtil.isFloat(args[4 + i])) throw new InvalidCommandArgument();
				pitch = ACFUtil.parseFloat(args[4 + i]);
			}
			if (args.length > 5 + i) {
				if (!ACFUtil.isFloat(args[5 + i])) throw new InvalidCommandArgument();
				yaw = ACFUtil.parseFloat(args[5 + i]);
			}
			Location location = new Location(world, x, y, z, pitch, yaw);

			TargetedLocationSpell newSpell = ((TargetedLocationSpell) spell);
			boolean casted;
			// Handle with or without caster.
			if (issuer.getIssuer() instanceof LivingEntity) casted = newSpell.castAtLocation(issuer.getIssuer(), location, 1F);
			else casted = newSpell.castAtLocation(location, 1F);
			if (!casted) throw new ConditionFailedException("Spell probably cannot be cast from console.");
		}

	}

}
