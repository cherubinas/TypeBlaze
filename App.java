import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        Game game = new Game();
        game.initialize("../projektas1/lib/duom.txt");
        game.start();
       
    }
}

class Game {
    private Page page;
    private Map<String, List<String>> gameModes;

    public void initialize(String fileName) {
        Map<String, List<String>> dataMap = InOut.readData(fileName);
        List<String> textLines = dataMap.get("textLines");
        page = new Page();
        page.playMusic();
        gameModes = loadGameModes(textLines);

        page = new Page();
        page.initialize(textLines, gameModes);
    }

    public void start() {
        page.setVisible(true);


    }

    private Map<String, List<String>> loadGameModes(List<String> textLines) {
        Map<String, List<String>> gameModes = new HashMap<>();
        String currentMode = "";
        List<String> currentWordList = new ArrayList<>();

        for (String line : textLines) {
            if (line.startsWith("[")) {
                if (!currentMode.isEmpty()) {
                    gameModes.put(currentMode, new ArrayList<>(currentWordList));
                    currentWordList.clear();
                }
                currentMode = line.substring(1, line.length() - 1);
            } else {
                currentWordList.add(line);
            }
        }

        if (!currentMode.isEmpty()) {
            gameModes.put(currentMode, new ArrayList<>(currentWordList));
        }

        return gameModes;
    }
    
    
}
