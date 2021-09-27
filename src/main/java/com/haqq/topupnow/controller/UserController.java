package com.haqq.topupnow.controller;

import com.haqq.topupnow.model.User;
import com.haqq.topupnow.model.WalletTransaction;
import com.haqq.topupnow.payload.*;
import com.haqq.topupnow.repository.UserRepository;
import com.haqq.topupnow.repository.WalletTransactionRepo;
import com.haqq.topupnow.security.UserPrincipal;
import com.haqq.topupnow.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    WalletTransactionRepo walletTransactionRepo;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(), currentUser.getEmail(), currentUser.getBalance());
        return userSummary;
    }



    @PostMapping("/user/wallet")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userWallet(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody WalletRequest walletRequest) {

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(currentUser.getUsername()).toUri();
        if(walletRequest.getInFlaw().contains("true")){

            int amount = Integer.parseInt(walletRequest.getAmount());
            double balance = currentUser.getBalance();
            User user = userRepository.findByEmail(currentUser.getEmail());
            user.setBalance(balance + amount);
            userRepository.save(user);
//              insert wallet transactions
            WalletTransaction walletTransaction = new WalletTransaction();
            walletTransaction.setAmount(String.valueOf(walletRequest.getAmount()));
            walletTransaction.setUserid(String.valueOf(currentUser.getId()));
            walletTransaction.setType("Credit");
            walletTransaction.setOndate(new Date());
            walletTransactionRepo.save(walletTransaction);
            return ResponseEntity.created(location).body(new ApiResponse(true, "User wallet successfully!"));
        }else{

            int amount = Integer.parseInt(walletRequest.getAmount());
            double balance = currentUser.getBalance();

            if(balance < amount){
                return ResponseEntity.created(location).body(new ApiResponse(false, "insufficient Wallet Balance"));
            }else{
                User user = userRepository.findByEmail(currentUser.getEmail());
                user.setBalance(balance - amount);
                userRepository.save(user);
//              insert wallet transactions
                WalletTransaction walletTransaction = new WalletTransaction();
                walletTransaction.setAmount(String.valueOf(walletRequest.getAmount()));
                walletTransaction.setUserid(String.valueOf(currentUser.getId()));
                walletTransaction.setType("Debit");
                walletTransaction.setOndate(new Date());
                walletTransactionRepo.save(walletTransaction);
                return ResponseEntity.created(location).body(new ApiResponse(true, "Wallet Updated Successfully!"));

            }

        }



    }


    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

//    @GetMapping("/users/{username}")
//    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//
//        long pollCount = pollRepository.countByCreatedBy(user.getId());
//        long voteCount = voteRepository.countByUserId(user.getId());
//
//        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, voteCount);
//
//        return userProfile;
//    }
//
//    @GetMapping("/users/{username}/polls")
//    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
//                                                         @CurrentUser UserPrincipal currentUser,
//                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
//                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
//        return pollService.getPollsCreatedBy(username, currentUser, page, size);
//    }
//
//
//    @GetMapping("/users/{username}/votes")
//    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
//                                                       @CurrentUser UserPrincipal currentUser,
//                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
//                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
//        return pollService.getPollsVotedBy(username, currentUser, page, size);
//    }

}
