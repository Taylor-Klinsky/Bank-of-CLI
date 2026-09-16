package org.revature.api;

import org.revature.service.AccountService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class BankRepl {
    private final Scanner scanner = new Scanner(System.in);
    private final AccountService accountService;
    private boolean loggedIn = false;

    public BankRepl(AccountService accountService) { this.accountService = accountService; }

    // Starts REPL and contains flow for user input
    public void run() {
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            try {
                if (!handle(command)) return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // boolean flag indicates whether to continue
    private boolean handle(String command) throws IllegalArgumentException{
        switch (command) {
            case "deposit" -> deposit();
            case "help" -> printHelp();
            case "exit" -> { return false; }
            default -> throw new IllegalArgumentException("Invalid command: " + command);
        }
        return true;
    }

    private void printHelp() {
        System.out.print("Available commands: \n");
        if (loggedIn) {
            System.out.print("deposit - Deposit funds into your account\n");
            System.out.print("withdraw - Withdraw funds from your account\n");
            System.out.print("transfer - Transfer funds from one account to another\n");
            System.out.print("check - Check your account's balance\n");
            System.out.print("logout - Log out of this account\n");
        } else {
            System.out.print("login - Log into an account\n");
        }
        System.out.print("help - Display the list of commands\n");
        System.out.print("exit - Exit the application\n");
    }

    private void logIn() {
        System.out.print("Account number: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("PIN: ");
        int pin = Integer.parseInt(scanner.nextLine().trim());
        try {
            accountService.findAccount(id, pin);
            loggedIn = true;
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            loggedIn = false;
        }
    }

    private void deposit() {
        System.out.print("Deposit amount: ");
        double amount = Integer.parseInt(scanner.nextLine().trim());

    }
}
