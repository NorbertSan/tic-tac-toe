package com.example.tictactoe.service;

import com.example.tictactoe.model.*;
import com.example.tictactoe.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    public Game createGame(String player1){
        Game game = new Game(new ObjectId(),UUID.randomUUID().toString(),player1,null,GameStatus.NEW,new int[3][3],null);
        GameStorage.getInstance().createGame(game);
        return game;
    }

    public Game connectToGame(String player, String gameId)  {
        Game game = this.getGame(gameId);

        if(game.getPlayer1().equals(player)){
            return game;
        }

        if(game.getPlayer2() != null){
            if(game.getPlayer2().equals(player)){
                return game;
            }
        }

        if(game.getPlayer1() != null && game.getPlayer2() != null){
            String noFreeSlotsMessage = "Game with id " + gameId + "has no free slots to play";
            System.out.println(noFreeSlotsMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NO_FREE_SLOTS");
        }

        game.setPlayer2(player);
        if(game.getPlayer1() != null && game.getPlayer2() != null){
            game.setStatus(GameStatus.IN_PROGRESS);
        }
        GameStorage.getInstance().patchGame(game);

        return game;
    }

    public Game connectToRandomGame(String player2) {
        ArrayList<Game> games = GameStorage.getInstance().getGames(GameStatus.NEW);
        Game game = games.stream()
                .findFirst()
                .orElseThrow(() -> {
                    String noOpenedGamesMessage = "No waiting games found";
                    System.out.println(noOpenedGamesMessage);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, noOpenedGamesMessage);
                });

      game.setPlayer2(player2);
      game.setStatus(GameStatus.IN_PROGRESS);

      GameStorage.getInstance().patchGame(game);
      return game;
    }

    public Game playGame(Move move, String gameId) {
        Game game = this.getGame(gameId);

        if(game.getStatus().equals(GameStatus.FINISHED)){
            String gameFinishedMessage = "Game is already finished";
            System.out.println(gameFinishedMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GAME_FINISHED");
        }

        int[][] board = game.getBoard();
        board[move.getPositionY()][move.getPositionX()] = move.getSign().getValue();
        game.setBoard(board);

        if(checkWinner(game.getBoard(),move.getSign())){
            game.setStatus(GameStatus.FINISHED);
            game.setWinner(move.getPlayer());
        }

        if(checkIfGameEnded(game.getBoard())){
            game.setStatus(GameStatus.FINISHED);
        }

        GameStorage.getInstance().patchGame(game);

        return game;
    }

    private Boolean checkIfGameEnded(int[][] board){
        Boolean gameEnded= true;
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[i].length;j++){
                if(board[i][j] == 0){
                    gameEnded = false;
                }
            }
        }

        return gameEnded;
    }

    private Boolean checkWinner(int[][] board, Sign playerSign){
        int [] oneDimensionArr = new int[9];
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[i].length;j++){
                oneDimensionArr[i*board.length + j] = board[i][j];
            }
        }

        int [][] winCombitations = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{1,5,8},{2,5,8}};

        boolean hasPlayerWon = false;
        for(int k=0; k< winCombitations.length; k++){
            int [] singleWinCombination = winCombitations[k];
            int a = singleWinCombination[0];
            int b = singleWinCombination[1];
            int c = singleWinCombination[2];

            if(hasPlayerWon){
                break;
            }

            if(
                    oneDimensionArr[a] == playerSign.getValue() &&
                            oneDimensionArr[b] == playerSign.getValue() &&
                                oneDimensionArr[c] == playerSign.getValue()
            ){
                hasPlayerWon=true;
            }
        }

        return hasPlayerWon;
    }

    public Game getGame(String gameId) {
        Game game = GameStorage.getInstance().getGame(gameId);
        return game;
    }

    public ArrayList<Game> getGames(GameStatus status){
        return GameStorage.getInstance().getGames(status);
    }
}
