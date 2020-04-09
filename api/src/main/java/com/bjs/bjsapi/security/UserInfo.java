package com.bjs.bjsapi.security;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class UserInfo {
	String username;
	Boolean administrator;
}
