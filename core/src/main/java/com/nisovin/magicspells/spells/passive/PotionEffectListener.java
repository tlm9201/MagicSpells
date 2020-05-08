package com.nisovin.magicspells.spells.passive;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.*;

import java.util.*;

public class PotionEffectListener extends PassiveListener {

    private Set<potionTrigger> spells = new HashSet<>();

    @Override
    public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
        List<PotionEffectType> types = new ArrayList<PotionEffectType>(); //Using lists instead of individual entries
        List<Action> actions = new ArrayList<Action>();
        List<Cause> causes = new ArrayList<Cause>();

        if(var != null && !var.isEmpty()) {
            var = var.toUpperCase(); //All parameters need to be uppercase anyway, and there's no need to cap asterisks.
            String[] splits = var.split(" ");
            if (!splits[0].equals("*")) { //Asterisks are wildcards, for when you want the parameter to pass on *any* value
                for (String s : splits[0].split(",")) { //Each parameter can accept a list of options, separated by commas
                    if (PotionEffectType.getByName(s) != null) { types.add(PotionEffectType.getByName(s)); }
                    else MagicSpells.error("PotionEffect Passive " + spell.getInternalName() + " has an invalid effect defined: " + s + "!");
                }
            } else types = Arrays.asList(PotionEffectType.values()); //It's dirty, but it works. If a wildcard is used, dump every value into the list.

            if (splits.length > 1 && !splits[1].equals("*")) {
                for (String s : splits[1].split(",")) {
                    try { actions.add(Action.valueOf(s)); }
                    catch (IllegalArgumentException e) {MagicSpells.error("PotionEffect Passive " + spell.getInternalName() + " has an invalid action defined: " + s + "!");}
                }
            } else actions = Arrays.asList(Action.values());

            if (splits.length > 1 && !splits[2].equals("*")) {
                for (String s : splits[2].split(",")) {
                    try { causes.add(Cause.valueOf(s)); }
                    catch (IllegalArgumentException e) {MagicSpells.error("PotionEffect Passive " + spell.getInternalName() + " has an invalid cause defined: " + s + "!");}
                }
            } else causes = Arrays.asList(Cause.values());
        }
        spells.add(new potionTrigger(spell, types, actions, causes));
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Spellbook spellbook = MagicSpells.getSpellbook(player);
        for (PotionEffectListener.potionTrigger trigger : spells) {
            if (!isCancelStateOk(trigger.spell, event.isCancelled())) continue;
            if (!spellbook.hasSpell(trigger.spell)) continue;
            if (!trigger.actions.contains(event.getAction()) || !trigger.causes.contains(event.getCause())) continue;
            PotionEffectType thisEffect = null;
            switch (event.getAction()) { //The effect used by the event is referenced differently based on the action, so unfortunately this is needed
                case ADDED:
                    thisEffect = event.getNewEffect().getType();
                    break;
                case CHANGED:
                    thisEffect = event.getModifiedType();
                    break;
                case REMOVED:
                case CLEARED:
                    thisEffect = event.getOldEffect().getType();
                    break;
            }
            if (thisEffect.equals(null) || !trigger.types.contains(thisEffect)) continue;
            boolean casted = trigger.spell.activate(player);
            if (!PassiveListener.cancelDefaultAction(trigger.spell, casted)) continue;
            event.setCancelled(true);
        }
    }

    private static class potionTrigger {
        PassiveSpell spell;
        List<PotionEffectType> types;
        List<Action> actions;
        List<Cause> causes;

        potionTrigger(PassiveSpell spell, List<PotionEffectType> types, List<Action> actions, List<Cause> causes) {
            this.spell = spell;
            this.types = types;
            this.actions = actions;
            this.causes = causes;
        }
    }
}
