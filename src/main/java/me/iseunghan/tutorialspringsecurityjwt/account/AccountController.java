package me.iseunghan.tutorialspringsecurityjwt.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping
    public ResponseEntity addAccount() {
        List<Account> accounts = accountService.addTestAccount();

        return ResponseEntity.created(URI.create("/account")).body(accounts);
    }
}
