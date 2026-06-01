package com.adi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NBADatabase {

    private static HashMap<String, Integer> playerIDs = new HashMap<>();
    private static HashMap<String, Integer> teamIDs = new HashMap<>();
    private static HashMap<String, String> teamAbbreviations = new HashMap<>();
    private static Scanner scanner;

    public static void initialize() throws Exception {

        teamIDs.put("Oklahoma City Thunder", 61);
        teamAbbreviations.put("OKC", "Oklahoma City Thunder");

        teamIDs.put("Detroit Pistons", 42);
        teamAbbreviations.put("DET", "Detroit Pistons");

        teamIDs.put("Los Angeles Clippers", 46);
        teamAbbreviations.put("LAC", "Los Angeles Clippers");

        teamIDs.put("Golden State Warriors", 43);
        teamAbbreviations.put("GSW", "Golden State Warriors");

        teamIDs.put("Boston Celtics", 36);
        teamAbbreviations.put("BOS", "Boston Celtics");

        teamIDs.put("New York Knicks", 54);
        teamAbbreviations.put("NYK", "New York Knicks");

        teamIDs.put("Toronto Raptors", 62);
        teamAbbreviations.put("TOR", "Toronto Raptors");

        teamIDs.put("Los Angeles Lakers", 47);
        teamAbbreviations.put("LAL", "Los Angeles Lakers");

        teamIDs.put("Philadelphia 76ers", 56);
        teamAbbreviations.put("PHI", "Philadelphia 76ers");

        teamIDs.put("Brooklyn Nets", 52);
        teamAbbreviations.put("BKN", "Brooklyn Nets");

        teamIDs.put("San Antonio Spurs", 60);
        teamAbbreviations.put("SAS", "San Antonio Spurs");

        teamIDs.put("Cleveland Cavaliers", 39);
        teamAbbreviations.put("CLE", "Cleveland Cavaliers");

        teamIDs.put("Sacramento Kings", 59);
        teamAbbreviations.put("SAC", "Sacramento Kings");

        teamIDs.put("Memphis Grizzlies", 48);
        teamAbbreviations.put("MEM", "Memphis Grizzlies");

        teamIDs.put("Miami Heat", 49);
        teamAbbreviations.put("MIA", "Miami Heat");

        teamIDs.put("Minnesota Timberwolves", 51);
        teamAbbreviations.put("MIN", "Minnesota Timberwolves");

        teamIDs.put("Houston Rockets", 44);
        teamAbbreviations.put("HOU", "Houston Rockets");

        teamIDs.put("Dallas Mavericks", 40);
        teamAbbreviations.put("DAL", "Dallas Mavericks");

        teamIDs.put("Portland Trail Blazers", 58);
        teamAbbreviations.put("POR", "Portland Trail Blazers");

        teamIDs.put("Atlanta Hawks", 35);
        teamAbbreviations.put("ATL", "Atlanta Hawks");

        teamIDs.put("Denver Nuggets", 41);
        teamAbbreviations.put("DEN", "Denver Nuggets");

        teamIDs.put("Washington Wizards", 64);
        teamAbbreviations.put("WAS", "Washington Wizards");

        teamIDs.put("Orlando Magic", 55);
        teamAbbreviations.put("ORL", "Orlando Magic");

        teamIDs.put("Indiana Pacers", 45);
        teamAbbreviations.put("IND", "Indiana Pacers");

        teamIDs.put("Phoenix Suns", 57);
        teamAbbreviations.put("PHO", "Phoenix Suns");

        teamIDs.put("Utah Jazz", 63);
        teamAbbreviations.put("UTA", "Utah Jazz");

        teamIDs.put("New Orleans Pelicans", 53);
        teamAbbreviations.put("NO", "New Orleans Pelicans");

        teamIDs.put("Charlotte Hornets", 37);
        teamAbbreviations.put("CHA", "Charlotte Hornets");

        teamIDs.put("Milwaukee Bucks", 50);
        teamAbbreviations.put("MIL", "Milwaukee Bucks");

        teamIDs.put("Chicago Bulls", 38);
        teamAbbreviations.put("CHI", "Chicago Bulls");

        String response = NBAService.getPlayers();
        JSONArray players = new JSONObject(response).getJSONArray("players");

        for (int i = 0; i < players.length(); i++) {
            JSONObject player = players.getJSONObject(i);
            String fullName = AccentRemover.removeAccents(player.getString("displayName"));
            playerIDs.put(fullName, player.getInt("personId"));
        }

    }

    private static int getPlayerID(String name) {
        return playerIDs.get(name);
    }

    public static void setScanner(Scanner scan) {
        scanner = scan;
    }

    private static Integer getTeamID(String name) {
        if (teamIDs.containsKey(name)) {
            return teamIDs.get(name);
        } else if (teamAbbreviations.containsKey(name)) {
            String abbrev = teamAbbreviations.get(name);
            return teamIDs.get(abbrev);
        } else {
            return null; // Not found
        }
    }

    public static void getTodaysGames() throws Exception {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        JSONObject response = new JSONObject(NBAService.getGames(today.format(formatter)));
        JSONArray games = response.getJSONArray("games");

        if (games.length() == 0) {
            System.out.println("No games found today.");
            menuReturn();
            return;
        }

        System.out.println("\nToday's Games:");

        System.out.println("==================================================");
        System.out.println("MATCHUP               VENUE            START TIME");
        System.out.println("==================================================");

        for (int i = 0; i < games.length(); i++) {
            JSONObject game = games.getJSONObject(i);
            String matchup = game.getString("home_team_abbreviation") + " vs "
                    + game.getString("away_team_abbreviation");
            String venue = game.getString("venue");
            String startTime = TimeConverter.convertToRegular(game.getString("start_time"));
            System.out.printf(
                    "%-16s %-21s %s%n",
                    matchup,
                    venue,
                    startTime);
        }

        System.out.print("Would you like to get travel recommendations (Y/N)?: ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            travelRecommendations(games);
        } else {
            System.out.println("Travel recommendations skipped.");
        }

        menuReturn();

    }

    public static void searchGames() throws Exception {
        System.out.print("\nEnter date (MM/dd/yyyy): ");
        String date = scanner.nextLine();

        if (date.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))) {
            getTodaysGames();

        }

        JSONObject response = new JSONObject(NBAService.getGames(date));
        JSONArray games = response.getJSONArray("games");

        if (games.length() == 0) {
            System.out.println("No games found on " + date + ".");
            menuReturn();
        }

        System.out.println("\nGames on " + date + ":");

        System.out.println("==================================================");
        System.out.println("MATCHUP               VENUE               RESULT");
        System.out.println("==================================================");

        for (int i = 0; i < games.length(); i++) {
            JSONObject game = games.getJSONObject(i);
            String matchup = game.getString("home_team_abbreviation") + " vs "
                    + game.getString("away_team_abbreviation");
            String venue = game.getString("venue");
            String result = game.getInt("home_score") + "-" + game.getInt("away_score");
            System.out.printf(
                    "%-16s %-24s %s%n",
                    matchup,
                    venue,
                    result);
        }

        LocalDate inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        if (inputDate.isAfter(LocalDate.now())) {
            System.out.print("Would you like to get travel recommendations (Y/N)?: ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("Y")) {
                travelRecommendations(games);
            } else {
                System.out.println("Travel recommendations skipped.");
            }
        }

        menuReturn();

    }

    private static void travelRecommendations(JSONArray games) {
        System.out.println(
                "Enter the name of the team whose game you are attending (e.g. OKC, Los Angeles Lakers, Atlanta): ");
        String team = scanner.nextLine();
        String city = "";
        String startTime = "";
        while (startTime.equals("")) {
            for (int i = 0; i < games.length(); i++) {
                JSONObject game = games.getJSONObject(i);
                if (game.getString("home_team_abbreviation").equalsIgnoreCase(team)
                        || game.getString("away_team_abbreviation").equalsIgnoreCase(team)) {
                    startTime = game.getString("start_time");
                    city = game.getString("location").split(", ")[0];
                } else if (game.getString("home_team_full_name").equalsIgnoreCase(team)
                        || game.getString("away_team_full_name").equalsIgnoreCase(team)) {
                    startTime = game.getString("start_time");
                    city = game.getString("location").split(", ")[0];
                } else if (game.getString("home_team_name").equalsIgnoreCase(team)
                        || game.getString("away_team_name").equalsIgnoreCase(team)) {
                    startTime = game.getString("start_time");
                    city = game.getString("location").split(", ")[0];
                }
            }

            if (startTime.equals("")) {
                System.out.println("Team not found in today's games. Please enter a valid team name: ");
                team = scanner.nextLine();
            }
        }

        WeatherClient weather = new WeatherClient();
        weather.setCity(city);
        try {
            weather.getWeather(startTime);
        } catch (Exception e) {
            System.out.println("Error fetching weather data.");
        }
    }

    private static void menuReturn() {
        System.out.println();
        System.out.print("Would you like to return to the menu (Y/N)?: ");
        String returnChoice = scanner.nextLine();
        if (returnChoice.equalsIgnoreCase("Y")) {
            return;
        } else {
            System.out.println("Goodbye.");
            System.exit(0);
        }
    }

    public static void getStandings() throws Exception {

        String east = NBAService.getStandings("east");
        JSONObject eastObj = new JSONObject(east);
        JSONArray eastStandings = eastObj.getJSONArray("standings");
        ArrayList<JSONObject> eastConference = new ArrayList<JSONObject>();

        for (int i = 0; i < eastStandings.length(); i++) {
            eastConference.add(eastStandings.getJSONObject(i));
        }

        String west = NBAService.getStandings("west");
        JSONObject westObj = new JSONObject(west);
        JSONArray westStandings = westObj.getJSONArray("standings");
        ArrayList<JSONObject> westConference = new ArrayList<JSONObject>();

        for (int i = 0; i < westStandings.length(); i++) {
            westConference.add(westStandings.getJSONObject(i));
        }

        // Sort East
        eastConference.sort((a, b) -> {

            int winsA = a.getInt("wins");
            int winsB = b.getInt("wins");

            if (winsA != winsB) {
                return winsB - winsA;
            }

            int lossesA = a.getInt("losses");
            int lossesB = b.getInt("losses");

            return lossesA - lossesB;
        });

        // Sort West
        westConference.sort((a, b) -> {

            int winsA = a.getInt("wins");
            int winsB = b.getInt("wins");

            if (winsA != winsB) {
                return winsB - winsA;
            }

            int lossesA = a.getInt("losses");
            int lossesB = b.getInt("losses");

            return lossesA - lossesB;
        });

        System.out.println("\n================ EASTERN CONFERENCE ================\n");

        System.out.printf("%-4s %-22s %-6s %-8s %-8s%n",
                "RK", "TEAM", "W-L", "L10", "STRK");

        for (int i = 0; i < eastConference.size(); i++) {

            JSONObject team = eastConference.get(i);

            String name = team.getString("team_name") + " "
                    + team.getString("nickname");

            String record = team.getInt("wins") + "-"
                    + team.getInt("losses");

            System.out.printf("%-4d %-22s %-6s %-8s %-8s%n",
                    i + 1,
                    name,
                    record,
                    team.getString("last_10"),
                    team.getString("streak"));
        }

        System.out.println("\n================ WESTERN CONFERENCE ================\n");

        System.out.printf("%-4s %-22s %-6s %-8s %-8s%n",
                "RK", "TEAM", "W-L", "L10", "STRK");

        for (int i = 0; i < westConference.size(); i++) {

            JSONObject team = westConference.get(i);

            String name = team.getString("team_name") + " "
                    + team.getString("nickname");

            String record = team.getInt("wins") + "-"
                    + team.getInt("losses");

            System.out.printf("%-4d %-22s %-6s %-8s %-8s%n",
                    i + 1,
                    name,
                    record,
                    team.getString("last_10"),
                    team.getString("streak"));
        }

        menuReturn();
    }

    public static void getTeamStats() throws Exception {
        System.out.print("\nEnter team name or abbreviation: ");
        String team = scanner.nextLine().toUpperCase();

        if (teamAbbreviations.containsKey(team)) {
            team = teamAbbreviations.get(team);
        }

        Integer teamID = getTeamID(team);
        if (teamID == null) {
            System.out.println("Team not found.");
            return;
        }

        String response = NBAService.getTeam(teamID);
        JSONObject stats = new JSONObject(response);

        double points = stats.getDouble("points_per_game");
        int pointsRank = stats.getInt("points_per_game_rank");

        double assists = stats.getDouble("assists_per_game");
        int assistsRank = stats.getInt("assists_per_game_rank");

        double fieldGoalPct = stats.getDouble("field_goal_pct");
        int fieldGoalPctRank = stats.getInt("field_goal_pct_rank");

        double threePointerPct = stats.getDouble("three_pointer_pct");
        int threePointerPctRank = stats.getInt("three_pointer_pct_rank");

        double freeThrowPct = stats.getDouble("free_throw_pct");
        int freeThrowPctRank = stats.getInt("free_throw_pct_rank");

        double turnoversPerGame = stats.getDouble("turnovers_per_game");
        int turnoversPerGameRank = stats.getInt("turnovers_per_game_rank");

        double pointsAllowedPerGame = stats.getDouble("points_allowed_per_game");
        int pointsAllowedPerGameRank = stats.getInt("points_allowed_per_game_rank");

        double stealsPerGame = stats.getDouble("steals_per_game");
        int stealsPerGameRank = stats.getInt("steals_per_game_rank");

        double blocksPerGame = stats.getDouble("blocks_per_game");
        int blocksPerGameRank = stats.getInt("blocks_per_game_rank");

        double reboundsPerGame = stats.getDouble("rebounds_per_game");
        int reboundsPerGameRank = stats.getInt("rebounds_per_game_rank");

        System.out.println("========================================");
        System.out.printf("         %s%n", team);
        System.out.println("========================================");
        System.out.println();

        System.out.println("OFFENSE");
        System.out.println("----------------------------------------");
        System.out.printf("Points/Game      %.1f   (#%d)%n",
                points,
                pointsRank);

        System.out.printf("Assists/Game     %.1f   (#%d)%n",
                assists,
                assistsRank);

        System.out.printf("FG%%              %.1f   (#%d)%n",
                fieldGoalPct,
                fieldGoalPctRank);
        System.out.printf("3PT%%             %.1f   (#%d)%n",
                threePointerPct,
                threePointerPctRank);

        System.out.printf("FT%%              %.1f   (#%d)%n",
                freeThrowPct,
                freeThrowPctRank);
        System.out.printf("Turnovers/Game   %.1f   (#%d)%n",
                turnoversPerGame,
                turnoversPerGameRank);

        System.out.println();

        System.out.println("DEFENSE");
        System.out.println("----------------------------------------");
        System.out.printf("Points Allowed   %.1f   (#%d)%n",
                pointsAllowedPerGame,
                pointsAllowedPerGameRank);

        System.out.printf("Steals/Game      %.1f   (#%d)%n",
                stealsPerGame,
                stealsPerGameRank);
        System.out.printf("Blocks/Game      %.1f   (#%d)%n",
                blocksPerGame,
                blocksPerGameRank);

        System.out.println();

        System.out.println("REBOUNDING");
        System.out.println("----------------------------------------");
        System.out.printf("Rebounds/Game    %.1f   (#%d)%n",
                reboundsPerGame,
                reboundsPerGameRank);

        System.out.println();
        System.out.println("========================================");

        System.out.println();
        // WEAKNESSES
        System.out.println("\nWEAKNESSES");
        System.out.println("----------------------------------------");

        if (freeThrowPctRank > 25) {
            System.out.printf("- Free throw efficiency (%.1f%%, #%d) is below elite conversion level%n",
                    freeThrowPct, freeThrowPctRank);
        } else if (freeThrowPctRank > 20) {
            System.out.printf("- Free throw efficiency (%.1f%%, #%d) is subpar compared to top teams%n",
                    freeThrowPct, freeThrowPctRank);
        } else if (freeThrowPctRank > 15) {
            System.out.printf("- Free throw efficiency (%.1f%%, #%d) is below average compared to top teams%n",
                    freeThrowPct, freeThrowPctRank);
        }

        if (pointsAllowedPerGame > 120) {
            System.out.printf(
                    "- Defensive scoring allowed (%.1f PPG, #%d) indicates poor resistance to opponent scoring%n",
                    pointsAllowedPerGame, pointsAllowedPerGameRank);
        } else if (pointsAllowedPerGame > 115) {
            System.out.printf(
                    "- Defensive scoring allowed (%.1f PPG, #%d) indicates substandard resistance to opponent scoring%n",
                    pointsAllowedPerGame, pointsAllowedPerGameRank);
        } else if (pointsAllowedPerGame > 112) {
            System.out.printf(
                    "- Defensive scoring allowed (%.1f PPG, #%d) indicates average resistance to opponent scoring%n",
                    pointsAllowedPerGame, pointsAllowedPerGameRank);
        }

        if (stealsPerGameRank > 25) {
            System.out.printf(
                    "- Steal generation (%.1f SPG, #%d) is poor compared to top defensive teams%n",
                    stealsPerGame, stealsPerGameRank);
        } else if (stealsPerGameRank > 20) {
            System.out.printf(
                    "- Steal generation (%.1f SPG, #%d) is below average compared to top defensive teams%n",
                    stealsPerGame, stealsPerGameRank);
        } else if (stealsPerGameRank > 15) {
            System.out.printf(
                    "- Steal generation (%.1f SPG, #%d) is subpar compared to top defensive teams%n",
                    stealsPerGame, stealsPerGameRank);
        }

        if (reboundsPerGameRank > 25) {
            System.out.printf(
                    "- Rebounding output (%.1f RPG, #%d) is below average compared to top teams%n",
                    reboundsPerGame, reboundsPerGameRank);
        } else if (reboundsPerGameRank > 20) {
            System.out.printf(
                    "- Rebounding output (%.1f RPG, #%d) is modest compared to top teams%n",
                    reboundsPerGame, reboundsPerGameRank);
        } else if (reboundsPerGameRank > 15) {
            System.out.printf(
                    "- Rebounding output (%.1f RPG, #%d) is subpar compared to top teams%n",
                    reboundsPerGame, reboundsPerGameRank);
        }

        if (threePointerPctRank > 25) {
            System.out.printf(
                    "- Perimeter shooting (%.1f%% 3PT, #%d) is poor compared to top offensive teams%n",
                    threePointerPct, threePointerPctRank);
        } else if (threePointerPctRank > 20) {
            System.out.printf(
                    "- Perimeter shooting (%.1f%% 3PT, #%d) is subpar compared to top offensive teams%n",
                    threePointerPct, threePointerPctRank);
        } else if (threePointerPctRank > 15) {
            System.out.printf(
                    "- Perimeter shooting (%.1f%% 3PT, #%d) is below average compared to top offensive teams%n",
                    threePointerPct, threePointerPctRank);
        }

        if (blocksPerGameRank > 25) {
            System.out.printf(
                    "- Rim protection output (%.1f BPG, #%d) is poor compared to top defensive frontcourts%n",
                    blocksPerGame, blocksPerGameRank);
        } else if (blocksPerGameRank > 20) {
            System.out.printf(
                    "- Rim protection output (%.1f BPG, #%d) is subpar compared to top defensive frontcourts%n",
                    blocksPerGame, blocksPerGameRank);
        } else if (blocksPerGameRank > 15) {
            System.out.printf(
                    "- Rim protection output (%.1f BPG, #%d) is below average compared to top defensive frontcourts%n",
                    blocksPerGame, blocksPerGameRank);
        }

        if (turnoversPerGameRank > 25) {
            System.out.printf(
                    "- Turnover rate (%.1f TOV/G, #%d) is extremely high compared to top teams, indicating severe possession inefficiency%n",
                    turnoversPerGame, turnoversPerGameRank);
        } else if (turnoversPerGameRank > 20) {
            System.out.printf(
                    "- Turnover rate (%.1f TOV/G, #%d) is high compared to top teams, indicating significant possession inefficiency%n",
                    turnoversPerGame, turnoversPerGameRank);
        } else if (turnoversPerGameRank > 15) {
            System.out.printf("- Turnover rate (%.1f TOV/G, #%d) suggests moderate possession inefficiency%n",
                    turnoversPerGame, turnoversPerGameRank);
        }

        if (assistsRank > 25) {
            System.out.printf("- Playmaking rank (#%d) indicates substandard offensive distribution%n",
                    assistsRank);
        } else if (assistsRank > 20) {
            System.out.printf("- Playmaking rank (#%d) indicates below-average offensive distribution%n",
                    assistsRank);
        } else if (assistsRank > 16) {
            System.out.printf("- Playmaking rank (#%d) indicates mid-tier offensive distribution%n",
                    assistsRank);
        }

        if (fieldGoalPctRank > 25) {
            System.out.printf("- Shooting efficiency rank (%d) indicates substandard offensive execution%n",
                    fieldGoalPctRank);
        } else if (fieldGoalPctRank > 20) {
            System.out.printf("- Shooting efficiency rank (%d) indicates below-average offensive execution%n",
                    fieldGoalPctRank);
        } else if (fieldGoalPctRank > 16) {
            System.out.printf("- Shooting efficiency rank (%d) indicates mid-tier offensive execution%n",
                    fieldGoalPctRank);
        }

        if (pointsRank > 25) {
            System.out.printf("- Scoring output rank (%d) shows substandard offensive production%n",
                    pointsRank);
        } else if (pointsRank > 20) {
            System.out.printf("- Scoring output rank (%d) shows below-average offensive production%n",
                    pointsRank);
        } else if (pointsRank > 16) {
            System.out.printf("- Scoring output rank (%d) shows mid-tier offensive production%n",
                    pointsRank);
        }

        // STRENGTH NOTES
        System.out.println("\nSTRENGTH NOTES");
        System.out.println("----------------------------------------");

        if (stealsPerGameRank <= 5) {
            System.out.printf("+ Elite defensive disruption (%.1f steals/game, #%d)%n",
                    stealsPerGame, stealsPerGameRank);
        } else if (stealsPerGameRank <= 8) {
            System.out.printf("+ Strong defensive disruption (%.1f steals/game, #%d)%n",
                    stealsPerGame, stealsPerGameRank);
        } else if (stealsPerGameRank <= 12) {
            System.out.printf("+ Solid defensive disruption (%.1f steals/game, #%d)%n",
                    stealsPerGame, stealsPerGameRank);
        }

        if (threePointerPctRank <= 5) {
            System.out.printf("+ High-level perimeter shooting (%.1f%% 3PT, #%d)%n",
                    threePointerPct, threePointerPctRank);
        } else if (threePointerPctRank <= 8) {
            System.out.printf("+ Strong perimeter shooting (%.1f%% 3PT, #%d)%n",
                    threePointerPct, threePointerPctRank);
        } else if (threePointerPctRank <= 12) {
            System.out.printf("+ Solid perimeter shooting (%.1f%% 3PT, #%d)%n",
                    threePointerPct, threePointerPctRank);
        }

        if (reboundsPerGameRank <= 5) {
            System.out.printf("+ Strong rebounding presence (%.1f RPG, #%d)%n",
                    reboundsPerGame, reboundsPerGameRank);
        } else if (reboundsPerGameRank <= 8) {
            System.out.printf("+ Above-average rebounding presence (%.1f RPG, #%d)%n",
                    reboundsPerGame, reboundsPerGameRank);
        } else if (reboundsPerGameRank <= 12) {
            System.out.printf("+ Solid rebounding presence (%.1f RPG, #%d)%n",
                    reboundsPerGame, reboundsPerGameRank);
        }

        if (fieldGoalPctRank <= 5) {
            System.out.printf("+ Elite shooting efficiency (%.1f%% FG, #%d)%n",
                    fieldGoalPct, fieldGoalPctRank);
        } else if (fieldGoalPctRank <= 8) {
            System.out.printf("+ Strong shooting efficiency (%.1f%% FG, #%d)%n",
                    fieldGoalPct, fieldGoalPctRank);
        } else if (fieldGoalPctRank <= 12) {
            System.out.printf("+ Solid shooting efficiency (%.1f%% FG, #%d)%n",
                    fieldGoalPct, fieldGoalPctRank);
        }

        if (pointsAllowedPerGameRank <= 5) {
            System.out.printf("+ Strong defensive performance (%.1f PPG allowed, #%d)%n",
                    pointsAllowedPerGame, pointsAllowedPerGameRank);
        } else if (pointsAllowedPerGameRank <= 8) {
            System.out.printf("+ Above-average defensive performance (%.1f PPG allowed, #%d)%n",
                    pointsAllowedPerGame, pointsAllowedPerGameRank);
        } else if (pointsAllowedPerGameRank <= 12) {
            System.out.printf("+ Solid defensive performance (%.1f PPG allowed, #%d)%n",
                    pointsAllowedPerGame, pointsAllowedPerGameRank);
        }

        if (assistsRank <= 5) {
            System.out.printf("+ Elite offensive ball movement (%.1f APG, #%d)%n",
                    assists, assistsRank);
        } else if (assistsRank <= 8) {
            System.out.printf("+ Above-average offensive ball movement (%.1f APG, #%d)%n",
                    assists, assistsRank);
        } else if (assistsRank <= 12) {
            System.out.printf("+ Solid offensive ball movement (%.1f APG, #%d)%n",
                    assists, assistsRank);
        }

        if (pointsRank <= 5) {
            System.out.printf("+ Elite scoring output (%.1f PPG, #%d)%n",
                    points, pointsRank);
        } else if (pointsRank <= 8) {
            System.out.printf("+ Above-average scoring output (%.1f PPG, #%d)%n",
                    points, pointsRank);
        } else if (pointsRank <= 13) {
            System.out.printf("+ Reliable scoring output (%.1f PPG, #%d)%n",
                    points, pointsRank);
        }

        menuReturn();

    }

    public static void getPlayerStats() throws Exception {
        System.out.print("\nEnter player name (e.g. LeBron James): ");
        String player = scanner.nextLine();

        Integer playerID = getPlayerID(player);
        if (playerID == null) {
            System.out.println("Player not found.");
            return;
        }

        String response = NBAService.getPlayerStats(playerID);
        JSONObject stats = new JSONObject(response);

        JSONObject seasonStats = stats.getJSONArray("regularSeason")
                .getJSONObject(stats.getJSONArray("regularSeason").length() - 1);

        double fieldGoalPct = seasonStats.getDouble("fieldGoalPct");
        double threePointPct = seasonStats.getDouble("threePointPct");
        double freeThrowPct = seasonStats.getDouble("freeThrowPct");
        double points = seasonStats.getDouble("points");
        double assists = seasonStats.getDouble("assists");
        double turnovers = seasonStats.getDouble("turnovers");
        double steals = seasonStats.getDouble("steals");
        double blocks = seasonStats.getDouble("blocks");
        double personalFouls = seasonStats.getDouble("personalFouls");
        double rebounds = seasonStats.getDouble("rebounds");
        double offRebounds = seasonStats.getDouble("offRebounds");
        double defRebounds = seasonStats.getDouble("defRebounds");
        double fieldGoalsAttempted = seasonStats.getDouble("fieldGoalsAttempted");

        System.out.println("========================================");
        System.out.println("        PLAYER STATS REPORT");
        System.out.println("========================================");

        // OFFENSE
        System.out.println("\nOFFENSE");
        System.out.println("----------------------------------------");
        System.out.printf("Points/Game        %.1f%n", points);
        System.out.printf("FG%%               %.1f%n", fieldGoalPct * 100);
        System.out.printf("3PT%%              %.1f%n", threePointPct * 100);
        System.out.printf("FT%%               %.1f%n", freeThrowPct * 100);
        System.out.printf("Assists/Game       %.1f%n", assists);
        System.out.printf("Turnovers/Game     %.1f%n", turnovers);

        // DEFENSE
        System.out.println("\nDEFENSE");
        System.out.println("----------------------------------------");
        System.out.printf("Steals/Game        %.1f%n", steals);
        System.out.printf("Blocks/Game        %.1f%n", blocks);
        System.out.printf("Personal Fouls     %.1f%n", personalFouls);

        // REBOUNDING
        System.out.println("\nREBOUNDING");
        System.out.println("----------------------------------------");
        System.out.printf("Rebounds/Game      %.1f%n", rebounds);
        System.out.printf("Offensive Rebounds %.1f%n", offRebounds);
        System.out.printf("Defensive Rebounds %.1f%n", defRebounds);

        // WEAKNESSES
        System.out.println("\nWEAKNESSES");
        System.out.println("----------------------------------------");

        boolean foundWeakness = false;
        
        if (threePointPct < 0.3) {
            System.out.printf("- Three-point efficiency (%.1f%%) is poor and limits offensive versatility%n",
                    threePointPct * 100);
            foundWeakness = true;
        }
        else if (threePointPct < 0.33) {
            System.out.printf("- Three-point efficiency (%.1f%%) is below strong perimeter threshold%n",
                    threePointPct * 100);
            foundWeakness = true;
        }
        else if (threePointPct < 0.35) {
            System.out.printf("- Three-point efficiency (%.1f%%) is below solid perimeter threshold%n",
                    threePointPct * 100);
            foundWeakness = true;
        }

        if (freeThrowPct < 0.5) {
            System.out.printf("- Free throw efficiency (%.1f%%) is extremely poor and limits offensive reliability%n",
                    freeThrowPct * 100);
            foundWeakness = true;
        } else if (freeThrowPct < 0.65) {
            System.out.printf("- Free throw efficiency (%.1f%%) is poor and limits offensive reliability%n",
                    freeThrowPct * 100);
            foundWeakness = true;
        } else if (freeThrowPct < 0.75) {
            System.out.printf("- Free throw consistency (%.1f%%) limits late-game reliability%n", freeThrowPct * 100);
            foundWeakness = true;
        }

        if (turnovers > 4.0) {
            System.out.printf("- Turnover rate (%.1f) is extremely high and indicates severe possession waste%n", turnovers);
            foundWeakness = true;
        } else if (turnovers > 3.2) {
            System.out.printf("- Turnover rate (%.1f) is high and indicates significant possession waste%n", turnovers);
            foundWeakness = true;
        } else if (turnovers > 2.5) {
            System.out.printf("- Turnover rate (%.1f) indicates occasional possession waste%n", turnovers);
            foundWeakness = true;
        }

        if (assists < 5.0 && turnovers > 2.5) {
            System.out.printf("- Assist-to-turnover balance suggests inefficient playmaking decisions%n");
            foundWeakness = true;
        }

        if (blocks < 0.5) {
            System.out.printf("- Minimal rim protection impact (%.1f blocks/game)%n", blocks);
            foundWeakness = true;
        } else if (blocks < 1.0) {
            System.out.printf("- Limited rim protection impact (%.1f blocks/game)%n", blocks);
            foundWeakness = true;
        }

        if (steals < 0.5) {
            System.out.printf("- Very low defensive disruption (%.1f steals/game)%n", steals);
            foundWeakness = true;
        } else if (steals < 1.0) {
            System.out.printf("- Low defensive disruption (%.1f steals/game)%n", steals);
            foundWeakness = true;
        }

        if (rebounds < 3.0) {
            System.out.printf("- Very low rebounding contribution (%.1f RPG)%n", rebounds);
            foundWeakness = true;
        } else if (rebounds < 5.0) {
            System.out.printf("- Below-average rebounding contribution (%.1f RPG)%n", rebounds);
            foundWeakness = true;
        }

        if (fieldGoalPct < 0.4) {
            System.out.printf("- Very poor shooting efficiency (%.1f%% FG)%n", fieldGoalPct * 100);
            foundWeakness = true;
        }
        else if (fieldGoalPct < 0.43) {
            System.out.printf("- Poor shooting efficiency (%.1f%% FG)%n", fieldGoalPct * 100);
            foundWeakness = true;
        }
        else if (fieldGoalPct < 0.45) {
            System.out.printf("- Inefficient scoring from the field (%.1f%% FG)%n", fieldGoalPct * 100);
            foundWeakness = true;
        }

        if (fieldGoalsAttempted > 15 && fieldGoalPct < 0.48) {
            System.out.printf("- High shot volume with moderate efficiency reduces scoring efficiency impact%n");
            foundWeakness = true;
        }

        if (personalFouls > 2.5) {
            System.out.printf("- Elevated foul rate (%.1f) may limit defensive availability%n", personalFouls);
            foundWeakness = true;
        }

        if (points < 7.5) {
            System.out.printf("- Low scoring output (%.1f PPG) limits offensive impact%n", points);
            foundWeakness = true;
        }

        if (points < 20 && fieldGoalPct < 0.48) {
            System.out.printf("- Inefficient scoring production (%.1f PPG on sub-elite efficiency)%n", points);
            foundWeakness = true;
        }

        if (points < 18 && assists < 5) {
            System.out.printf("- Limited dual-threat offensive impact (%.1f PPG with low playmaking)%n", points);
            foundWeakness = true;
        }

        if (!foundWeakness) {
            System.out.println("No major weaknesses identified.");
        }

        // STRENGTH NOTES
        System.out.println("\nSTRENGTH NOTES");
        System.out.println("----------------------------------------");

        boolean foundStrength = false;

        if (fieldGoalPct >= 0.52) {
            System.out.printf("+ Elite shooting efficiency (%.1f%% FG)%n", fieldGoalPct * 100);
            foundStrength = true;
        } else if (fieldGoalPct >= 0.48) {
            System.out.printf("+ Strong shooting efficiency (%.1f%% FG)%n", fieldGoalPct * 100);
            foundStrength = true;
        }

        if (threePointPct >= 0.43) {
            System.out.printf("+ Elite perimeter shooting (%.1f%% 3PT), dangerous shooter%n", threePointPct * 100);
            foundStrength = true;
        } else if (threePointPct >= 0.40) {
            System.out.printf("+ High-level perimeter shooting (%.1f%% 3PT)%n", threePointPct * 100);
            foundStrength = true;
        } else if (threePointPct >= 0.37) {
            System.out.printf("+ Strong perimeter shooting (%.1f%% 3PT)%n", threePointPct * 100);
            foundStrength = true;
        }

        if (freeThrowPct >= 0.90) {
            System.out.printf("+ Elite free throw shooting (%.1f%% FT), reliable in late game situations%n", freeThrowPct * 100);
            foundStrength = true;
        } else if (freeThrowPct >= 0.85) {
            System.out.printf("+ Strong free throw shooting (%.1f%% FT)%n", freeThrowPct * 100);
            foundStrength = true;
        } else if (freeThrowPct >= 0.80) {
            System.out.printf("+ Reliable free throw shooting (%.1f%% FT)%n", freeThrowPct * 100);
            foundStrength = true;
        }

        if (assists >= 9.0) {
            System.out.printf("+ Elite playmaking (%.1f assists/game)%n", assists);
            foundStrength = true;
        } else if (assists >= 7.0) {
            System.out.printf("+ High-level playmaking (%.1f assists/game)%n", assists);
            foundStrength = true;
        } else if (assists >= 5.5) {
            System.out.printf("+ Strong facilitation ability (%.1f assists/game)%n", assists);
            foundStrength = true;
        }

        if (rebounds >= 10.0) {
            System.out.printf("+ Dominant rebounding presence (%.1f RPG)%n", rebounds);
            foundStrength = true;
        } else if (rebounds >= 8.0) {
            System.out.printf("+ Elite rebounding presence (%.1f RPG)%n", rebounds);
            foundStrength = true;
        } else if (rebounds >= 6.0) {
            System.out.printf("+ Solid rebounding for position (%.1f RPG)%n", rebounds);
            foundStrength = true;
        }

        if (steals >= 1.5) {
            System.out.printf("+ High defensive disruption (%.1f steals/game)%n", steals);
            foundStrength = true;
        } else if (steals >= 1.0) {
            System.out.printf("+ Active defensive hands (%.1f steals/game)%n", steals);
            foundStrength = true;
        }

        if (blocks >= 1.5) {
            System.out.printf("+ Strong rim protection presence (%.1f blocks/game)%n", blocks);
            foundStrength = true;
        }

        if (points >= 30) {
            System.out.printf("+ Unstoppable scorer (%.1f PPG)%n", points);
            foundStrength = true;
        }
        else if (points >= 25) {
            System.out.printf("+ Elite scoring production (%.1f PPG)%n", points);
            foundStrength = true;
        } else if (points >= 20) {
            System.out.printf("+ Strong scoring output (%.1f PPG)%n", points);
            foundStrength = true;
        } else if (points >= 15) {
            System.out.printf("+ Solid scoring contribution (%.1f PPG)%n", points);
            foundStrength = true;
        }

        if (assists >= 6.0 && turnovers <= 2.0) {
            System.out.printf("+ Efficient playmaking with controlled turnovers%n");
            foundStrength = true;
        }

        if (fieldGoalPct >= 0.50 && threePointPct >= 0.37) {
            System.out.printf("+ Complete scoring efficiency profile (inside + perimeter)%n");
            foundStrength = true;
        }

        if (!foundStrength) {
            System.out.println("No major strengths identified.");
        }

        menuReturn();
    }

}