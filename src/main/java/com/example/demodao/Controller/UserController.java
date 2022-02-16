package com.example.demodao.Controller;

import com.example.demodao.Entity.User;
import com.example.demodao.Service.UserService;
import com.example.demodao.Util.UserResponseTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @GetMapping(value = "/healthz", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void checkHealthz(HttpServletRequest httpRequest){
        return ;
    }


    @PostMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponseTransfer newUser(@Valid @RequestBody User newUser){
        //TODO :  email uniqueness verify
        User userEmailValid = userService.findByUserName(newUser.getUsername());
        if(userEmailValid != null){
            throw new DuplicateKeyException("Your email has been associated with an existing account. Please try another email address");
        }
        User user = new User();
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        date = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        //user.setId(this.jdkIdGenerator.generateId());
        user.setFirst_name(newUser.getFirst_name());
        user.setLast_name(newUser.getLast_name());
        user.setUsername(newUser.getUsername());
        //bcrypt
        user.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

        user.setAccountCreated(currentTime);
        user.setAccountUpdated(currentTime);

        userService.save(user);
        return new UserResponseTransfer(user.getId(),user.getFirst_name(),user.getLast_name(),user.getUsername(),user.getAccountCreated(),user.getAccountUpdated());
    }

    @GetMapping(value = "/v1/user/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponseTransfer get(HttpServletRequest httpRequest){
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            User user = userService.findByUserName(values[0]);

            return new UserResponseTransfer(user.getId(),user.getFirst_name(),user.getLast_name(),user.getUsername(),user.getAccountCreated(),user.getAccountUpdated());

        }else {
            return null;
        }
    }


    @PutMapping (value = "/user/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponseTransfer update(HttpServletRequest httpRequest,@Valid @RequestBody User updateUser){
        //TODO : email uniqueness verify
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            User user = userService.findByUserName(values[0]);
            bCryptPasswordEncoder = new BCryptPasswordEncoder();
            date = new Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(date);
            //user.setId(this.jdkIdGenerator.generateId());

            if(updateUser.getFirst_name() != null || updateUser.getFirst_name()!=""){
                user.setFirst_name(updateUser.getFirst_name());
            }
            if(updateUser.getLast_name() != null || updateUser.getLast_name()!=""){
                user.setLast_name(updateUser.getLast_name());
            }
            if(updateUser.getUsername() != null || updateUser.getUsername()!=""){
                user.setUsername(updateUser.getUsername());
            }
            //bcrypt
            if(updateUser.getPassword() != null || updateUser.getPassword()!=""){
                user.setPassword(bCryptPasswordEncoder.encode(updateUser.getPassword()));
            }

            user.setAccountUpdated(currentTime);

            userService.save(user);
            return null;

        }else {
            return null;
        }

    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidationExceptions(MethodArgumentNotValidException e){
        Map<String,String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(
                (error) ->{
                    String filedName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(filedName,errorMessage);
                }
        );
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateKeyException.class)
    public Map<String,String> handleDuplicateKeyExceptions(DuplicateKeyException e){
        Map<String,String> errors = new HashMap<>();
        errors.put("Error: ",e.getMessage());
        return errors;
    }

}
