package com.example.tictactoe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sign {
    X(1),Y(2);

    private Integer value;
}
