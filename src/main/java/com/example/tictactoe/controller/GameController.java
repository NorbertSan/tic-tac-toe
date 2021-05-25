package com.example.tictactoe.controller;

import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.Player;
import com.example.tictactoe.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
