package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;

/**
 * UserRepository - Spring Data JPA repository for UserRecord entities.
 * 
 * This interface provides database operations for user management.
 * Spring Data JPA automatically implements these methods based on naming conventions.
 */
public interface UserRepository extends CrudRepository<UserRecord, Long> {
    
    /**
     * Find a user by their ID.
     * 
     * Note: This method returns Optional<UserRecord> in newer Spring Data JPA versions.
     * We use the inherited findById(Long) method from CrudRepository instead.
     * 
     * @param id The user ID
     * @return The user record wrapped in Optional, or empty if not found
     */
    // UserRecord findById(long id); // Commented out - using inherited method
    
    /**
     * Find a user by their name.
     * 
     * Spring Data JPA automatically implements this method based on the naming convention:
     * - "findBy" + field name (Name) = findBy + Name
     * - Returns the first user with matching name, or null if not found
     * 
     * @param name The user's name
     * @return The user record, or null if not found
     */
    UserRecord findByName(String name);
}
