package com.example.demodao.Controller;

import com.example.demodao.Service.UserService;
import com.example.demodao.Util.UserResponseTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class UserController {
    @GetMapping(value = "/healthz", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponseTransfer get(HttpServletRequest httpRequest){

        return null;
    }

    private UserService userService;
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    private Date date;

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
