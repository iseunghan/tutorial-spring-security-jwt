package me.iseunghan.tutorialspringsecurityjwt.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Account> addTestAccount() {
        Account account = Account.builder()
                .username("admin")
                .password("pass")
                .roles(Set.of(AccountRole.ADMIN))
                .build();
        Account account1 = Account.builder()
                .username("manager")
                .password("pass")
                .roles(Set.of(AccountRole.MANAGER))
                .build();
        Account account2 = Account.builder()
                .username("user")
                .password("pass")
                .roles(Set.of(AccountRole.USER))
                .build();

        accountRepository.save(account);
        accountRepository.save(account1);
        accountRepository.save(account2);

        return List.of(account, account1, account2);
    }
}
