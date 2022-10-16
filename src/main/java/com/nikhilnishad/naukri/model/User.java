package com.nikhilnishad.naukri.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class User  implements Serializable {

    @MongoId
    @JsonIgnore
    private String userId;

    @NotNull(message = "email cannot be null")
    @Email(message = "email format is not correct")
    @Size(min = 6)
    private String email;

    @Size(min = 6)
    @NotNull(message = "password cannot be null")
    private String password;

    @JsonIgnore
    private boolean isActive=true;
}
