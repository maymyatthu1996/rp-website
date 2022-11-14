package com.planetway.relyingpartyapp.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.planetway.relyingpartyapp.model.RpUserEntity;
import com.planetway.relyingpartyapp.model.UserRegistrationDto;

public interface UserService extends UserDetailsService{
	RpUserEntity save(UserRegistrationDto registrationDto);
}
