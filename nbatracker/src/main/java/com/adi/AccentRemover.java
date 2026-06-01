package com.adi;

import java.text.Normalizer;
import java.util.regex.Pattern;


public class AccentRemover {
    public static String removeAccents(String text) {
        // Decompose characters (e.g., 'č' becomes 'c' + ' ˇ ')
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        
        // Remove the combining diacritical marks via regex
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}