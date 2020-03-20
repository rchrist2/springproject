package myproject.services;

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

}
