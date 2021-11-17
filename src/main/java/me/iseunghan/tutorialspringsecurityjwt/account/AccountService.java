package me.iseunghan.tutorialspringsecurityjwt.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다. username : " + username));

        return new AccountAdapter(account);
    }

    public List<Account> getAccountList() {
        return accountRepository.findAll();
    }

    public Account getAccount(Long id) throws NotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다. [id] : " + id));
    }

    public Account getAccountByUsername(String username) throws NotFoundException {
        return accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다. [username] : " + username));
    }

    public Account addAccount(AccountDto accountDto) {
        accountRepository.findAccountByUsername(accountDto.getUsername())
                .ifPresent(account -> {
                    throw new DuplicateException("존재하는 Username입니다. [username] : " + account.getUsername());
                });

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
