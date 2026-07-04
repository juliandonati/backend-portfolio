package com.juliandonati.backendPortafolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "presentations")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Presentation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    @NotNull(message = "El nombre no puede ser nulo")
    private String name;

    @Column(length = 50)
    private String title;

    private String description;

    private String imgUrl;

    @Column(length = 254)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @OneToOne(mappedBy = "presentation")

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Portfolio portfolio;
}
