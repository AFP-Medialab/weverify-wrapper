package com.afp.medialab.weverify.security;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.nimbusds.jose.shaded.json.JSONArray;

/**
 * Convert JWT Roles to Authorities
 * @author Bertrand Goupil
 *
 */
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

	@Override
	public AbstractAuthenticationToken convert(Jwt source) {
		Collection<GrantedAuthority> authorities = Stream
				.concat(defaultGrantedAuthoritiesConverter.convert(source).stream(),
						extractResourceRoles(source).stream())
				.collect(Collectors.toSet());
		return new JwtAuthenticationToken(source, authorities);
	}

	private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt) {
		JSONArray roles = jwt.getClaim("roles");
		if (roles != null)
			return roles.stream().map(x -> new SimpleGrantedAuthority((String) x)).collect(Collectors.toSet());
		return Collections.emptySet();
	}
}
