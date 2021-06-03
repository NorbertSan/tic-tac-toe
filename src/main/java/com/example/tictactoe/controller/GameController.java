package com.example.tictactoe.controller;

import com.example.tictactoe.exception.GameException;
import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.Move;
import com.example.tictactoe.model.Player;
import com.example.tictactoe.model.Sign;
import com.example.tictactoe.service.GameService;
import com.example.tictactoe.storage.GameStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @PostMapping("")
    public ResponseEntity<Game> create(@RequestBody Player player){
        Game game = gameService.createGame(player);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame( @PathVariable String id) throws GameException {
        Game game = gameService.getGame(id);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/connect")
    public ResponseEntity<Game> connectToGame(@RequestBody Player player2, @PathVariable String id) throws GameException {
        Game game = gameService.connectToGame(player2,id);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connectToRandomGame(@RequestBody Player player2) throws GameException {
        Game game = gameService.connectToRandomGame(player2);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<Game> makeMove(@RequestBody Move move,@PathVariable String id) throws GameException {
        Game game = gameService.playGame(move,id);
        simpMessagingTemplate.convertAndSend("/topic/game-progress" + game.getGameId(),game);
        return ResponseEntity. ok(game);
    }
}
