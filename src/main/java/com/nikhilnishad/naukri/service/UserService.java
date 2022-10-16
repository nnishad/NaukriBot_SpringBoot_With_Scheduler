package com.nikhilnishad.naukri.service;

import com.nikhilnishad.naukri.model.User;
import com.nikhilnishad.naukri.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public User addUser(User userInput) {
        User user = userRepository.findByEmail(userInput.getEmail());
        if(user==null){
            return userRepository.save(userInput);
        }
        user.setActive(true);
        user.setPassword(userInput.getPassword());
        return userRepository.save(user);
    }

    public User getUser(String email) {
        User user=userRepository.findByEmail(email);
        if(user.isActive()){
            return user;
        }
        return null;
    }

    public User removeUser(String email) {
        User user=userRepository.findByEmail(email);
        if(user.isActive()){
            user.setActive(false);
            return userRepository.save(user);
        }
        return null;
    }
}
