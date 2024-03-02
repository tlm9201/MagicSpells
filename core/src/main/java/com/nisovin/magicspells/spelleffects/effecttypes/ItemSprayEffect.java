package com.nisovin.magicspells.spelleffects.effecttypes;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;

import net.kyori.adventure.util.TriState;

import com.nisovin.magicspells.util.Name;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.SpellData;
import com.nisovin.magicspells.util.config.ConfigData;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import com.nisovin.magicspells.util.config.ConfigDataUtil;

@Name("itemspray")
public class ItemSprayEffect extends SpellEffect {

	private static final List<Item> items = new ArrayList<>();

	private ConfigData<Material> material;

	private ConfigData<Double> force;

	private ConfigData<Integer> amount;
	private ConfigData<Integer> duration;

	private ConfigData<Boolean> gravity;
	private ConfigData<Boolean> removeItemFriction;
	private ConfigData<Boolean> resolveForcePerItem;

	@Override
	public void loadFromConfig(ConfigurationSection config) {
		material = ConfigDataUtil.getMaterial(config, "type", null);

		force = ConfigDataUtil.getDouble(config, "force", 1);

		amount = ConfigDataUtil.getInteger(config, "amount", 15);
		duration = ConfigDataUtil.getInteger(config, "duration", 10);

		gravity = ConfigDataUtil.getBoolean(config, "gravity", true);
		removeItemFriction = ConfigDataUtil.getBoolean(config, "remove-item-friction", false);
		resolveForcePerItem = ConfigDataUtil.getBoolean(config, "resolve-force-per-item", false);
	}

	@Override
	public Runnable playEffectLocation(Location location, SpellData data) {
		Material material = this.material.get(data);
		if (material == null) return null;
		ItemStack itemStack = new ItemStack(material);

		Location loc = location.clone().add(0, 1, 0);

		boolean resolveForcePerItem = this.resolveForcePerItem.get(data);
		double force = resolveForcePerItem ? 0 : this.force.get(data);
		int duration = this.duration.get(data);
		boolean gravity = this.gravity.get(data);
		boolean removeItemFriction = this.removeItemFriction.get(data);

		int amount = this.amount.get(data);
		for (int i = 0; i < amount; i++) {
			Item dropped = loc.getWorld().dropItem(loc, itemStack, item -> {
				double f = resolveForcePerItem ? this.force.get(data) : force;
				item.setVelocity(new Vector(
						(random.nextDouble() - 0.5d) * f,
						(random.nextDouble() - 0.5d) * f,
						(random.nextDouble() - 0.5d) * f
				));
				// Prevents merging too.
				item.setCanPlayerPickup(false);
				item.setCanMobPickup(false);
				item.setGravity(gravity);
				if (removeItemFriction) item.setFrictionState(TriState.FALSE);
			});

			items.add(dropped);
			MagicSpells.scheduleDelayedTask(() -> {
				items.remove(dropped);
				dropped.remove();
			}, duration);
		}
		return null;
	}

	@Override
	public void turnOff() {
		items.forEach(Entity::remove);
		items.clear();
	}

}
