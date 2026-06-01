package com.adi;

public class NBAService {

    public static String getPlayers() throws Exception {
        return StatsClient.fetch("/players?isActive=1&season=2025-26");
    }

    public static String getPlayerStats(int playerId) throws Exception {
        return StatsClient.fetch("/players/" + playerId + "/stats?perMode=PerGame");
    }

    public static String getTeam(int teamId) throws Exception {
        return NBAClient.fetch("/teams/" + teamId + "/season/information");
    }

    public static String getTeamPlayers(int teamId) throws Exception {
        return NBAClient.fetch("/teams/" + teamId + "/rosters");
    }

    public static String getStandings(String conference) throws Exception {
        return NBAClient.fetch("/standings/" + conference);
    }

    public static String getGames(String date) throws Exception {
        return NBAClient.fetch("/games?date=" + date);
    }


}