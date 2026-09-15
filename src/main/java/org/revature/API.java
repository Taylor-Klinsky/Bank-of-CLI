package org.revature;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class API {
    static final String WELCOME_MESSAGE = "Welcome to the Revature Bank!";
    static final String MENU_OPTIONS = "Options:\n- Deposit\n- Withdraw\n- Transfer\n- View transactions";
    static final String ACCOUNT_ID_REQUEST = "Please enter your account ID: ";
    static final String PIN_REQUEST = "Please enter your PIN: ";

    private static final Logger logger = LoggerFactory.getLogger(API.class);

    public static void logIn(Scanner scanner) {
        boolean success = false;

        while (!success) {
            // Read in account ID and PIN
            System.out.print(ACCOUNT_ID_REQUEST);
            while (!scanner.hasNextInt()) {
                System.out.print("That is not a valid account ID. Please ensure it includes only numbers.\n");
                logger.error("Non-numeric account ID entered");
                System.out.print(ACCOUNT_ID_REQUEST);
                scanner.next();
            }
            int accountID = scanner.nextInt();

            System.out.print(PIN_REQUEST);
            while (!scanner.hasNextInt()) {
                System.out.print("That is not a valid PIN. Please ensure it includes only numbers.\n");
                logger.error("Non-numeric PIN entered");
                System.out.print(PIN_REQUEST);
                scanner.next();
            }
            int pin = scanner.nextInt();

            // Check if account ID and PIN combination is valid
            if (Business.validateLogin(accountID, pin)) {
                success = true;
            } else {
                System.out.print("Sorry, we couldn't find that account.");
                logger.error("Account ID and PIN combination not found in database");
            }
        }
    }
}
