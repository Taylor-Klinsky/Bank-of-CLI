package org.revature.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.revature.domain.Account;
import org.revature.exception.AccountNotFoundException;
import org.revature.exception.InvalidCredentialException;
import org.revature.exception.NotLoggedInException;
import org.revature.persistence.AccountDAO;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    private AccountDAO accountDAO;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void logInWithCorrectCredentialsLogsUserIn() {
        Account account = new Account(12345678L, "1234", new BigDecimal("1234.56"));

        when(accountDAO.getAccountByAccountNumber(12345678L)).thenReturn(account);
        accountService.logIn(12345678L, "1234");

        verify(accountDAO).getAccountByAccountNumber(12345678L);
        assertTrue(accountService.isLoggedIn());
    }

    @Test
    void logInWithIncorrectPinThrowsException() {
        Account account = new Account(12345678L, "1234", new BigDecimal("1234.56"));

        when(accountDAO.getAccountByAccountNumber(12345678L)).thenReturn(account);

        assertThrows(InvalidCredentialException.class, () -> accountService.logIn(12345678L, "0000"));
        verify(accountDAO).getAccountByAccountNumber(12345678L);
        assertFalse(accountService.isLoggedIn());
    }

    @Test
    void logInWithIncorrectAccountNumberThrowsException() {
        assertThrows(AccountNotFoundException.class, () -> accountService.logIn(0L, "0000"));
        verify(accountDAO).getAccountByAccountNumber(0L);
        assertFalse(accountService.isLoggedIn());
    }

    @Test
    void checkBalanceWhileNotLoggedInThrowsException() {
        assertThrows(NotLoggedInException.class, () -> accountService.getBalance());
    }
}
