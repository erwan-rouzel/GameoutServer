package com.gameout.network;

import com.gameout.model.GameState;
import java.util.concurrent.ConcurrentHashMap;

class GameoutServer {
    public static ConcurrentHashMap<Integer, GameState> gameStateList = new ConcurrentHashMap<Integer, GameState>();

    /*
    Début de partie (webservice REST JSon, TCP) :
    4 octets : ID Partie
    4 octets : timestamp complet (heure en secondes)
    1 octet : nombre de personnes équipe 1
    1 octet : nombre de personnes équipe 2
    1 octet : nombre de personnes équipe 3

    Message fin de partie pour le Host (TCP) :
    4 octets : ID Partie
    4 octets : timestamp
    3 octets : scores (entre 0 et 9)

    Message fin de partie pour les invités (TCP) :
    4 octets : ID Partie
    4 octets : timestamp
    3 octets : scores (entre 0 et 9)

    Message des joueurs vers le serveur (en binaire, UDP) :
    4 octets : ID Partie
    4 octets : timestamp en microsecondes
    1 octer : ID team
    1 octet : ID Joueur dans team
    1 octet : action
    1 octet : incrément
    2 octets : coordonnée X
    2 octets : coordonnée Y
    2 octets : coordonnée VX
    2 octets : coordonnée VY

    Message du serveur vers chaque joueur :
    4 octets : timestamp
    1 octet : incrément
    3 octets : scores (entre 0 et 9)
    2 octets : X balle
    2 octets : Y balle
    2 octets : VX balle
    2 octets : VY balle
    1 octet : nombre d'équipes
    1 octet : nombre de personnes équipe 1
    1 octet : nombre de personnes équipe 2
    1 octet : nombre de personnes équipe 3

    2 octets : X (E1, J1)
    2 octets : Y (E1, J1)
    2 octets : VX (E1, J1)
    2 octets : VY (E1, J1)
    1 octet : état J1
    ...
    2 octets : X (E1, Jn)
    2 octets : Y (E1, Jn)
    2 octets : VX (E1, Jn)
    2 octets : VY (E1, Jn)
    1 octet : état Jn

    2 octets : X (E2, J1)
    2 octets : Y (E2, J1)
    2 octets : VX (E2, J1)
    2 octets : VY (E2, J1)
    1 octet : état J1
    ...
    2 octets : X (E2, Jm)
    2 octets : Y (E2, Jm)
    2 octets : VX (E2, Jm)
    2 octets : VY (E2, Jm)
    1 octet : état Jm

    2 octets : X (E3, J1)
    2 octets : Y (E3, J1)
    2 octets : VX (E3, J1)
    2 octets : VY (E3, J1)
    1 octet : état J1
    ...
    2 octets : X (E3, Jp)
    2 octets : Y (E3, Jp)`
    2 octets : VX (E3, Jp)
    2 octets : VY (E3, Jp)
    1 octet : état Jp
    */
    public static void main(String args[]) throws Exception
    {
        new Thread(new SessionServer(9475)).start();
        new Thread(new MessageServer(9476)).start();
        new Thread(new StreamServer(9500)).start();
    }
}
