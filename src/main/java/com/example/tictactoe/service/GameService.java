package com.example.tictactoe.service;

import com.example.tictactoe.exception.GameException;
import com.example.tictactoe.model.*;
import com.example.tictactoe.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    public GameMongo createGame(String player1){
        GameMongo game = new GameMongo(new ObjectId(),UUID.randomUUID().toString(),player1,null,GameStatus.NEW,new int[3][3],null);
        GameStorage.getInstance().createGame(game);
        return game;
    }

    public GameMongo connectToGame(String player2,String gameId) throws GameException {
        GameMongo game = this.getGameMongo(gameId);
        if(game.getPlayer2() != null){
            String noFreeSlotsMessage = "Game with id " + gameId + "has no free slots to play";
            throw new GameException(noFreeSlotsMessage);
        }
        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().patchGame(game);

        return game;
    }

    public GameMongo connectToRandomGame(String player2) throws GameException{
        ArrayList<GameMongo> games = GameStorage.getInstance().getGamesMongo();
        GameMongo game = games.stream()
                .filter(item -> {
                    GameStatus status = item.getStatus();
                    if(status == null) return false;
                    return status.equals(GameStatus.NEW);
                })
                .findFirst()
                .orElseThrow(() -> new GameException("No waiting games found"));

      game.setPlayer2(player2);
      game.setStatus(GameStatus.IN_PROGRESS);

      GameStorage.getInstance().patchGame(game);
      return game;
    }

    public GameMongo playGame(Move move,String gameId) throws GameException{
        GameMongo game = this.getGameMongo(gameId);

        if(game.getStatus().equals(GameStatus.FINISHED)){
            throw new GameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        board[move.getPositionX()][move.getPositionY()] = move.getSign().getValue();
        game.setBoard(board);


        if(checkWinner(game.getBoard(),move.getSign())){
            game.setStatus(GameStatus.FINISHED);
            game.setWinner(move.getPlayer());
        }
        GameStorage.getInstance().patchGame(game);

        return game;
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

    public GameMongo getGameMongo(String gameId) throws GameException{
        GameMongo game = GameStorage.getInstance().getGame(gameId);
        return game;
    }

    public ArrayList<GameMongo> getGames(){
        return GameStorage.getInstance().getGamesMongo();
    }
}
