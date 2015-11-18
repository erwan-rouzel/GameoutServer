package com.gameout.network;

import com.gameout.model.GameSession;
import com.google.gson.Gson;

/**
 * Created by erwan on 11/11/2015.
 */
public class TestClient {
    /*
        Début de partie (webservice REST JSon, TCP) :
    4 octets : ID Partie
    4 octets : timestamp complet (heure en secondes)
    1 octet : nombre d'équipes
    1 octet : nombre de personnes équipe 1
    1 octet : nombre de personnes équipe 2
    1 octet : nombre de personnes équipe 3
     */
    public static void main(String args[]) throws Exception {
        System.out.println("Starting client...");

        GameoutClient client = new GameoutClient();

        GameSession session = new GameSession();
        session.id = 1;
        session.timestamp = 12345;
        session.numberOfPlayersInTeam1 = 1;
        session.numberOfPlayersInTeam2 = 1;

        Gson gson = new Gson();

        client.sendMessageTCP(gson.toJson(session));
        Thread.sleep(3000);
        client.sendMessageTCP(gson.toJson(session));
        Thread.sleep(3000);
        client.sendMessageUDP("Hello UDP!");
    }
}
