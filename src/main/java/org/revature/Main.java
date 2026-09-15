package org.revature;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Main.run(scanner);

        scanner.close();
    }

    private static void run(Scanner scanner) {
        API.logIn(scanner);
    }
}