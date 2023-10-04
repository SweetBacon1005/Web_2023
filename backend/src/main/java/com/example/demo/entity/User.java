package com.example.demo.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(name = "Created")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date created;

    @Column
    private String phone;

    @Column
    private boolean userStatus;
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="User_Role",
    joinColumns = @JoinColumn(name="userId"),
    inverseJoinColumns = @JoinColumn(name="roleId"))
    private Set<Role> listRoles=new HashSet<>();
}
