package org.revature.api;

import java.util.Scanner;

public class BankRepl {
    private final Scanner scanner = new Scanner(System.in);

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
            case "help" -> printHelp();
            case "exit" -> {return false;}
            default -> throw new IllegalArgumentException("Invalid command: " + command);
        }
        return true;
    }

    private static void printHelp() {
        System.out.print("Available commands: \n");
        System.out.print("deposit - Deposit funds into your account\n");
        System.out.print("withdraw - Withdraw funds from your account\n");
        System.out.print("transfer - Transfer funds from one account to another\n");
        System.out.print("check - Check your account's balance\n");
        System.out.print("help - Display the list of commands\n");
        System.out.print("exit - Exit the application\n");
    }
}
