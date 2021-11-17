package me.iseunghan.tutorialspringsecurityjwt.account;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class AccountResource extends RepresentationModel {

    @Autowired
    private ModelMapper modelMapper;

    @JsonUnwrapped
    private AccountDto accountDto;

    public AccountResource() {  }

    public AccountResource(AccountDto accountDto) {
        super.add(linkTo(AccountController.class).slash(accountDto.getId()).withSelfRel());
        super.add(linkTo(AccountController.class).slash(accountDto.getId()).withRel("PUT"));
        super.add(linkTo(AccountController.class).slash(accountDto.getId()).withRel("DELETE"));
        this.accountDto = accountDto;
    }

    public AccountResource toModel(Account account) {
        AccountDto accountDto = modelMapper.map(account, AccountDto.class);
        return new AccountResource(accountDto);
    }

    public CollectionModel<AccountResource> toCollectionModel(List<Account> accountList) {
        List<AccountResource> accountResourceList = accountList.stream().map(account -> new AccountResource(modelMapper.map(account, AccountDto.class)))
                .collect(Collectors.toList());
        CollectionModel<AccountResource> collectionModel = CollectionModel.of(accountResourceList);
        collectionModel.add(linkTo(AccountController.class).withSelfRel());

        return collectionModel;
    }
}
