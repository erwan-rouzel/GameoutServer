package com.gameout.network;

import com.gameout.exception.MalformedPlayerMessageException;
import com.gameout.model.GameState;
import com.gameout.model.Player;
import com.gameout.model.Team;
import com.oracle.tools.packager.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by erwanrouzel on 06/12/2015.
 */
public class GameoutServerHelper {
    public static byte[] getGameStateMessage(GameState gameState) {
        /*
        8 octets : timestamp
        1 octet : incrément
        3 octets : scores (entre 0 et 9)
        2 octets : X balle
        2 octets : Y balle
        2 octets : VX balle
        2 octets : VY balle
        1 octet : nombre de personnes équipe 1
        1 octet : nombre de personnes équipe 2
        1 octet : nombre de personnes équipe 3

        2 octets : X (E1, J1)
        2 octets : Y (E1, J1)
        2 octets : VX (E1, J1)
        2 octets : VY (E1, J1)
        1 octet : état J1
        ...
        */

        ArrayList<Byte> message = new ArrayList<Byte>();

        long timestamp = new Date().getTime();
        byte increment = 0;
        byte score1 = gameState.teams[0].score;
        byte score2 = gameState.teams[1].score;
        byte score3 = gameState.teams[2].score;
        short ballX = gameState.ball.x;
        short ballY = gameState.ball.y;
        short ballVX = gameState.ball.vx;
        short ballVY = gameState.ball.vy;
        byte numPlayersTeam1 = (byte) gameState.teams[0].players.length;
        byte numPlayersTeam2 = (byte) gameState.teams[1].players.length;
        byte numPlayersTeam3 = (byte) gameState.teams[2].players.length;

        byte[] buffer;

        buffer = GameoutUtils.longToBytes(timestamp);
        message.add(buffer[0]);
        message.add(buffer[1]);
        message.add(buffer[2]);
        message.add(buffer[3]);
        message.add(buffer[4]);
        message.add(buffer[5]);
        message.add(buffer[6]);
        message.add(buffer[7]);

        message.add(increment);
        message.add(score1);
        message.add(score2);
        message.add(score3);

        buffer = GameoutUtils.shortToBytes(ballX);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballY);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballVX);
        message.add(buffer[0]);
        message.add(buffer[1]);

        buffer = GameoutUtils.shortToBytes(ballVY);
        message.add(buffer[0]);
        message.add(buffer[1]);

        message.add(numPlayersTeam1);
        message.add(numPlayersTeam2);
        message.add(numPlayersTeam3);

        for(Team t: gameState.teams) {
            for(Player p: t.players) {
                buffer = GameoutUtils.shortToBytes(p.x);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.y);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.vx);
                message.add(buffer[0]);
                message.add(buffer[1]);

                buffer = GameoutUtils.shortToBytes(p.vy);
                message.add(buffer[0]);
                message.add(buffer[1]);

                message.add((byte) p.state.ordinal());
            }
        }

        byte[] result = new byte[message.size()];
        for(int i = 0; i < message.size(); i++) {
            result[i] = message.get(i);
        }
        return result;
    }

    public static Player processMessageFromPlayer(byte[] m, InetAddress ip) throws MalformedPlayerMessageException {
        /*
        4 octets : ID Partie
        8 octets : timestamp en microsecondes
        1 octet : ID team
        1 octet : ID Joueur dans team
        1 octet : action
        1 octet : incrément
        2 octets : coordonnée X
        2 octets : coordonnée Y
        2 octets : coordonnée VX
        2 octets : coordonnée VY
        */

        if(m.length != 24) {
            throw new MalformedPlayerMessageException(m);
        }

        int idGame =        GameoutUtils.bytesToInt(m[0], m[1], m[2], m[3]);
        long timestamp =     GameoutUtils.bytesToLong(m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11]);
        byte idTeam =       m[12];
        byte idPlayer =     m[13];
        byte action =       m[14];
        byte increment =    m[15];
        short x =           GameoutUtils.bytesToShort(m[16], m[17]);
        short y =           GameoutUtils.bytesToShort(m[18], m[19]);
        short vx =          GameoutUtils.bytesToShort(m[20], m[21]);
        short vy =          GameoutUtils.bytesToShort(m[22], m[23]);

        // We retrieve the corresponding player and update his datas
        GameState state = GameoutServer.gameStateList.get(idGame);
        LogHelper.log(GameoutServerHelper.class.getSimpleName(), "idTeam=" + idTeam);
        LogHelper.log(GameoutServerHelper.class.getSimpleName(), state.toString());
        LogHelper.log(GameoutServerHelper.class.getSimpleName(), state.teams.toString());

        Team team = state.teams[idTeam];
        Player player = team.players[idPlayer];

        player.ip = ip;
        player.x = x;
        player.y = y;
        player.vx = vx;
        player.vy = vy;

        return player;
    }
}
