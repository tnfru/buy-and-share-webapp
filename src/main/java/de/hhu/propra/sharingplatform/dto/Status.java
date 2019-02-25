package de.hhu.propra.sharingplatform.dto;

public enum Status {
    /**
     * Resolved: Item-owner gets bail.
     * Rejected: Borrower keeps bail.
     * Pending: Waiting for admin to resolve.
     */
    PENDING, ACCEPTED, REJECTED, RESOLVED
}

