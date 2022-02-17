package com.csye6225.webservice.Service;

import com.csye6225.webservice.DAO.UserDaoImpl;
import com.csye6225.webservice.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional(readOnly=true)
public class UserService {

    @Autowired
    private UserDaoImpl userDao;

    @Transactional(readOnly = false)
    public void save(User user){
        userDao.save(user);
    }

    public User findById(String id){
        User user = userDao.findById(id);
        return user;
    }

    public User findByUserName(String userName){
        User user = userDao.findByUserName(userName);
        return user;
    }

    @Transactional(readOnly = false)
    public void update(User user)
    {
        userDao.update(user);
    }
    @Transactional(readOnly = false)
    public void delete(String id)
    {
        userDao.delete(id);
    }
}
