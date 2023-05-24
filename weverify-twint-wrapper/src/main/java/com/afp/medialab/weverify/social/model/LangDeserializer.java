package com.afp.medialab.weverify.social.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class LangDeserializer extends StdDeserializer<String> {

	public LangDeserializer() {
		this(null);
	}
	
	public LangDeserializer(Class<?> vc) {
		super(vc);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 160011596962084608L;

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
        final String lang = node.textValue();
        if(lang.equals(""))
        	return null;
        return lang;
	}

}
