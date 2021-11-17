package me.iseunghan.tutorialspringsecurityjwt.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
                .username(accountDto.getUsername())
                .password(passwordEncoder.encode(accountDto.getPassword()))
                .roles(Set.of(AccountRole.USER))
                .build()
        ;

        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, AccountDto accountDto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다. [id] : " + id));

        if(StringUtils.hasText(accountDto.getUsername())) {
            account.setUsername(accountDto.getUsername());
        }
        if(StringUtils.hasText(accountDto.getPassword())) {
            account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        }

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다. [id] : " + id));

        accountRepository.delete(account);
    }
}
