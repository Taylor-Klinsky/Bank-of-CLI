package org.revature.api;

import org.revature.exception.NotLoggedInException;
import org.revature.service.AccountService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class BankRepl {
    private final Scanner scanner = new Scanner(System.in);
    private final AccountService accountService;

    public BankRepl(AccountService accountService) { this.accountService = accountService; }

    // Starts REPL and contains flow for user input
    public void run() {
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            try {
                if (!handle(command)) return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // boolean flag indicates whether to continue
    private boolean handle(String command) throws Exception {
        switch (command) {
            case "deposit" -> deposit();
            case "withdraw" -> withdraw();
            case "transfer" -> transfer();
            case "balance" -> balance();
            case "help" -> printHelp();
            case "exit" -> {
                return false;
            }
            case "login" -> logIn();
            case "logout" -> logOut();
            default -> throw new IllegalArgumentException("Invalid command: " + command);
        }
        return true;
    }

    private void printHelp() {
        System.out.print("Available commands: \n");
        if (accountService.isLoggedIn()) {
            System.out.print("deposit - Deposit funds into your account\n");
            System.out.print("withdraw - Withdraw funds from your account\n");
            System.out.print("transfer - Transfer funds from one account to another\n");
            System.out.print("balance - Check your account's balance\n");
            System.out.print("transactions - List your recent transactions\n");
            System.out.print("logout - Log out of this account\n");
        } else {
            System.out.print("login - Log into an account\n");
            System.out.print("create - Create a new account\n");
        }
        System.out.print("help - Display the list of commands\n");
        System.out.print("exit - Exit the application\n");
    }

    private void logIn() {
        System.out.print("Account number: ");
        int id = readInt();
        System.out.print("PIN: ");
        int pin = readInt();
        try {
            accountService.logIn(id, pin);
            System.out.print("Logged in\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void logOut() {
        accountService.logOut();
        System.out.print("Logged out\n");
    }

    private void deposit() {
            System.out.print("Deposit amount: $");
            BigDecimal amount = readBigDecimal();
            try {
                accountService.deposit(amount);
                System.out.print("Successfully deposited " + formatMoney(amount) + "\n");
                balance();
            } catch (NotLoggedInException e) {
                System.out.println(e.getMessage());
            }
    }

    private void withdraw() {
        System.out.print("Withdraw amount: $");
        BigDecimal amount = readBigDecimal();
        try {
            accountService.withdraw(amount);
            System.out.print("Successfully withdrew " + formatMoney(amount) + "\n");
            balance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void transfer() {
        System.out.print("Account to transfer to: ");
        long toAccount = readLong();
        System.out.print("Amount to transfer: $");
        BigDecimal amount = readBigDecimal();
        try {
            accountService.transfer(toAccount, amount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void balance() {
        try {
            BigDecimal balance = accountService.getBalance();
            System.out.print("Current balance: " + formatMoney(balance) + "\n");
        } catch (NotLoggedInException e) {
            System.out.println(e.getMessage());
        }
    }

    private void create() {
        int pin;
        int pinConfirm;

        do {
            System.out.print("Enter a PIN: ");
            pin = readInt();
            System.out.print("Confirm PIN: ");
            pinConfirm = readInt();

            if (pin != pinConfirm) System.out.print("PIN does not match, please try again\n");
        } while (pinConfirm != pin);

        accountService.addAccount(pin);
        System.out.print("Account created successfully\n");
        System.out.print("Account number: " + accountService.getAccountNumber());
        balance();
    }

    // Helper methods
    private BigDecimal readBigDecimal() {
        while (true) {
            String input = scanner.nextLine().trim();

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private int readInt() {
        while (true) {
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private long readLong() {
        while (true) {
            String input = scanner.nextLine().trim();

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private String formatMoney(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}
