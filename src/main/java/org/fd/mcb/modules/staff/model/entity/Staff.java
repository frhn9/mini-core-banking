
package org.fd.mcb.modules.staff.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.fd.mcb.modules.staff.enums.StaffRole;
import org.fd.mcb.modules.staff.enums.StaffStatus;

@Entity
@Table(name = "staff")
@Getter
@Setter
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private StaffRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StaffStatus status;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
}
