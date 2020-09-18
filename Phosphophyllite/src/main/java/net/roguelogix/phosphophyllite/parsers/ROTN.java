package net.roguelogix.phosphophyllite.parsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ROTN {
    public Element parseString(String string) {
        List<Character> characters = string.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Iterator<Character> iterator = characters.iterator();
        iterator.next();
        return parseElement(null, iterator);
    }
    
    private Element parseElement(String name, Iterator<Character> nextCharacter) {
        StringBuilder nextElementType = new StringBuilder();
        char newChar;
        while ((newChar = nextCharacter.next()) != ' ') {
            nextElementType.append(newChar);
        }
        switch (nextElementType.toString()) {
            case "section": {
                return parseSection(name, nextCharacter);
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private Element parseSection(String name, Iterator<Character> nextCharacter) {
        ArrayList<Element> elements = new ArrayList<>();
        return null;
    }
}
