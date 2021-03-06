package com.example.demopugspring.engine.operation;

import java.util.List;

import com.example.demopugspring.engine.MapperEngine;
import com.example.demopugspring.engine.MapperError;
import com.example.demopugspring.visitor.StandardVisitor;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.util.Terser;

/**
 * This Operation inserts the text in {@link AbstractOperation#value} at the
 * path specified in the first key included in {@link AbstractOperation#keys},
 * replacing any previous content in the message.
 * </p>
 * If the key contains the '#' character, instead of using
 * {@link ca.uhn.hl7v2.util.Terser}, it will use
 * {@link com.example.demopugspring.visitor.StandardVisitor} to get the types to
 * be changed.
 *
 */
public class Text extends AbstractOperation {

	public Text(MapperEngine engine, Message incomingMessage, Message outgoingMessage, Terser incomingTerser, Terser outgoingTerser, List<String> keys, String value) {
		super(engine, incomingMessage, outgoingMessage, incomingTerser, outgoingTerser, keys, value);
	}

	@Override
	public void map() {
		String key = keys.get(0);

		try {
			if (key.contains("#")) {
				StandardVisitor visitor = new StandardVisitor(key);
				visitor.start(outgoingMessage);
				for (Type type : visitor.getVisitedTypes()) {
					type.parse(value);
				}
			} else {
				outgoingTerser.set(key, value);
			}
		} catch (HL7Exception e) {
			log.error(e.getMessage());
			errors.add(new MapperError(key, e.getMessage()));
		}
	}
}
