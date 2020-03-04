package com.example.developer.instgramclone.Utils;

public class Validation {

    public static boolean checkInput(String input) {
        if (input.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
