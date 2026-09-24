package org.revature.api;

import org.revature.TransactionType;
import org.revature.domain.Transaction;
import org.revature.exception.NotLoggedInException;
import org.revature.service.AccountService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class BankRepl {
    private final Scanner scanner = new Scanner(System.in);
    private final AccountService accountService;

    public BankRepl(AccountService accountService) { this.accountService = accountService; }

    // Starts REPL and contains flow for user input
    public void run() {
        System.out.print("Welcome to the Bank of CLI\n\n");
        printHelp();

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
    private boolean handle(String command) {
        switch (command) {
            case "deposit" -> deposit();
            case "withdraw" -> withdraw();
            case "transfer" -> transfer();
            case "balance" -> balance();
            case "trans" -> transactions();
            case "help" -> printHelp();
            case "exit" -> {
                return false;
            }
            case "create" -> create();
            case "login" -> logIn();
            case "logout" -> logOut();
            case "pin" -> pin();
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
            System.out.print("trans - List your recent transactions\n");
            System.out.print("pin - Change pin\n");
            System.out.print("logout - Log out of this account\n");
        } else {
            System.out.print("login - Log into an account\n");
        }
        System.out.print("create - Create a new account\n");
        System.out.print("help - Display the list of commands\n");
        System.out.print("exit - Exit the application\n");
    }

    private void logIn() {
        System.out.print("Account number: ");
        long accountNumber = readLong();
        System.out.print("PIN: ");
        String pin = scanner.nextLine().trim();
        try {
            accountService.logIn(accountNumber, pin);
            System.out.print("Logged in\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void create() {
        String pin;
        String pinConfirm;

        do {
            System.out.print("Enter a PIN: ");
            pin = readPin();
            System.out.print("Confirm PIN: ");
            pinConfirm = readPin();

            if (pin.compareTo(pinConfirm) != 0) System.out.print("PIN does not match, please try again\n");
        } while (pin.compareTo(pinConfirm) != 0);

        accountService.addAccount(pin);
        System.out.print("Account created successfully\n");
        System.out.print("Account number: " + accountService.getAccountNumber() + "\n");
        balance();
    }

    private void logOut() {
        accountService.logOut();
        System.out.print("Logged out\n");
    }

    // Must be logged in for the below functions
    private void deposit() {
        if (!accountService.isLoggedIn()) {
            System.out.print("You must be logged in to make a deposit\n");
            return;
        }

        System.out.print("Deposit amount: $");
        BigDecimal amount = readBigDecimal();
        try {
            accountService.deposit(amount);
            System.out.print("Successfully deposited " + formatMoney(amount) + "\n");
            balance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void withdraw() {
        if (!accountService.isLoggedIn()) {
            System.out.print("You must be logged in to make a withdrawal\n");
            return;
        }

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
        if (!accountService.isLoggedIn()) {
            System.out.print("You must be logged in to make a transfer\n");
            return;
        }

        System.out.print("Account to transfer to: ");
        long toAccount = readLong();
        System.out.print("Amount to transfer: $");
        BigDecimal amount = readBigDecimal();
        try {
            accountService.transfer(toAccount, amount);
            System.out.print("Transfer of " + formatMoney(amount) + " to account " + toAccount + " success\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        balance();
    }

    private void balance() {
        try {
            BigDecimal balance = accountService.getBalance();
            System.out.print("Current balance: " + formatMoney(balance) + "\n");
        } catch (NotLoggedInException e) {
            System.out.println(e.getMessage());
        }
    }

    private void transactions() {
        if (!accountService.isLoggedIn()) {
            System.out.print("You must be logged in to view transactions\n");
            return;
        }

        System.out.print("How many transactions would you like to show? Enter 0 to view all\n");
        System.out.print("Show this many: ");

        try {
            List<Transaction> transactions = accountService.getTransactions(accountService.getAccountNumber(), readPositiveInt());

            printTransactionHeader();

            for (Transaction transaction : transactions) {
                printTransaction(transaction);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void pin() {
        if (!accountService.isLoggedIn()) {
            System.out.print("You must be logged in to change your PIN\n");
            return;
        }

        System.out.print("Enter your old PIN: ");

        try {
            String pin = readPin();
            accountService.checkPin(accountService.getAccountNumber(), pin);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.print("Enter new PIN: ");
        String newPin = readPin();
        System.out.print("Reenter new PIN: ");
        String newPinReentry = readPin();

        if (newPin.compareTo(newPinReentry) != 0) {
            System.out.print("PINs do not match\n");
        } else {
            accountService.setPin(newPin);
        }

        System.out.print("PIN updated\n");
    }

    // Helper methods
    private void printTransaction(Transaction transaction) {
        String amount = formatMoney(transaction.getAmount());

        if (transaction.getType().equals(TransactionType.DEPOSIT)) {
            amount = "+" + amount;
        } else if (transaction.getType().equals(TransactionType.TRANSFER_IN)) {
            amount = "+" + amount;
        } else {
            amount = "-" + amount;
        }

        String relatedAccount = transaction.getRelatedAccount() == null
                ? "-"
                : String.valueOf(transaction.getRelatedAccount());

        LocalDateTime localDateTime = transaction.getTimestamp().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm");
        String formattedDate = localDateTime.format(formatter);

        System.out.printf(
                "%-20s %-16s %12s %12s%n",
                formattedDate,
                transaction.getType(),
                amount,
                relatedAccount
        );
    }

    private void printTransactionHeader() {
        System.out.println();
        System.out.println("Transaction History");
        System.out.println("────────────────────────────────────────────────────────────────────");
        System.out.printf("%-20s %-16s %-12s %-15s%n",
                "Date", "Type", "Amount", "Related Account");
        System.out.println("────────────────────────────────────────────────────────────────────");
    }

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

    private int readPositiveInt() {
        while (true) {
            String input = scanner.nextLine().trim();

            try {
                int result = Integer.parseInt(input);
                if (result < 0) {
                    System.out.print("Please enter a positive number: ");
                } else { return result; }
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

    private String readPin() {
        while (true) {
            String input = scanner.nextLine().trim();

            try {
                accountService.validatePinFormat(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.print("Please try again: ");
            }
        }
    }

    private String formatMoney(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}
