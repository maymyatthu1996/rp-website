package com.planetway.relyingpartyapp.model;

import java.util.List;

import org.apache.tomcat.jni.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String firstNameKatakana;
    private String lastNameKatakana;
    private String firstNameKanji;
    private String lastNameKanji;
    private String firstNameRomaji;
    private String lastNameRomaji;
    private String dateOfBirth;
    private List<Address> address;
}
