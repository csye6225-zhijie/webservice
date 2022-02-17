package com.csye6225.webservice.Controller;

import com.csye6225.webservice.Entity.User;
import com.csye6225.webservice.Service.UserService;
import com.csye6225.webservice.Util.UserResponseTransfer;
import org.aspectj.bridge.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class UserController {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private Date date;
    public static final String EMAIL_REGEX_DEFAULT = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private MessageUtil exceptionLog;

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX_DEFAULT);
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @GetMapping(value = "/healthz", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity checkHealthz(HttpServletRequest httpRequest){
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity newUser(@RequestBody User newUser){
        if (!isValidEmail(newUser.getUsername()))  {
           return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        //verify email uniqueness
        User repeatedUserEmail = userService.findByUserName(newUser.getUsername());
        if(repeatedUserEmail != null){
            throw new DuplicateKeyException("Your email has been associated with an existing account. Please try another email address");
        }
        User user = new User();
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        date = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        user.setFirst_name(newUser.getFirst_name());
        user.setLast_name(newUser.getLast_name());
        user.setUsername(newUser.getUsername());
        //bcrypt
        user.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

        user.setAccountCreated(currentTime);
        user.setAccountUpdated(currentTime);

        userService.save(user);
        return new ResponseEntity(new UserResponseTransfer(user.getId(),user.getFirst_name(),user.getLast_name(),user.getUsername(),user.getAccountCreated(),user.getAccountUpdated()),
                HttpStatus.CREATED);
    }

    @GetMapping(value = "/v1/user/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity get(HttpServletRequest httpRequest){
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            User user = userService.findByUserName(values[0]);
            if(null == user) return new ResponseEntity(HttpStatus.BAD_REQUEST);

            //check and compare password
            String rightPassword = user.getPassword();
            if(!BCrypt.checkpw(values[1], rightPassword))
                return new ResponseEntity(HttpStatus.BAD_REQUEST);

            return new ResponseEntity(new UserResponseTransfer(user.getId(),user.getFirst_name(),user.getLast_name(),user.getUsername(),user.getAccountCreated(),user.getAccountUpdated())
            , HttpStatus.OK);
        }else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping (value = "/v1/user/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(HttpServletRequest httpRequest, @RequestBody User updateUser){
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            User user = userService.findByUserName(values[0]);
            if (null == user) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            String rightPassword = user.getPassword();
            if(!BCrypt.checkpw(values[1], rightPassword))
                return new ResponseEntity(HttpStatus.UNAUTHORIZED); //

            date = new Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(date);
            //only permitted field can be modified
            if(updateUser.getId() != null || updateUser.getAccountCreated() != null || updateUser.getAccountUpdated() != null){
                return new ResponseEntity(HttpStatus.BAD_REQUEST); //compare if null use ==
            }
            if(updateUser.getUsername() != null && !updateUser.getUsername().equals(user.getUsername()) ){
                return new ResponseEntity(HttpStatus.BAD_REQUEST); //compare string not with '=='
            }


            if(updateUser.getFirst_name() != null && updateUser.getFirst_name()!=""){
                user.setFirst_name(updateUser.getFirst_name());
            }
            if(updateUser.getLast_name() != null && updateUser.getLast_name()!=""){
                user.setLast_name(updateUser.getLast_name());
            }
            //bcrypt
            bCryptPasswordEncoder = new BCryptPasswordEncoder();
            if(updateUser.getPassword() != null && updateUser.getPassword()!=""){
                user.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));
            }

            user.setAccountUpdated(currentTime);

            userService.save(user);
            return new ResponseEntity(HttpStatus.NO_CONTENT);

        }else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateKeyException.class)
    public Map<String,String> handleDuplicateKeyExceptions(DuplicateKeyException e){
        Map<String,String> errors = new HashMap<>();
        errors.put("Error: ",e.getMessage());
        return errors;
    }

}
