package org.fd.mcb.shared.enums;

/**
 * Enumeration representing the type of user in the system.
 * Used for unified authentication to differentiate between staff and customer users.
 */
public enum UserType {
    /**
     * Staff user (teller, admin, auditor)
     */
    STAFF,

    /**
     * Customer user (account holder)
     */
    CUSTOMER
}
