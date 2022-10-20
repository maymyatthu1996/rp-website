package com.planetway.relyingpartyapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PurchaseInfo")
@Data
@NoArgsConstructor
public class PurchaseInfoEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		private String name;
		private String email;
		private String dob;
		private String job;
		private String address;
		private String phone;

}
