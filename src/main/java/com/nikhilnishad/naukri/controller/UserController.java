package com.nikhilnishad.naukri.controller;

import com.nikhilnishad.naukri.exception.UserException;
import com.nikhilnishad.naukri.model.User;
import com.nikhilnishad.naukri.repository.UserRepository;
import com.nikhilnishad.naukri.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/user")
    public ResponseEntity<User> addUser(@Valid @RequestBody User userInput){
        return ResponseEntity.ok(userService.addUser(userInput));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<User> getUser(@PathVariable String email){
        User user= userService.getUser(email);
        if(user==null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<User> removeUser(@PathVariable String email){
        User user= userService.removeUser(email);
        if(user==null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.ok(user);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
