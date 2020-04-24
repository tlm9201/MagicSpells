package com.nisovin.magicspells.spells.targeted;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.spells.TargetedEntitySpell;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

public class CloseInventorySpell extends TargetedSpell implements TargetedEntitySpell {

    private final int delay;

    public CloseInventorySpell(MagicConfig config, String spellName) {
        super(config, spellName);
        delay = getConfigInt("delay", 0);
    }

    @Override
    public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
        if(state == SpellCastState.NORMAL) close(livingEntity);
        return PostCastAction.HANDLE_NORMALLY;
    }

    @Override
    public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
        return close(target);
    }

    @Override
    public boolean castAtEntity(LivingEntity target, float power) {
        return close(target);
    }

    private boolean close(LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player)) return false;
        Player player = (Player) livingEntity;
        if (delay > 0) MagicSpells.scheduleDelayedTask(player::closeInventory, delay);
        else player.closeInventory();
        return true;
    }
}
