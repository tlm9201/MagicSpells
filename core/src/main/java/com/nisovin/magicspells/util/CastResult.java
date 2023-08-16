package com.nisovin.magicspells.util;

import com.nisovin.magicspells.Spell.PostCastAction;

public record CastResult(PostCastAction action, SpellData data) {
}
