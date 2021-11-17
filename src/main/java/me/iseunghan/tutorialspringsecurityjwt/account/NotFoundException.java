package me.iseunghan.tutorialspringsecurityjwt.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class NotFoundException extends RuntimeException{

    @Getter
    final String errorMessage;
}
