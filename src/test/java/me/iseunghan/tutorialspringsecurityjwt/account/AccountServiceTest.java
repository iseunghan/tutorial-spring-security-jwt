package me.iseunghan.tutorialspringsecurityjwt.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AccountServiceTest {

    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;

    private Account account;

    @BeforeEach
    public void setUp() {
        // given
        int random = (int) (Math.random() * 100);
        AccountDto accountDto = AccountDto.builder()
                                    .username("user" + random)
                                    .password("pass")
                                    .build()
        ;

        this.account = accountService.addAccount(accountDto);
    }

    @Test
    @DisplayName("1. Account를 생성할 수 있다.")
    public void addAccountTest() {
        // given
        AccountDto accountDto = AccountDto.builder()
                .username("user")
                .password("pass")
                .build()
        ;

        // when
        Account account = accountService.addAccount(accountDto);

        // then
        assertEquals(account.getUsername(), accountDto.getUsername());
        assertTrue(passwordEncoder.matches(accountDto.getPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("2. 하나의 Account를 조회할 수 있다.")
    public void getAccountTest() {
        // given

        // when
        Account savedAccount = accountService.getAccountByUsername(this.account.getUsername());

        // then
        assertEquals(savedAccount.getUsername(), this.account.getUsername());
        assertEquals(savedAccount.getPassword(), this.account.getPassword());
    }

    @Test
    @DisplayName("3. 모든 Account를 조회할 수 있다.")
    public void getAccountListTest() {
        // given

        // when
        List<Account> accountList = accountService.getAccountList();

        // then
        assertTrue(accountList.size() != 0);
    }

    @Test
    @DisplayName("4. Account를 수정할 수 있다.")
    public void updateAccountTest() {
        // given
        AccountDto accountDto = AccountDto.builder()
                .username("user44")
                .password("pass44")
                .build()
        ;

        // when
        Account savedAccount = accountService.updateAccount(this.account.getId(), accountDto);

        // then
        assertEquals(savedAccount.getUsername(), accountDto.getUsername());
        assertTrue(passwordEncoder.matches(accountDto.getPassword(), savedAccount.getPassword()));
    }

    @Test
    @DisplayName("5. Account를 삭제할 수 있다.")
    public void deleteAccountTest() {
        // given

        // when
        accountService.deleteAccount(this.account.getId());

        // then
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> accountService.getAccount(this.account.getId()));
        assertEquals(nfe.getErrorMessage(), "존재하지 않는 유저입니다. [id] : " + this.account.getId());
    }
}