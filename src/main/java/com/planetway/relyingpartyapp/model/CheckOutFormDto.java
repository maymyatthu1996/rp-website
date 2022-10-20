package com.planetway.relyingpartyapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CheckOutFormDto {
	
	private String name;
	private String dob;
	private String job;
	private String address;
	private String phone;
	private int soapFlg;
	
	

}
