package com.csye6225.webservice.Controller;

import com.csye6225.webservice.Model.Image;
import com.csye6225.webservice.Model.User;
import com.csye6225.webservice.Model.VO.ImageVO;
import com.csye6225.webservice.Model.VO.UserVO;
import com.csye6225.webservice.Service.ImageService;
import com.csye6225.webservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("")
public class UserController {

    private UserService userService;
    @Resource
    private ImageService imageService;


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
        if (!userService.isValidEmail(newUser.getUsername()))  {
           return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //verify email uniqueness
        User existedUserEmail = userService.findByUserName(newUser.getUsername());
        if(existedUserEmail != null){
            throw new DuplicateKeyException("Your email has been associated with an existing account. Please try another email address");
        }
        if(newUser.getId() != null || newUser.getAccountCreated() != null || newUser.getAccountUpdated() != null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        UserVO userVO = userService.saveUser(newUser);
        return new ResponseEntity(userVO,
                HttpStatus.CREATED);
    }

    @GetMapping(value = "/v1/user/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest httpRequest){
        final String authorization = httpRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);

        if(userVO == null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        else {
            return new ResponseEntity(userVO, HttpStatus.OK);

        }
    }


    @PutMapping (value = "/v1/user/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(HttpServletRequest httpRequest, @RequestBody User updateUser) {
        final String authorization = httpRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);
        if (userVO != null) {

            int updateResult = userService.updateUser(userService.findById(userVO.getId()), updateUser); //
            if (updateResult == 0) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            } else if (updateResult == 1) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @PostMapping(value = "/v1/user/self/pic")
    @ResponseBody
    public ResponseEntity uploadUserProfilePic(HttpServletRequest httpServletRequest, InputStream is) throws Exception {
        if(null == is) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        final String authorization = httpServletRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);
        if(null == userVO) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        //check repeat image
        Image image = imageService.findByUserId(userVO.getId());
        if(null != image){
            imageService.deleteFile(image);
        }
        ImageVO imageVO = imageService.saveFile(userVO, is);
        return ResponseEntity
                .created(URI.create(imageVO.getUrl()))
                .body(imageVO);
    }

    @GetMapping(value = "/v1/user/self/pic")
    @ResponseBody
    public ResponseEntity getUserProfileInfo(HttpServletRequest httpServletRequest) throws IOException {
        final String authorization = httpServletRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);
        if(null == userVO)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Image image = imageService.findByUserId(userVO.getId());
        if (null == image) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        ImageVO imageVO = new ImageVO(image);

        return ResponseEntity
                .ok()
                .body(imageVO);
    }

    @DeleteMapping("/v1/user/self/pic")
    @ResponseBody
    public ResponseEntity deleteUserProfilePic(HttpServletRequest httpServletRequest) {
        final String authorization = httpServletRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);
        if(null == userVO) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Image image = imageService.findByUserId(userVO.getId());
        if (null == image) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        imageService.deleteFile(image);
        return ResponseEntity
                .noContent()
                .build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateKeyException.class)
    public Map<String,String> handleDuplicateKeyExceptions(DuplicateKeyException e){
        Map<String,String> errors = new HashMap<>();
        errors.put("Error: ",e.getMessage());
        return errors;
    }

}
