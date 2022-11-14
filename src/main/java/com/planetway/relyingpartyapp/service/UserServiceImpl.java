package com.planetway.relyingpartyapp.service;

import static java.util.Collections.emptyList;

import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.planetway.relyingpartyapp.exception.PlanetIdNotLinkedException;
import com.planetway.relyingpartyapp.model.PlanetIdEntity;
import com.planetway.relyingpartyapp.model.RpUserEntity;
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
	public RpUserEntity save(UserRegistrationDto registrationDto) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		RpUserEntity user = new RpUserEntity(registrationDto.getFirstName(), 
				registrationDto.getLastName(), registrationDto.getEmail(),
				passwordEncoder.encode(registrationDto.getPassword()));
		
		return userRepository.save(user);
	}

	@Override
	public UserInfo loadUserByUsername(String username) throws UsernameNotFoundException {
	
		RpUserEntity user = userRepository.findByEmail(username);
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
		return new UserInfo(user.getId(),user.getEmail(), user.getPassword(), emptyList(), planetId);	
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
    	RpUserEntity user = userRepository.findByPlanetId(planetId);
        if (user == null) {
            throw new PlanetIdNotLinkedException("PlanetID " + planetId + " not linked to account ");
        }

        UserInfo principal = new UserInfo(user.getId(),user.getEmail(), user.getPassword(),emptyList(), planetId);
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

