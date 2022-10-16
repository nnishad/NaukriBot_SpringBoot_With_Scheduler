package com.nikhilnishad.naukri.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class UserCookie implements Serializable {
    @Serial
    private static final long serialVersionUID = 4115876353625612383L;

    @MongoId
    private String cookieId;
    private String userId;
    private byte[] cookies;
}
