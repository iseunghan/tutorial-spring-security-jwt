package me.iseunghan.tutorialspringsecurityjwt.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

// UserAdapter를 사용하면, UserDetails의 모든 메소드를 구현할 필요없이 간편하게 구현 가능하다.
@Getter
public class AccountAdapter extends User {

    private final Account account;

    public AccountAdapter(Account account) {
        super(account.getUsername(), account.getPassword(), getAuthority(account.getRoles()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> getAuthority(Set<AccountRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
    }
}
