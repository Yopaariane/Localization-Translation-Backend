package com.myapp.localizationApp.service;

import com.myapp.localizationApp.configuration.CustomUserDetails;
import com.myapp.localizationApp.entity.RolePermission;
import com.myapp.localizationApp.entity.User;
import com.myapp.localizationApp.entity.UserRole;
import com.myapp.localizationApp.repository.RolePermissionRepository;
import com.myapp.localizationApp.repository.UserRepository;
import com.myapp.localizationApp.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService {

//    private final UserRepository userRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository){
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("Admin"));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPasswordHash(),
//                authorities
//        );
//    }

}
