package com.csye6225.webservice.Service;

import com.csye6225.webservice.DAO.UserDaoImpl;
import com.csye6225.webservice.Model.User;
import com.csye6225.webservice.Model.VO.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service("userService")
@Transactional(readOnly=true)
public class UserService {

    @Autowired
    private UserDaoImpl userDao;
    public static final String EMAIL_REGEX_DEFAULT = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Date date;

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX_DEFAULT);
    }

    public User createUser(User user){
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        date = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        User newUser = new User();

        newUser.setUsername(user.getUsername());
        newUser.setFirst_name(user.getFirst_name());
        newUser.setLast_name(user.getLast_name());
        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        newUser.setAccountCreated(currentTime);
        newUser.setAccountUpdated(currentTime);
        return newUser;
    }

    @Transactional(readOnly = false)
    public UserVO saveUser(User user){
        User newUser = createUser(user);
        userDao.save(newUser);
        return new UserVO(newUser.getId(), newUser.getFirst_name(), newUser.getLast_name(), newUser.getUsername(), newUser.getAccountCreated(), newUser.getAccountUpdated());
    }

    public User findById(String id){
        User user = userDao.findById(id);
        return user;
    }

    public User findByUserName(String userName){
        User user = userDao.findByUserName(userName);
        return user;
    }

    public UserVO authorize(String authorization){
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            User user = findByUserName(values[0]);
            if(null == user) return null;

            //check and compare password
            String rightPassword = user.getPassword();
            if(!BCrypt.checkpw(values[1], rightPassword))
                return null;

            UserVO userVO = new UserVO(user.getId(), user.getFirst_name(), user.getLast_name(), user.getUsername(), user.getAccountCreated(), user.getAccountUpdated());
            return userVO;
        }else {
            return null;
        }
    }

    @Transactional(readOnly = false)
    public int updateUser(User user, User updateUser)
    {
        //only permitted field can be modified
        if(updateUser.getId() != null || updateUser.getAccountCreated() != null || updateUser.getAccountUpdated() != null){
            return 0;
        }
        if(updateUser.getUsername() != null && !updateUser.getUsername().equals(user.getUsername()) ){
            return 0;
        }

        date = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);

        if(updateUser.getFirst_name() != null && updateUser.getFirst_name()!=""){
            user.setFirst_name(updateUser.getFirst_name());
        }
        if(updateUser.getLast_name() != null && updateUser.getLast_name()!=""){
            user.setLast_name(updateUser.getLast_name());
        }
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if(updateUser.getPassword() != null && updateUser.getPassword()!=""){
            user.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));
        }
        user.setAccountUpdated(currentTime);

        userDao.update(user);
        return 1;
    }

}
