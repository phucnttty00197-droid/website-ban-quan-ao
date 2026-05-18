package com.example.demo.Model.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name ="accounts")
public class Account {

    @Id
    @Column(name = "username")
    private String username;

    @Column(length = 255, nullable = false, name ="password")
    private String password;

    @Column(name ="fullname", length = 255, nullable = false)
    private String fullname;

    @Column(name ="email", length = 100, nullable = false)
    private String email;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name ="activated", nullable = false)
    private Boolean activated;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "account")
    @Builder.Default
    private List<Authority> authorities = new ArrayList<>();

    @PrePersist
    private void applyDefaults() {
        if (activated == null) {
            activated = true;
        }
        if (deleted == null)
            deleted = false;
    }

}
