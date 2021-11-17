package me.iseunghan.tutorialspringsecurityjwt.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountResource accountResource;

    @GetMapping
    public ResponseEntity getAccountList() {
        List<Account> accountList = accountService.getAccountList();
        CollectionModel<AccountResource> accountResources = accountResource.toCollectionModel(accountList);

        return ResponseEntity.ok(accountResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getAccount(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        AccountResource accountResource = this.accountResource.toModel(account);

        return ResponseEntity.ok(accountResource);
    }

    @PostMapping
    public ResponseEntity addAccount(@RequestBody AccountDto accountDto) {
        Account account = accountService.addAccount(accountDto);
        AccountResource accountResource = this.accountResource.toModel(account);
        URI uri = linkTo(AccountController.class).withSelfRel().toUri();

        return ResponseEntity.created(uri).body(accountResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto) {
        Account account = accountService.updateAccount(id, accountDto);
        AccountResource accountResource = this.accountResource.toModel(account);

        return ResponseEntity.ok(accountResource);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);

        return ResponseEntity.ok("DELETE SUCCESS by id : " + id);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity NotFoundExceptionHandler(NotFoundException e) {
        return ResponseEntity.badRequest().body(e.getErrorMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity DuplicateExceptionHandler(DuplicateException e) {
        return ResponseEntity.badRequest().body(e.getErrorMessage());
    }
}
