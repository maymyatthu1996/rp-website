package com.planetway.relyingpartyapp.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.planetway.relyingpartyapp.model.User;
import com.planetway.relyingpartyapp.model.UserRegistrationDto;

public interface UserService extends UserDetailsService{
	User save(UserRegistrationDto registrationDto);
}
