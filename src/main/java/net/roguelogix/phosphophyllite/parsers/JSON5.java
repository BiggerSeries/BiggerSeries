package net.roguelogix.phosphophyllite.parsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JSON5 {
    public Element parseString(String string) {
        List<Character> characters = string.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Iterator<Character> iterator = characters.iterator();
        iterator.next();
        return parseString(null, Element.Type.Section, iterator);
    }
    
    private Element parseString(String name, Element.Type objectType, Iterator<Character> nextCharacter) {
        ArrayList<Element> subElements = new ArrayList<>();
        char lastChar = 0;
        mainLoop:
        while (true) {
            whitespaceLoop:
            while (true) {
                lastChar = nextCharacter.next();
                switch (lastChar) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        continue;
                    }
                    default:
                        break whitespaceLoop;
                }
            }
            switch (objectType) {
                case Value:
                    break;
                case Array:
                    if (lastChar == ']') {
                        break mainLoop;
                    }
                    break;
                case Section:
                    if (lastChar == '}') {
                        break mainLoop;
                    }
                    break;
            }
        }
        return new Element(Element.Type.Section, null, name, subElements);
    }
}
