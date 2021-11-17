package me.iseunghan.tutorialspringsecurityjwt.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DuplicateException extends RuntimeException{

    @Getter
    final String errorMessage;
}
