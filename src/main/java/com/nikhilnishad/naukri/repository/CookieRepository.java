package com.nikhilnishad.naukri.repository;

import com.nikhilnishad.naukri.model.UserCookie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookieRepository extends MongoRepository<UserCookie, String> {
    UserCookie findByUserId(String userId);
}
