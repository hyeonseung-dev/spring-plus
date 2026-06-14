package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String bearerJwt = request.getHeader("Authorization");

		if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = jwtUtil.substringToken(bearerJwt);
		Claims claims = jwtUtil.extractClaims(jwt);

		Long userId = Long.parseLong(claims.getSubject());
		String email = claims.get("email", String.class);
		String nickname = claims.get("nickname", String.class);
		UserRole userRole = UserRole.of(claims.get("userRole", String.class));

		AuthUser authUser = new AuthUser(userId, email, nickname, userRole);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(
				authUser,
				null,
				List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()))
			);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}