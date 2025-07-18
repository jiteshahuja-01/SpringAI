package com.example.SpringAI.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TextSplitter {
    public List<String> split(String text) {
        // Simple split by paragraph
        return Arrays.stream(text.split("\\n\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
