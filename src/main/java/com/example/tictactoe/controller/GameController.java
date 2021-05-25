package com.example.tictactoe.controller;

import com.example.tictactoe.exception.GameException;
import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.Player;
import com.example.tictactoe.service.GameService;
import com.example.tictactoe.storage.GameStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @PostMapping("")
    public ResponseEntity<Game> create(@RequestBody Player player){
        Game game = gameService.createGame(player);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/:id/connect")
    public ResponseEntity<Game> connectToGame(@RequestBody Player player2, @PathVariable String id) throws GameException {
        Game game = gameService.connectToGame(player2,id);
        return ResponseEntity.ok(game);
    }
}
