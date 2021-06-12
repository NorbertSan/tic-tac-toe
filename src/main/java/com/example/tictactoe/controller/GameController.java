package com.example.tictactoe.controller;

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
    public ResponseEntity<Game> create(@RequestBody CreateGameBody createGameBody){
        Game game = gameService.createGame(createGameBody.getPlayer1());
        return ResponseEntity.ok(game);
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Game>> getGames() {
        ArrayList<Game> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable String id)  {
        Game game = gameService.getGame(id);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connectToRandomGame(@RequestBody ConnectRandomGameBody connectRandomGameBody)  {
        Game game = gameService.connectToRandomGame(connectRandomGameBody.getPlayer2());
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/connect")
    public ResponseEntity<Game> connectToGame(@RequestBody ConnectGameBody connectGameBody, @PathVariable String id)  {
        Game game = gameService.connectToGame(connectGameBody.getPlayer2(),id);
        return ResponseEntity.ok(game);
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<Game> makeMove(@RequestBody Move move, @PathVariable String id)  {
        Game game = gameService.playGame(move,id);
        simpMessagingTemplate.convertAndSend("/topic/game-progress" + game.getGameId(),game);
        return ResponseEntity. ok(game);
    }

}
