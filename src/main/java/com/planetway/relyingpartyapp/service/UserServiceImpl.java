package com.planetway.relyingpartyapp.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.planetway.relyingpartyapp.exception.PlanetIdNotLinkedException;
import com.planetway.relyingpartyapp.model.PlanetIdEntity;
import com.planetway.relyingpartyapp.model.Role;
import com.planetway.relyingpartyapp.model.User;
import com.planetway.relyingpartyapp.model.UserInfo;
import com.planetway.relyingpartyapp.model.UserRegistrationDto;
import com.planetway.relyingpartyapp.repository.PlanetIdRepository;
import com.planetway.relyingpartyapp.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PlanetIdRepository planetIdRepository;
	
	public UserServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public User save(UserRegistrationDto registrationDto) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		User user = new User(registrationDto.getFirstName(), 
				registrationDto.getLastName(), registrationDto.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()), Arrays.asList(new Role("ROLE_USER")));
		
		return userRepository.save(user);
	}

	@Override
	public UserInfo loadUserByUsername(String username) throws UsernameNotFoundException {
	
		User user = userRepository.findByEmail(username);
		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
        PlanetIdEntity planetIdEntity = planetIdRepository.findByRpUserId(user.getId());
        String planetId;
        if (planetIdEntity != null) {
            planetId = planetIdEntity.getId();
        } else {
            planetId = null;
        }
		return new UserInfo(user.getId(),user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()), planetId);	
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
	
	@Transactional
    public UserInfo linkPlanetId(UserInfo user, String planetId) {
        PlanetIdEntity planetIdEntity = planetIdRepository.findByRpUserId(user.getId());
        if (planetIdEntity != null) {
            throw new RuntimeException("PlanetID already linked.");
        }
        planetIdEntity = planetIdRepository.findById(planetId).orElse(null);
        if (planetIdEntity != null) {
            throw new RuntimeException("PlanetID already linked.");
        }

        planetIdEntity = new PlanetIdEntity();
        planetIdEntity.setId(planetId);
        planetIdEntity.setRpUserId(user.getId());
        planetIdRepository.save(planetIdEntity);
        user.setPlanetId(planetId);
        return user;
    }
	
    public void loginUserWithPlanetId(String planetId) {
        //log.info("Logging in user with PlanetID: {}", planetId);

        User user = userRepository.findByPlanetId(planetId);
        if (user == null) {
            throw new PlanetIdNotLinkedException("PlanetID " + planetId + " not linked to account ");
        }

        UserInfo principal = new UserInfo(user.getId(),user.getEmail(), user.getPassword(),mapRolesToAuthorities(user.getRoles()), planetId);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
	
    @Transactional
    public UserInfo unlinkPlanetId(UserInfo user) {
        planetIdRepository.deleteByRpUserId(user.getId());
        user.setPlanetId(null);
        return user;
    }
    

}

