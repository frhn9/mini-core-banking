package org.fd.mcb.shared.security;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.master.model.entity.Customer;
import org.fd.mcb.modules.staff.enums.StaffStatus;
import org.fd.mcb.modules.staff.model.entity.Staff;
import org.fd.mcb.shared.enums.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation that wraps either a Staff or Customer entity.
 * This allows unified authentication for both user types in the system.
 */
@Slf4j
@EqualsAndHashCode(of = "username")
@Builder
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;  // CIN for customers, username for staff
    private final String password;
    private final UserType userType;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonLocked;
    private final boolean enabled;

    /**
     * Creates a UserPrincipal from a Staff entity.
     *
     * @param staff the staff entity
     * @return UserPrincipal instance
     */
    public static UserPrincipal fromStaff(Staff staff) {
        return UserPrincipal.builder()
                .userId(staff.getId())
                .username(staff.getUsername())
                .password(staff.getPasswordHash())
                .userType(UserType.STAFF)
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + staff.getRole().name())
                ))
                .accountNonLocked(true)  // Staff accounts are never locked via account_locked field
                .enabled(staff.getStatus() == StaffStatus.ACTIVE)
                .build();
    }

    /**
     * Creates a UserPrincipal from a Customer entity.
     *
     * @param customer the customer entity
     * @return UserPrincipal instance
     */
    public static UserPrincipal fromCustomer(Customer customer) {
        return UserPrincipal.builder()
                .userId(customer.getId())
                .username(customer.getCin())
                .password(customer.getPasswordHash())
                .userType(UserType.CUSTOMER)
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                ))
                .accountNonLocked(customer.getAccountLocked() != null && !customer.getAccountLocked())
                .enabled(customer.getStatus() != null &&
                        customer.getStatus().name().equals("ACTIVE"))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // We don't track account expiration
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // We don't track password expiration
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the user ID.
     *
     * @return user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Gets the user type.
     *
     * @return user type
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Gets the role name without the ROLE_ prefix.
     *
     * @return role name (e.g., "ADMIN", "CUSTOMER")
     */
    public String getRole() {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.replace("ROLE_", ""))
                .orElse("UNKNOWN");
    }
}
