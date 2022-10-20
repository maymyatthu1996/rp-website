package com.planetway.relyingpartyapp.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planetway.relyingpartyapp.model.CheckOutFormDto;
import com.planetway.relyingpartyapp.model.PurchaseInfoEntity;
import com.planetway.relyingpartyapp.repository.PurchaseInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {

	@Autowired
	PurchaseInfoRepository purchaseInfoRepo;
	
	@Transactional
	public PurchaseInfoEntity save(CheckOutFormDto checkoutformdto, String email) {
		PurchaseInfoEntity entity = new PurchaseInfoEntity();
		entity.setName(checkoutformdto.getName());
		entity.setDob(checkoutformdto.getDob());
		entity.setEmail(email);
		//entity.setBankacc(checkoutformdto.getBankacc());
		entity.setPhone(checkoutformdto.getPhone());
		entity.setAddress(checkoutformdto.getAddress());
		entity.setJob(checkoutformdto.getJob());
		return purchaseInfoRepo.save(entity);
	}
}
