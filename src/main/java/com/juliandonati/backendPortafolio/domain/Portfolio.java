package com.juliandonati.backendPortafolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juliandonati.backendPortafolio.security.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portfolios")

@Data
public class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany(mappedBy="modifiablePortfolios")
    private Set<User> authorizedUsers = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "presentation_id")
    private Presentation presentation;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "about_me_id")
    private AboutMe aboutMe;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Degree> degrees = new HashSet<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Job> experience = new HashSet<>();

    @Column(length = 8, nullable = true)
    @ColumnDefault("'#000000'")
    private String primaryColorHex;
    @Column(length = 8, nullable = true)
    @ColumnDefault("'#ffffff'")
    private String secondaryColorHex;

    public void addAuthorizedUser(User user) {
        authorizedUsers.add(user);
        user.getModifiablePortfolios().add(this);
    }

    public void removeAuthorizedUser(User user) {
        authorizedUsers.remove(user);
        user.getModifiablePortfolios().remove(this);
    }


    public void addDegree(Degree degree) {
        degrees.add(degree);
        degree.setPortfolio(this);
    }

    public void removeDegree(Degree degree) {
        degrees.remove(degree);
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setPortfolio(this);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }


    public void addExperience(Job job) {
        experience.add(job);
        job.setPortfolio(this);
    }

    public void removeExperience(Job job) {
        experience.remove(job);
    }


}
