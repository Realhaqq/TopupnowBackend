package com.haqq.topupnow.controller;

import com.haqq.topupnow.model.User;
import com.haqq.topupnow.model.WalletTransaction;
import com.haqq.topupnow.payload.ApiResponse;
import com.haqq.topupnow.payload.TopupRequest;
import com.haqq.topupnow.repository.UserRepository;
import com.haqq.topupnow.repository.WalletTransactionRepo;
import com.haqq.topupnow.security.CurrentUser;
import com.haqq.topupnow.security.UserPrincipal;
import com.haqq.topupnow.service.RestService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;


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
            return ResponseEntity.created(location).body(new ApiResponse(false, "reload error " + realodlyauth()));

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

    public static String realodlyauth() {
        RestTemplate restTemplate = new RestTemplate();
        User user = null;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        try {
            JSONObject jsonCredentials = new JSONObject();
            jsonCredentials.put("client_id", "P9XyhmiDOOZ7PuI5gnUH777Mxu59mR9b");
            jsonCredentials.put("client_secret", "ZNBlIIxmuH-nltoj16VX0gB25e325c-ZeHAr4Vg6upuOHxb2TtLz7bwbZvi3aOn");
            jsonCredentials.put("grant_type", "client_credentials");
            jsonCredentials.put("audience", "https://topups.reloadly.com");
//            Log.e(Constants.APP_NAME, ">>>>>>>>>>>>>>>> JSON credentials " + jsonCredentials.toString());
            HttpEntity<String> entityCredentials = new HttpEntity<String>(jsonCredentials.toString(), httpHeaders);
            ResponseEntity<User> responseEntity = restTemplate.exchange("https://auth.reloadly.com/oauth/token",
                    HttpMethod.POST, entityCredentials, User.class);
            if (responseEntity != null) {
                System.out.println(responseEntity.getBody());
                user = responseEntity.getBody();
            }
            return String.valueOf(user);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
//            Log.e(Constants.APP_NAME, ">>>>>>>>>>>>>>>> " + e.getLocalizedMessage());
        }
        return null;


    }

}