package me.iseunghan.tutorialspringsecurityjwt.account;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void addAccountTest() {
        // Given
        Account account = Account.builder()
                .username("username")
                .password("password")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.MANAGER, AccountRole.USER))
                .build();

        // When
//        account.addAccountRoles(AccountRole.ADMIN, AccountRole.USER, AccountRole.MANAGER);


        // Then
        Set<AccountRole> roles = account.getRoles();
        assertEquals(roles.size(), 3);
        assertTrue(roles.contains(AccountRole.ADMIN));
        assertTrue(roles.contains(AccountRole.MANAGER));
        assertTrue(roles.contains(AccountRole.USER));
    }
}