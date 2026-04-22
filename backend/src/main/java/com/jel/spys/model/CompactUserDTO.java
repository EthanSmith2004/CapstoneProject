package com.jel.spys.model;

import com.jel.spys.entity.Role;
import com.jel.spys.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompactUserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isAdmin;

    public CompactUserDTO(UserEntity userEntity) {
       this.id = userEntity.getId();
       this.email = userEntity.getEmail();
       this.firstName = userEntity.getFirstName();
       this.lastName = userEntity.getLastName();
       this.isAdmin = userEntity.getRoles().contains(Role.ADMIN);
    }
}
