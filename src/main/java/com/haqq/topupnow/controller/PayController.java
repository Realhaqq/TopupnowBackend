package com.haqq.topupnow.controller;

import com.haqq.topupnow.model.User;
import com.haqq.topupnow.model.WalletTransaction;
import com.haqq.topupnow.payload.ApiResponse;
import com.haqq.topupnow.payload.TopupRequest;
import com.haqq.topupnow.repository.UserRepository;
import com.haqq.topupnow.repository.WalletTransactionRepo;
import com.haqq.topupnow.security.CurrentUser;
import com.haqq.topupnow.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class PayController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    WalletTransactionRepo walletTransactionRepo;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/pay/topups")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userWallet(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody TopupRequest topupRequest) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(currentUser.getUsername()).toUri();
        int amount = Integer.parseInt(topupRequest.getAmount());
        double balance = currentUser.getBalance();
        String email = currentUser.getEmail();
        int userid = Math.toIntExact(currentUser.getId());

        if (balance < amount) {
            return ResponseEntity.created(location).body(new ApiResponse(false, "insufficient Wallet Balance"));
        } else {
            if(updateWallet(amount, balance, email, userid)){
                return ResponseEntity.created(location).body(new ApiResponse(false, "Wallet Debitted success"));
            }else{
                return ResponseEntity.created(location).body(new ApiResponse(false, "Wallet cannot be debitted!"));


            }
        }


    }

    public boolean updateWallet(int amount, Double balance, String email, int userid){
            User user = userRepository.findByEmail(email);
            user.setBalance(balance - amount);
            userRepository.save(user);
//              insert wallet transactions
            WalletTransaction walletTransaction = new WalletTransaction();
            walletTransaction.setAmount(String.valueOf(amount));
            walletTransaction.setUserid(String.valueOf(userid));
            walletTransaction.setType("Debit");
            walletTransaction.setOndate(new Date());
            walletTransactionRepo.save(walletTransaction);

            return true;
    }

}