package com.csye6225.webservice.Service;

import com.csye6225.webservice.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userAuthenService")
public class UserAuthenService implements UserDetailsService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = userService.findByUserName(userName);//!!!!!!



        if(user == null){
            throw new UsernameNotFoundException("User not found by name "+userName);
        }
        return new org.springframework.security.core.userdetails.User(userName,
                user.getPassword(),getGrantedAuthority(user));
    }

    // 默认给user一个 “ROLE_USER" authority, 实际上应该从数据库读取并保存
    public List<GrantedAuthority> getGrantedAuthority(User user){
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        return list;
    }
}

