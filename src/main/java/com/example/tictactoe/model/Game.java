package com.example.tictactoe.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class Game {
    private ObjectId _id;
    private String gameId ;
    private String player1;
    private String player2;
    private GameStatus status;
    private int[][] board;
    private String winner;

    public Game(ObjectId id, String gameId, String player1, String player2, GameStatus status, int [][] board, String winner) {
        this._id = id;
        this.gameId = gameId;
        this.player1 = player1;
        this.player2 = player2;
        this.status = status;
        this.board = board;
        this.winner = winner;
    }
}
