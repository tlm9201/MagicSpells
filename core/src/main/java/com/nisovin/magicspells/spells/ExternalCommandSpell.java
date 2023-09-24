package com.nisovin.magicspells.spells;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.conversations.Prompt;
import org.bukkit.command.CommandSender;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.nisovin.magicspells.util.*;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.EffectPosition;

public class ExternalCommandSpell extends TargetedSpell implements TargetedEntitySpell {

	private static MessageBlocker messageBlocker;

	private final List<String> commandToBlock;
	private final List<String> commandToExecute;
	private final List<String> commandToExecuteLater;
	private final List<String> temporaryPermissions;

	private final ConfigData<Integer> commandDelay;

	private final boolean blockChatOutput;
	private final boolean requirePlayerTarget;
	private final ConfigData<Boolean> temporaryOp;
	private final ConfigData<Boolean> doVariableReplacement;
	private final ConfigData<Boolean> executeAsTargetInstead;
	private final ConfigData<Boolean> executeOnConsoleInstead;
	private final ConfigData<Boolean> useTargetVariablesInstead;

	private final String strBlockedOutput;
	private final String strCantUseCommand;

	private ConversationFactory convoFac;

	public ExternalCommandSpell(MagicConfig config, String spellName) {
		super(config, spellName);

		commandToBlock = getConfigStringList("command-to-block", null);
		commandToExecute = getConfigStringList("command-to-execute", null);
		commandToExecuteLater = getConfigStringList("command-to-execute-later", null);
		temporaryPermissions = getConfigStringList("temporary-permissions", null);

		commandDelay = getConfigDataInt("command-delay", 0);

		temporaryOp = getConfigDataBoolean("temporary-op", false);
		blockChatOutput = getConfigBoolean("block-chat-output", false);
		requirePlayerTarget = getConfigBoolean("require-player-target", false);
		doVariableReplacement = getConfigDataBoolean("do-variable-replacement", false);
		executeAsTargetInstead = getConfigDataBoolean("execute-as-target-instead", false);
		executeOnConsoleInstead = getConfigDataBoolean("execute-on-console-instead", false);
		useTargetVariablesInstead = getConfigDataBoolean("use-target-variables-instead", false);

		strNoTarget = getConfigString("str-no-target", "No target found.");
		strBlockedOutput = getConfigString("str-blocked-output", "");
		strCantUseCommand = getConfigString("str-cant-use-command", "&4You don't have permission to do that.");

		if (requirePlayerTarget) validTargetList = new ValidTargetList(true, false);

		if (blockChatOutput) {
			if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
				if (messageBlocker == null) messageBlocker = new MessageBlocker();
			} else {
				Prompt convoPrompt = new StringPrompt() {

					@NotNull
					@Override
					public String getPromptText(@NotNull ConversationContext context) {
						return strBlockedOutput;
					}

					@Override
					public Prompt acceptInput(@NotNull ConversationContext context, String input) {
						return Prompt.END_OF_CONVERSATION;
					}

				};

				convoFac = new ConversationFactory(MagicSpells.plugin)
					.withModality(true)
					.withFirstPrompt(convoPrompt)
					.withTimeout(1);
			}
		}
	}

	@Override
	public void turnOff() {
		if (messageBlocker == null) return;
		messageBlocker.turnOff();
		messageBlocker = null;
	}

	@Override
	public CastResult cast(SpellData data) {
		if (requirePlayerTarget) {
			TargetInfo<Player> info = getTargetedPlayer(data);
			if (info.noTarget()) return noTarget(info);
			data = info.spellData();
		}

		process(data.caster(), data.target(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public CastResult castAtEntity(SpellData data) {
		process(data.caster(), data.target(), data);
		return new CastResult(PostCastAction.HANDLE_NORMALLY, data);
	}

	@Override
	public boolean castFromConsole(CommandSender sender, String[] args) {
		if (!requirePlayerTarget) {
			process(sender, null, new SpellData(null, 1f, args));
			return true;
		}
		return false;
	}

	private void process(CommandSender sender, LivingEntity target, SpellData data) {
		boolean temporaryOp = this.temporaryOp.get(data);
		boolean executeAsTargetInstead = this.executeAsTargetInstead.get(data);
		boolean executeOnConsoleInstead = this.executeOnConsoleInstead.get(data);

		// Get actual sender
		CommandSender actualSender;
		if (executeAsTargetInstead) actualSender = target;
		else if (executeOnConsoleInstead) actualSender = Bukkit.getConsoleSender();
		else actualSender = sender;
		if (actualSender == null) return;

		// Grant permissions and op
		boolean opped = false;
		if (actualSender instanceof Player) {
			if (temporaryPermissions != null) {
				for (String perm : temporaryPermissions) {
					if (actualSender.hasPermission(perm)) continue;
					actualSender.addAttachment(MagicSpells.plugin, perm.trim(), true, 5);
				}
			}
			if (temporaryOp && !actualSender.isOp()) {
				opped = true;
				actualSender.setOp(true);
			}
		}

		// Perform commands
		try {
			if (commandToExecute != null && !commandToExecute.isEmpty()) {

				Conversation convo = null;
				if (sender instanceof Player player) {
					if (blockChatOutput && messageBlocker != null) {
						messageBlocker.addPlayer(player);
					} else if (convoFac != null) {
						convo = convoFac.buildConversation(player);
						convo.begin();
					}
				}

				int delay = 0;
				LivingEntity varOwner, varTarget;
				if (useTargetVariablesInstead.get(data)) {
					varOwner = target;
					varTarget = sender instanceof LivingEntity le ? le : null;
				} else {
					varOwner = sender instanceof LivingEntity le ? le : null;
					varTarget = target;
				}
				SpellData varData = data.builder().caster(varOwner).target(varTarget).build();

				boolean doVariableReplacement = this.doVariableReplacement.get(data);
				for (String comm : commandToExecute) {
					if (comm == null || comm.isEmpty()) continue;
					if (doVariableReplacement)
						comm = MagicSpells.doReplacements(comm, varOwner, varData);
					if (data.hasArgs()) {
						for (int i = 0; i < data.args().length; i++) {
							comm = comm.replace("%" + (i + 1), data.args()[i]);
						}
					}
					if (sender != null) comm = comm.replace("%a", sender.getName());
					if (target != null) comm = comm.replace("%t", target.getName());
					if (comm.startsWith("DELAY ")) {
						String[] split = comm.split(" ");
						delay += Integer.parseInt(split[1]);
					} else if (delay > 0) {
						final CommandSender s = actualSender;
						final String c = comm;
						MagicSpells.scheduleDelayedTask(() -> Bukkit.dispatchCommand(s, c), delay);
					} else {
						Bukkit.dispatchCommand(actualSender, comm);
					}
				}
				if (blockChatOutput && messageBlocker != null && sender instanceof Player player)
					messageBlocker.removePlayer(player);
				else if (convo != null) convo.abandon();
			}
		} catch (Exception e) {
			// Catch all exceptions to make sure we don't leave someone opped
			e.printStackTrace();
		}

		// Deop
		if (opped) actualSender.setOp(false);

		// Effects
		if (sender instanceof BlockCommandSender commandBlock) {
			playSpellEffects(EffectPosition.CASTER, commandBlock.getBlock().getLocation(), data);
		} else playSpellEffects(data);

		// Add delayed command
		if (commandToExecuteLater != null && !commandToExecuteLater.isEmpty() && !commandToExecuteLater.get(0).isEmpty()) {
			MagicSpells.scheduleDelayedTask(new DelayedCommand(sender, target, data, executeAsTargetInstead, executeOnConsoleInstead, temporaryOp), commandDelay.get(data));
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().isOp()) return;
		if (commandToBlock == null) return;
		if (commandToBlock.isEmpty()) return;
		String msg = event.getMessage();
		for (String comm : commandToBlock) {
			comm = comm.trim();
			if (comm.isEmpty()) continue;
			if (!msg.startsWith("/" + commandToBlock)) continue;

			event.setCancelled(true);
			sendMessage(strCantUseCommand, event.getPlayer(), SpellData.NULL);
			return;
		}
	}

	public boolean requiresPlayerTarget() {
		return requirePlayerTarget;
	}

	private class DelayedCommand implements Runnable {

		private final CommandSender sender;
		private final LivingEntity target;
		private final SpellData data;

		private final boolean executeOnConsoleInstead;
		private final boolean executeAsTargetInstead;
		private final boolean temporaryOp;

		private DelayedCommand(CommandSender sender, LivingEntity target, SpellData data, boolean executeAsTargetInstead, boolean executeOnConsoleInstead, boolean temporaryOp) {
			this.sender = sender;
			this.target = target;
			this.data = data;

			this.temporaryOp = temporaryOp;
			this.executeAsTargetInstead = executeAsTargetInstead;
			this.executeOnConsoleInstead = executeOnConsoleInstead;
		}

		@Override
		public void run() {
			// Get actual sender
			CommandSender actualSender;
			if (executeAsTargetInstead) actualSender = target;
			else if (executeOnConsoleInstead) actualSender = Bukkit.getConsoleSender();
			else actualSender = sender;
			if (actualSender == null) return;

			// Grant permissions
			boolean opped = false;
			if (actualSender instanceof Player) {
				if (temporaryPermissions != null) {
					for (String perm : temporaryPermissions) {
						if (actualSender.hasPermission(perm)) continue;
						actualSender.addAttachment(MagicSpells.plugin, perm, true, 5);
					}
				}
				if (temporaryOp && !actualSender.isOp()) {
					opped = true;
					actualSender.setOp(true);
				}
			}

			// Run commands
			try {
				Conversation convo = null;
				if (sender instanceof Player player) {
					if (blockChatOutput && messageBlocker != null) {
						messageBlocker.addPlayer(player);
					} else if (convoFac != null) {
						convo = convoFac.buildConversation(player);
						convo.begin();
					}
				}
				for (String comm : commandToExecuteLater) {
					if (comm == null) continue;
					if (comm.isEmpty()) continue;
					if (sender != null) comm = comm.replace("%a", sender.getName());
					if (target != null) comm = comm.replace("%t", target.getName());
					Bukkit.dispatchCommand(actualSender, comm);
				}
				if (blockChatOutput && messageBlocker != null && sender instanceof Player player)
					messageBlocker.removePlayer(player);
				else if (convo != null) convo.abandon();
			} catch (Exception e) {
				// Catch exceptions to make sure we don't leave someone opped
				e.printStackTrace();
			}

			// Deop
			if (opped) actualSender.setOp(false);

			// Graphical effect
			if (sender == null) return;
			if (sender instanceof LivingEntity caster) playSpellEffects(EffectPosition.DISABLED, caster, data);
			else if (sender instanceof BlockCommandSender commandBlock)
				playSpellEffects(EffectPosition.DISABLED, commandBlock.getBlock().getLocation(), data);
		}

	}

}
