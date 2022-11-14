package com.planetway.relyingpartyapp.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
@Table(name = "User")
public class UserInfo extends User {
    private static final long serialVersionUID = 1L;
	private Long id;
    private String planetId;

    public UserInfo(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, String planetId) {
        super(username, password, authorities);
        this.id = id;
        this.planetId = planetId;
    }
}
