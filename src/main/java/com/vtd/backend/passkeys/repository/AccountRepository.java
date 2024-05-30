package com.vtd.backend.passkeys.repository;

import com.vtd.backend.passkeys.entities.AccountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<AccountEntity, String> {
    Optional<AccountEntity> findByUsername(String username);
}
