package dev.adambertalan.interview.wecan.entity;

import javax.persistence.*;

@Entity
public class UserEntity extends BaseEntity {

    @Column(unique = true)
    private String userName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
