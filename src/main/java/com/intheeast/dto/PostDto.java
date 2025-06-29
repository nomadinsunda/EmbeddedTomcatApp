package com.intheeast.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto{
    
    private Long id;    
    
    @Size(min = 1, max = 100)
    private String name;
    
    @Size(min = 1, max = 100)
    @Email
    private String email;    
    
    @Column(name = "ip_addr")
    private String ipAddress;
    
    @Size(min = 1, max = 120)
    private String title;
    
    @Size(max = 250)
    private String web;    
    
    private String text;    
}
