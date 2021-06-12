package com.example.tictactoe.model;

import lombok.Data;
import lombok.Getter;


@Data
@Getter
public class Move {
    private Sign sign;
    private Integer positionX;
    private Integer positionY;
    private String player;
}
