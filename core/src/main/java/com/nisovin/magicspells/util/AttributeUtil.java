package com.nisovin.magicspells.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import static org.bukkit.attribute.AttributeModifier.Operation;

public class AttributeUtil {

	private static final Map<String, Attribute> attributeNameMap = new HashMap<>();
	private static final Map<String, Operation> operationNameMap = new HashMap<>();

	static {
		for (Operation operation : Operation.values()) {
			String name = operation.name().toLowerCase();

			operationNameMap.put(name, operation);
			operationNameMap.put(name.replaceAll("_", ""), operation);
		}

		operationNameMap.put("multiply_scalar", Operation.MULTIPLY_SCALAR_1);
		operationNameMap.put("multiplyscalar", Operation.MULTIPLY_SCALAR_1);

		for (Attribute attribute : Attribute.values()) {
			String key = attribute.getKey().getKey();
			String rep = key.replaceAll("_", "");

			attributeNameMap.put(attribute.name().toLowerCase(), attribute);

			attributeNameMap.put(key, attribute);
			attributeNameMap.put(key.substring(key.indexOf('.')), attribute);

			attributeNameMap.put(rep, attribute);
			attributeNameMap.put(rep.substring(key.indexOf('.')), attribute);
		}

	}

	public static Attribute getAttribute(String attribute) {
		return attributeNameMap.get(attribute.toLowerCase());
	}

	public static Operation getOperation(String operation) {
		return operationNameMap.get(operation.toLowerCase());
	}

	public static class AttributeModifierData {

		private final String name;
		private final double amount;
		private final Operation operation;
		private final EquipmentSlot slot;

		public AttributeModifierData(AttributeModifier mod) {
			name = mod.getName();
			amount = mod.getAmount();
			operation = mod.getOperation();
			slot = mod.getSlot();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			AttributeModifierData that = (AttributeModifierData) o;
			return amount == that.amount && Objects.equals(name, that.name) && operation == that.operation && slot == that.slot;
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, amount, operation, slot);
		}

	}

}
