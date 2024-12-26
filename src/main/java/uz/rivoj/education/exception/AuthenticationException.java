package uz.rivoj.education.exception;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class AuthenticationException extends AuthenticationCredentialsNotFoundException {
    public AuthenticationException(String msg) {
        super(msg);
    }
}