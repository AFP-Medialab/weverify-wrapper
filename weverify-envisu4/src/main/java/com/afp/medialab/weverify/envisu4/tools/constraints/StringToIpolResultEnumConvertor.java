package com.afp.medialab.weverify.envisu4.tools.constraints;

import org.springframework.core.convert.converter.Converter;

@RequestParameterConverter
public class StringToIpolResultEnumConvertor implements Converter<String, IpolResultEnum> {

	
	public IpolResultEnum convert(String source) {
		// TODO Auto-generated method stub
		return IpolResultEnum.decode(source);
	}

}
