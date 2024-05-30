package com.vtd.backend.passkeys.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class AccountEntity {

    @Id
    private String id;
    private String username;
    private String accountId;
    private PasswordEntity password;
    private List<PasskeyEntity> passkeys;

    public AccountEntity(String username, String accountId, PasswordEntity password, List<PasskeyEntity> passkeys) {
        this.username = username;
        this.accountId = accountId;
        this.password = password;
        this.passkeys = passkeys;
    }
}
