package com.afp.medialab.weverify.envisu4.tools.constraints;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IpolResultEnum {

	output_0("output_0.png"), output_1("output_1.png"), pano("pano.jpg"), stdout("stdout.txt");

	private String code;

	private IpolResultEnum(String result) {
		this.code = result;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	@JsonCreator
	public static IpolResultEnum decode(final String code) {
		return Stream.of(IpolResultEnum.values()).filter(targetEnum -> targetEnum.code.equals(code)).findFirst()
				.orElse(null);
	}

}
