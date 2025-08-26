package com.example.kkBazar;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

public class BlobDeserializer extends StdDeserializer<Blob> {

	public BlobDeserializer() {
		this(null);
	}

	public BlobDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Blob deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		JsonNode node = jp.getCodec().readTree(jp);
		String base64String = node.asText();
		byte[] decodedBytes = Base64.getDecoder().decode(base64String);

		try {
			return new SerialBlob(decodedBytes);
		} catch (SQLException e) {
			throw new IOException("Error creating Blob from base64 string", e);
		}
	}
}
