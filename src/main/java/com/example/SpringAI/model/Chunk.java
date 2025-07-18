package com.example.SpringAI.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Chunk {
    private String text;
    private float[] embedding;

    public Chunk(String text) {
        this.text  = text;
    }

    // Getters & setters
}

