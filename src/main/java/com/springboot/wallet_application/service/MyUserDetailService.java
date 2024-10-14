package com.springboot.wallet_application.service;

import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.UserPrincipal;
import com.springboot.wallet_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user==null) {
            throw new UsernameNotFoundException("Username not found Exception for " + username);
        }
        return new UserPrincipal(user);
    }
}
