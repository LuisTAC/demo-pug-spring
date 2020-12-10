package com.example.demopugspring.engine.operation;

import java.util.List;
import java.util.Objects;

import com.example.demopugspring.engine.MapperEngine;
import com.example.demopugspring.engine.MapperError;
import com.example.demopugspring.visitor.StandardVisitor;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.util.Terser;

/**
 * This Operation copies the value of the message at the path given by the first
 * key in {@link AbstractOperation#keys}, with the value at the path given by
 * {@link AbstractOperation#value}, replacing any previous content in the
 * message.
 * </p>
 * If the key contains the '#' character, instead of using
 * {@link ca.uhn.hl7v2.util.Terser}, it will use
 * {@link com.example.demopugspring.visitor.StandardVisitor} to get the types to
 * be changed.
 *
 */
public class Field extends AbstractOperation {

	private String fieldValue;

	public Field(MapperEngine engine, Message incomingMessage, Message outgoingMessage, Terser incomingTerser, Terser outgoingTerser, List<String> keys, String value) {
		super(engine, incomingMessage, outgoingMessage, incomingTerser, outgoingTerser, keys, value);
	}

	@Override
	public void map() {
		try {
			fieldValue = getUseOriginalValue() ? incomingTerser.get(value) : outgoingTerser.get(value);
		} catch (HL7Exception e) {
			log.error(e.getMessage());
			errors.add(new MapperError(keys.toString(), e.getMessage()));
		}

		String key = keys.get(0);

		try {
			if (key.contains("#")) {
				StandardVisitor visitor = new StandardVisitor(key);
				visitor.start(outgoingMessage);
				for (Type type : visitor.getVisitedTypes()) {
					type.parse(fieldValue);
				}
			} else {
				outgoingTerser.set(key, fieldValue);
			}
		} catch (HL7Exception e) {
			log.error(e.getMessage());
			errors.add(new MapperError(key, e.getMessage()));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(fieldValue);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Field)) {
			return false;
		}
		Field other = (Field) obj;
		return Objects.equals(fieldValue, other.fieldValue);
	}
}
