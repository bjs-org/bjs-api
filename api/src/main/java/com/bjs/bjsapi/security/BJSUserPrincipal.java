package com.bjs.bjsapi.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.bjs.bjsapi.database.model.User;

public class BJSUserPrincipal implements UserDetails {

	private User user;

	public BJSUserPrincipal(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (user.getAdministrator()) {
			return AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
		}

		return AuthorityUtils.createAuthorityList("ROLE_USER");
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getEnabled();
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return String.format("BJSUserPrincipal{user=%s}", user);
	}

}
