package com.technischools.telegramApp.utils;

import java.util.*;
import java.util.stream.Collectors;

public class NLPUtils {

    private static final Set<String> STOPWORDS = Set.of(
            "the", "is", "and", "of", "on", "in", "to", "with", "for", "a", "an", "by", "it", "at", "this", "that"
    );

    private static final Set<String> POLISH_STOPWORDS = Set.of(
            "a", "aby", "ach", "acz", "aczkolwiek", "ale", "albowiem", "ani", "aż", "bardziej",
            "bardzo", "bo", "bowiem", "by", "byli", "być", "byl", "bym", "byś", "chociaż",
            "czy", "daleko", "dla", "dlaczego", "dlatego", "do", "dobrze", "dokąd", "dużo",
            "wtedy", "gdzie", "gdy", "gdyby", "gdyż", "go", "i", "ich", "ile", "im", "inna",
            "inne", "inny", "innych", "iż", "ja", "ją", "jak", "jaka", "jakie", "jakiś", "jaki",
            "jednak", "jego", "jej", "już", "kiedy", "kilka", "kim", "kto", "który", "której",
            "którzy", "która", "któremu", "którym", "którą", "ku", "lat", "lecz", "lub", "ma",
            "mają", "mając", "mam", "między", "mi", "mną", "mnie", "mimo", "miło", "można",
            "musi", "na", "nad", "nam", "nami", "nas", "nasi", "nasz", "nasza", "nasze",
            "naszego", "naszych", "natomiast", "natychmiast", "nawet", "nic", "nich", "nie",
            "niego", "niej", "niemu", "nigdy", "nim", "nimi", "niż", "no", "o", "obok", "od",
            "około", "on", "ona", "one", "oni", "ono", "oraz", "oto", "podczas", "pod", "po",
            "ponad", "ponieważ", "powinien", "powinna", "powinno", "poza", "prawie", "przed",
            "przez", "przy", "również", "się", "sam", "sama", "sobie", "są", "swoje", "ta",
            "tak", "taka", "taki", "takie", "takiż", "tam", "te", "tego", "tej", "ten", "teraz",
            "też", "to", "tobą", "tobie", "toteż", "tutaj", "twoi", "twój", "twa", "twe", "twoja",
            "twoje", "twych", "ty", "tylko", "tym", "u", "w", "wam", "wami", "was", "wasi",
            "wasz", "wasza", "wasze", "we", "według", "wiele", "więcej", "wszyscy", "wszelki",
            "wszędzie", "wciąż", "wy", "z", "za", "zawsze", "ze", "zł", "znowu", "znów",
            "zostanie", "został", "że", "żaden", "żeby"
    );


    public static List<String> extractKeywords(String text) {
        // Split text into words
        String[] words = text.toLowerCase().split("\\W+"); // Split on non-word characters
        return Arrays.stream(words)
                .filter(word -> !POLISH_STOPWORDS.contains(word) && word.length() > 2) // Filter out stopwords and short words
                .collect(Collectors.toList());
    }
}
