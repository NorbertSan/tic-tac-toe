package com.example.tictactoe.controller;

import com.example.tictactoe.exception.GameException;
import com.example.tictactoe.model.*;
import com.example.tictactoe.service.GameService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;



@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @PostMapping("")
    public ResponseEntity<GameMongo> create(@RequestBody CreateGameBody createGameBody){
        GameMongo game = gameService.createGame(createGameBody.getPlayer1());
        return ResponseEntity.ok(game);
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<GameMongo>> getGames() {
        ArrayList<GameMongo> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameMongo> getGame(@PathVariable String id) throws GameException {
        GameMongo game = gameService.getGameMongo(id);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/connect")
    public ResponseEntity<GameMongo> connectToRandomGame(@RequestBody ConnectRandomGameBody connectRandomGameBody) throws GameException {
        GameMongo game = gameService.connectToRandomGame(connectRandomGameBody.getPlayer2());
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/connect")
    public ResponseEntity<GameMongo> connectToGame(@RequestBody ConnectGameBody connectGameBody, @PathVariable String id) throws GameException {
        GameMongo game = gameService.connectToGame(connectGameBody.getPlayer2(),id);
        return ResponseEntity.ok(game);
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<GameMongo> makeMove(@RequestBody Move move,@PathVariable String id) throws GameException {
        GameMongo game = gameService.playGame(move,id);
        simpMessagingTemplate.convertAndSend("/topic/game-progress" + game.getGameId(),game);
        return ResponseEntity. ok(game);
    }

}
