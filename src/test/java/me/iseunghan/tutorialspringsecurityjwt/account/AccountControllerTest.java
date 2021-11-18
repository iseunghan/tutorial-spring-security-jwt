package me.iseunghan.tutorialspringsecurityjwt.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mock;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountController accountController;

    @Autowired
    AccountService accountService;

    private Account account;

    @BeforeEach
    public void setUp() {
        // given
        int random = (int) (Math.random() * 100);

        AccountDto accountDto = AccountDto.builder()
                                    .username("username" + random)
                                    .password("pass")
                                    .build()
        ;

        this.account = accountService.addAccount(accountDto);
    }

    @Test
    @DisplayName("1. 하나의 Account를 생성할 수 있다.")
    public void addAccount() throws Exception {
        // given
        AccountDto accountDto = AccountDto.builder()
                                .username("user")
                                .password("pass")
                                .build()
        ;

        // when & then
        mock.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(accountDto.getUsername()))
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.put").exists())
                .andExpect(jsonPath("$._links.delete").exists())
        ;
    }

    @Test
    @DisplayName("2. 하나의 Account를 조회할 수 있다.")
    public void getAccount() throws Exception {
        // given

        // when & then
        mock.perform(get("/account/" + account.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.put").exists())
                .andExpect(jsonPath("$._links.delete").exists())
        ;
    }

    @Test
    @DisplayName("3. 모든 Account를 조회할 수 있다.")
    public void getAccountList() throws Exception {
        // given

        // when & then
        mock.perform(get("/account"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.accountResourceList.[0].id").exists())
                .andExpect(jsonPath("$._embedded.accountResourceList.[0].username").exists())
                .andExpect(jsonPath("$._embedded.accountResourceList.[0].password").exists())
                .andExpect(jsonPath("$._embedded.accountResourceList.[0]._links.self").exists())
                .andExpect(jsonPath("$._embedded.accountResourceList.[0]._links.put").exists())
                .andExpect(jsonPath("$._embedded.accountResourceList.[0]._links.delete").exists())
                .andExpect(jsonPath("$._links.self").exists())
        ;
    }

    @Test
    @DisplayName("4. 하나의 Account를 수정할 수 있다.")
    public void updateAccount() throws Exception {
        // given
        AccountDto accountDto = AccountDto.builder()
                                    .username("edit_username")
                                    .password("edit_password")
                                    .build()
        ;

        // when & then
        mock.perform(put("/account/" + this.account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("edit_username"))
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.put").exists())
                .andExpect(jsonPath("$._links.delete").exists())
        ;
    }

    @Test
    @DisplayName("5. 하나의 Account를 삭제할 수 있다.")
    public void deleteAccount() throws Exception {
        // given

        // when & then
        mock.perform(delete("/account/" + this.account.getId()))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }
}