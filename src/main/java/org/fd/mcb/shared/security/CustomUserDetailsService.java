package org.fd.mcb.shared.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.master.model.entity.Customer;
import org.fd.mcb.modules.master.model.repository.CustomerRepository;
import org.fd.mcb.modules.staff.model.entity.Staff;
import org.fd.mcb.modules.staff.repository.StaffRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService that loads users from both Staff and Customer tables.
 * This service attempts to find the user in both repositories and returns a
 * UserPrincipal with the appropriate user type.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;

    /**
     * Loads a user by username/CIN.
     * First attempts to find a staff member by username,
     * then attempts to find a customer by CIN.
     *
     * @param identifier the username (for staff) or CIN (for customers)
     * @return UserDetails representing the found user
     * @throws UsernameNotFoundException if no user is found
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Attempting to load user with identifier: {}", identifier);

        // Try to find staff first
        Staff staff = staffRepository.findByUsername(identifier).orElse(null);
        if (staff != null) {
            log.debug("Found staff user: {}", identifier);
            return UserPrincipal.fromStaff(staff);
        }

        // Try to find customer
        Customer customer = customerRepository.findByCin(identifier).orElse(null);
        if (customer != null) {
            log.debug("Found customer user with CIN: {}", identifier);
            return UserPrincipal.fromCustomer(customer);
        }

        log.warn("User not found with identifier: {}", identifier);
        throw new UsernameNotFoundException("User not found with identifier: " + identifier);
    }

    /**
     * Loads a user by username (staff only).
     *
     * @param username the staff username
     * @return UserPrincipal for the staff member
     * @throws UsernameNotFoundException if staff not found
     */
    public UserPrincipal loadStaffByUsername(String username) throws UsernameNotFoundException {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Staff not found with username: " + username));
        return UserPrincipal.fromStaff(staff);
    }

    /**
     * Loads a user by CIN (customers only).
     *
     * @param cin the customer CIN
     * @return UserPrincipal for the customer
     * @throws UsernameNotFoundException if customer not found
     */
    public UserPrincipal loadCustomerByCin(String cin) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByCin(cin)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with CIN: " + cin));
        return UserPrincipal.fromCustomer(customer);
    }
}
