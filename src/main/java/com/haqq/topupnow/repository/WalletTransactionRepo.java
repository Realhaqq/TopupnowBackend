package com.haqq.topupnow.repository;

import com.haqq.topupnow.model.Role;
import com.haqq.topupnow.model.RoleName;
import com.haqq.topupnow.model.WalletTransaction;
import com.haqq.topupnow.payload.WalletRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletTransactionRepo extends JpaRepository<WalletTransaction, Integer> {
//    Optional<Role> findByName(RoleName roleName);
WalletTransaction findWalletTransactionByuserid(String userid);

}
