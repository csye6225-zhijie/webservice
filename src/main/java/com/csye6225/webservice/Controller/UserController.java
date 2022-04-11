package com.csye6225.webservice.Controller;

import com.amazonaws.util.EC2MetadataUtils;
import com.csye6225.webservice.Model.Image;
import com.csye6225.webservice.Model.User;
import com.csye6225.webservice.Model.VO.ImageVO;
import com.csye6225.webservice.Model.VO.UserVO;
import com.csye6225.webservice.Service.ImageService;
import com.csye6225.webservice.Service.UserService;
import com.google.gson.JsonObject;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Date;
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

    private final static Logger logger = LoggerFactory.getLogger( UserController.class);

    @Autowired
    private StatsDClient statsDClient;

    @GetMapping(value = "/healthz", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity checkHealthz(HttpServletRequest httpRequest){
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity newUser(@RequestBody User newUser){
        if (!userService.isValidEmail(newUser.getUsername()))  {
            logger.error("Provide valid email address");
           return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //verify email uniqueness
        User existedUserEmail = userService.findByUserName(newUser.getUsername());
        if(existedUserEmail != null){
            logger.warn("Provide unique email");
            throw new DuplicateKeyException("Your email has been associated with an existing account. Please try another email address");
        }
        if(newUser.getId() != null || newUser.getAccountCreated() != null || newUser.getAccountUpdated() != null) {
            logger.error("Only name and password can be reset");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        UserVO userVO = userService.saveUser(newUser);

        statsDClient.incrementCounter("_UserRegister_API_");
        logger.info("User registered. Current time: "+ new Date().toString());

        return new ResponseEntity(userVO,
                HttpStatus.CREATED);
    }

    @GetMapping(value = "/v1/user/self", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest httpRequest){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Current Data & Time", new Date().toString());
        jsonObject.addProperty("Message", "API: when created new User");

        try {
            jsonObject.addProperty("PrivateIpAddress", EC2MetadataUtils.getPrivateIpAddress());
            jsonObject.addProperty("InstanceId", EC2MetadataUtils.getInstanceId());
            jsonObject.addProperty("InstanceType", EC2MetadataUtils.getInstanceType());
            jsonObject.addProperty("AvailabilityZone", EC2MetadataUtils.getAvailabilityZone());
            jsonObject.addProperty("EC2InstanceRegion", EC2MetadataUtils.getEC2InstanceRegion());
            jsonObject.addProperty("AmiId", EC2MetadataUtils.getAmiId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        String returnString = jsonObject.toString();
        logger.info(returnString);

        final String authorization = httpRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);

        if(userVO == null) {
            logger.error("Unauthorized");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        else {
            statsDClient.incrementCounter("_UserLoggedIn_API_");
            logger.info("Logged in. Current time: "+ new Date().toString());

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
                logger.error("Provide valid new name or password");
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            } else if (updateResult == 1) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }
        statsDClient.incrementCounter("_ResetPassword_API_");
        logger.info("Reset password. Current time: "+ new Date().toString());

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @PostMapping(value = "/v1/user/self/pic")
    @ResponseBody
    public ResponseEntity uploadUserProfilePic(HttpServletRequest httpServletRequest, InputStream is) throws Exception {
        if(null == is) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        final String authorization = httpServletRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);
        if(null == userVO) {
            logger.error("Unauthorized");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        //check repeat image
        Image image = imageService.findByUserId(userVO.getId());
        if(null != image){
            imageService.deleteFile(image);
        }
        ImageVO imageVO = imageService.saveFile(userVO, is);

        statsDClient.incrementCounter("_SetImage_API_");
        logger.info("Set image. Current time: "+ new Date().toString());

        return ResponseEntity
                .created(URI.create(imageVO.getUrl()))
                .body(imageVO);
    }

    @GetMapping(value = "/v1/user/self/pic")
    @ResponseBody
    public ResponseEntity getUserProfileInfo(HttpServletRequest httpServletRequest) throws IOException {
        final String authorization = httpServletRequest.getHeader("Authorization");
        UserVO userVO = userService.authorize(authorization);
        if(null == userVO) {
            logger.error("Unauthorized");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Image image = imageService.findByUserId(userVO.getId());
        if (null == image) {
            logger.info("No attached image");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        ImageVO imageVO = new ImageVO(image);

        statsDClient.incrementCounter("_GetImage_API_");
        logger.info("Get image info. Current time: "+ new Date().toString());

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
            logger.error("Unauthorized");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Image image = imageService.findByUserId(userVO.getId());
        if (null == image) {
            logger.info("No attached image");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        imageService.deleteFile(image);

        statsDClient.incrementCounter("_DeleteImage_API_");
        logger.info("Delete image. Current time: "+ new Date().toString());

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
