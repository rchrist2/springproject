package myproject.services;

import myproject.models.Tblemployee;
import myproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void deleteUser(int userId){
        userRepository.deleteUser(userId);
    }

    @Transactional
    public void insertUser(String user, String pass, int tblemployee, byte[] salt, String hashed_password, int userId){
        userRepository.updateUserAccount(user, pass, tblemployee, salt, hashed_password, userId);
    }

    @Transactional
    public void updateUserPassword(byte[] salt, String hashed_password, int emp){
        userRepository.updateUserPassword(salt, hashed_password, emp);
    }
}
