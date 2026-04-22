package com.jel.spys.model;

import com.jel.spys.entity.Role;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserProfileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithProfileDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String credentialNumber;

    public UserWithProfileDTO(UserProfileEntity userProfileEntity) {
        UserEntity userEntity = userProfileEntity.getUser();
       this.id = userEntity.getId();
       this.email = userEntity.getEmail();
       this.firstName = userEntity.getFirstName();
       this.lastName = userEntity.getLastName();
       this.credentialNumber = userProfileEntity.getCredentialNumber();
    }
}
