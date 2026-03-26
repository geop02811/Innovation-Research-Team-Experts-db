package org.innov.expertdb.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users") // Maps to the Flyway 'users' table
@Data // Lombok: Generates Getters, Setters, toString, etc.
@NoArgsConstructor // Lombok: Generates a no-args constructor
@AllArgsConstructor // Lombok: Generates an all-args constructor
@Builder // Lombok: Enables the Builder pattern for this class
public class User {
    
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM) // Generates a random UUID for the ID
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // This tells JPA: "Look at the 'user_roles' table. The 'user_id' column links back to me. 
    // The 'role' column contains my Enums as Strings."
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();
}
