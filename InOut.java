import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InOut {
    public static Map<String, List<String>> readData(String fileName) {
        Map<String, List<String>> dataMap = new HashMap<>();
        List<String> textLines = new ArrayList<>();
        List<String> easyTextLines = new ArrayList<>();
        List<String> normalTextLines = new ArrayList<>();
        List<String> codeTextLines = new ArrayList<>();
        String punctuation = "";

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
            if (!lines.isEmpty()) {
                punctuation = lines.get(0);
                lines.remove(0);
            }
            textLines.addAll(lines);

            boolean isEasyMode = false;
            boolean isNormalMode = false;
            boolean isCodeMode = false;

            for (String line : textLines) {
                if (line.equalsIgnoreCase("[Easy Mode]")) {
                    isEasyMode = true;
                    isNormalMode = false;
                    isCodeMode = false;
                    continue;
                } else if (line.equalsIgnoreCase("[Normal Mode]")) {
                    isEasyMode = false;
                    isNormalMode = true;
                    isCodeMode = false;
                    continue;
                } else if (line.equalsIgnoreCase("[Code Text]")) {
                    isEasyMode = false;
                    isNormalMode = false;
                    isCodeMode = true;
                    continue;
                }

                if (isEasyMode) {
                    easyTextLines.add(line);
                } else if (isNormalMode) {
                    normalTextLines.add(line);
                } else if (isCodeMode) {
                    codeTextLines.add(line);
                }
            }

            dataMap.put("punctuation", new ArrayList<>(List.of(punctuation)));
            dataMap.put("textLines", textLines);
            dataMap.put("easyTextLines", easyTextLines);
            dataMap.put("normalTextLines", normalTextLines);
            dataMap.put("codeTextLines", codeTextLines);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataMap;
    }
}
