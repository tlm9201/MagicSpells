package com.nisovin.magicspells.spells;

import com.nisovin.magicspells.util.MagicConfig;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

public class CloseInventorySpell extends InstantSpell {

    public CloseInventorySpell(MagicConfig config, String spellName) {
        super(config, spellName);
    }

    @Override
    public PostCastAction castSpell(LivingEntity livingEntity, SpellCastState state, float power, String[] args) {
        if(state == SpellCastState.NORMAL && livingEntity instanceof Player) {
            ((Player) livingEntity).closeInventory();
        }
        return PostCastAction.HANDLE_NORMALLY;
    }
}
