package com.adi;


import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        try { // Initialize the database connection
            NBADatabase.initialize();
        } catch (Exception e) {
            System.out.println("Error initializing database.");
            return;
        }

        NBADatabase.setScanner(scanner); // Share the scanner
        boolean running = true;

        while (running) {

            printMenu();

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {

                case 1:
                    try {
                        NBADatabase.getTodaysGames();
                    } catch (Exception e) {
                        System.out.println("Error loading today's games.");
                    }
                    break;

                case 2:
                    try {
                        NBADatabase.searchGames();
                    } catch (Exception e) {
                        System.out.println("Error searching games. Please ensure date is in MM/dd/yyyy format.");
                    }
                    break;

                case 3:
                    try {
                        NBADatabase.getStandings();
                    } catch (Exception e) {
                        System.out.println("Error loading standings.");
                    }
                    break;

                case 4:
                    try {
                        NBADatabase.getTeamStats();
                    } catch (Exception e) {
                        System.out.println("Error loading team stats. Please ensure team name is correct.");
                    }
                    break;

                case 5:
                    try {
                        NBADatabase.getPlayerStats();
                    } catch (Exception e) {
                        System.out.println("Error loading player stats. Please ensure player name is correct.");
                    }
                    break;

                case 6:
                    exit(running);
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public static void printMenu() {

        System.out.println("\n====================================");
        System.out.println("        NBATRACKER");
        System.out.println("====================================");
        System.out.println("1. Today's Games");
        System.out.println("2. Search Games by Date");
        System.out.println("3. View League Standings");
        System.out.println("4. Get Team Stats");
        System.out.println("5. Get Player Stats");
        System.out.println("6. Exit");
        System.out.println("====================================");
        System.out.print("\nChoose option: ");
    }

    public static void exit(boolean running) {

        running = false;
        System.out.println("Goodbye.");
        scanner.close();
        System.exit(0);

    }

}