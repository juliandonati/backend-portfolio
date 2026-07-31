package com.juliandonati.backendPortafolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "degrees")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Degree {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    @OnDelete(action = OnDeleteAction.CASCADE)

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Portfolio portfolio;
}
