package com.intheeast.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
//@DynamicUpdate
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity 
    implements Comparable<Comment> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COMMENT_ID")
    private Long id;
    
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;
    
    
    @Size(min = 1, max = 100)
    private String name;    

    @Size(min = 1, max = 100)
    @Email
    private String email;

    @Lob
    private String text;

    @Column(name = "ip_addr")
    private String ipAddress;
    

    @Override
    public int compareTo(Comment o) {
        if (this.creationDate == null || o.creationDate == null) {
            return 0;
        }
        return this.creationDate.compareTo(o.creationDate);
    }
}