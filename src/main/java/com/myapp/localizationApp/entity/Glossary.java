package com.myapp.localizationApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table (name = "glossary")
public class Glossary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private String initial_translation;

    @Column(nullable = false)
    private String context;

    @Column(nullable = false)
    private String translatable;

    @Column(nullable = true)
    private String comment;

    @Column(nullable = false)
    private String status;

   @UpdateTimestamp
    private LocalDateTime create_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Organization organization;
}
