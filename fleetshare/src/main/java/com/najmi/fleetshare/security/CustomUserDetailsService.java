package com.najmi.fleetshare.security;

import com.najmi.fleetshare.entity.Renter;
import com.najmi.fleetshare.entity.User;
import com.najmi.fleetshare.entity.UserRole;
import com.najmi.fleetshare.repository.RenterRepository;
import com.najmi.fleetshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // If user is a renter, fetch their full name
        String fullName = null;
        if (user.getUserRole() == UserRole.RENTER) {
            Renter renter = renterRepository.findByUserId(user.getUserId())
                    .orElse(null);
            if (renter != null) {
                fullName = renter.getFullName();
            }
        }

        return new CustomUserDetails(user, fullName);
    }
}
