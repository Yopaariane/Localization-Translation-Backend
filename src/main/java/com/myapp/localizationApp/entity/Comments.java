package com.myapp.localizationApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table (name = "comments")
public class Comments {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Terms term;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @UpdateTimestamp
    private LocalDateTime created_at;
}
