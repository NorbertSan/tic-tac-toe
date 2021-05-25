package com.example.tictactoe.service;

import com.example.tictactoe.exception.GameException;
import com.example.tictactoe.model.*;
import com.example.tictactoe.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    public Game createGame(Player player){
        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(GameStatus.NEW);

        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToGame(Player player2,String gameId) throws GameException {
        Game game = getGame(gameId);
        if(game.getPlayer2() != null){
            String noFreeSlotsMessage = "Game with id " + gameId + "has to free slots to play";
            throw new GameException(noFreeSlotsMessage);
        }
        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToRandomGame(Player player2) throws GameException{
      Game game = GameStorage.getInstance().getGames()
              .values()
              .stream()
              .filter(item -> item.getStatus().equals(GameStatus.NEW))
              .findFirst().orElseThrow(() -> new GameException("No waiting games found"));

      game.setPlayer2(player2);
      game.setStatus(GameStatus.IN_PROGRESS);
      GameStorage.getInstance().setGame(game);
      return game;
    }

    public Game playGame(Move move) throws GameException{
        Game game = getGame(move.getGameId());

        if(game.getStatus().equals(GameStatus.FINISHED)){
            throw new GameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        board[move.getPositionX()][move.getPositionY()] = move.getSign().getValue();
        game.setBoard(board);

        checkWinner(game.getBoard(),move.getSign());
        return game;
    }

    private Boolean checkWinner(int[][] board, Sign playerSign){
        int [] oneDimensionArr = new int[9];
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[i].length;j++){
                oneDimensionArr[i*j] = board[i][j];
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

    private Game getGame(String gameId) throws GameException{
        if(!GameStorage.getInstance ().getGames().containsKey(gameId)){
            String gameNotFoundMessage = "Game with id " + gameId + "not found";
            throw new GameException(gameNotFoundMessage);
        };
        Game game = GameStorage.getInstance().getGames().get(gameId);

        return game;
    }
}
