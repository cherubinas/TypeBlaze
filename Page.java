import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Page extends JFrame {
    private JTextField textField;
    private List<String> wordList;
    private int currentIndex;
    private JLabel textLabel;
    private long startTime = 0;
    private long endTime;
    private Timer timer;
    private JButton stopButton;
    private JButton tryAgainButton;
    private JButton exitButton;
    private int correctWordsCount;
    private JPanel modeButtonPanel;
    private Map<String, List<String>> gameModes;

    public void initialize(List<String> textLines, Map<String, List<String>> gameModes) {

        setTitle("TypeBlaze");
        getContentPane().setBackground(new Color(50, 100, 200)); 

        setSize(500, 600);
        setMinimumSize(new Dimension(300, 400));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Font buttonFont = new Font("Monospaced", Font.BOLD, 16);
        

        stopButton = new JButton("Stop");
        stopButton.setFont(buttonFont);
        stopButton.setBackground(Color.BLACK);
        stopButton.setForeground(Color.WHITE);

        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBackground(Color.BLACK);
        tryAgainButton.setForeground(Color.WHITE);
        tryAgainButton.setFont(buttonFont);

        exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();

        buttonPanel.setOpaque(false);

        buttonPanel.add(stopButton);
        buttonPanel.add(tryAgainButton);
        buttonPanel.add(exitButton);
        
        JPanel upperPanel = new JPanel(new BorderLayout());
        upperPanel.setOpaque(false);
        textLabel = new JLabel();
        textLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        textLabel.setForeground(Color.WHITE);
        upperPanel.add(textLabel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new GridBagLayout());
        middlePanel.setOpaque(false);
        textField = new JTextField(20);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);
        middlePanel.add(textField, gbc);

        JPanel contentPane = new CustomImagePanel(); 
contentPane.setLayout(new BorderLayout());
contentPane.add(upperPanel, BorderLayout.NORTH);
contentPane.add(middlePanel, BorderLayout.CENTER);
contentPane.add(buttonPanel, BorderLayout.SOUTH);
setContentPane(contentPane);


        textField.addActionListener(e -> handleTextFieldInput());

        stopButton.addActionListener(e -> stopGame());

        tryAgainButton.addActionListener(e -> restartGame());

        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);

        textLabel.setVisible(false);
        buttonPanel.setVisible(true);

        this.gameModes = gameModes;
        selectGameMode();
    }

    private void handleTextFieldInput() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
            startTimerUpdate();
        }
        String input = textField.getText();
        checkMatch(input);
        textField.setText("");
    }

    private void startTimerUpdate() {
        timer = new Timer(1000, e -> updateTextLabel());
        timer.start();
    }

    private void updateTextLabel() {
        StringBuilder sb = new StringBuilder("<html>");

        if (startTime != 0) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            sb.append("<span style='font-weight: bold;'>").append(minutes).append(" minutes ").append(seconds)
                    .append(" seconds </span><br><br>");
        }

        for (int i = 0; i < wordList.size(); i++) {
            String word = wordList.get(i);
            if (i < currentIndex) {
                sb.append("<span style='color: #999999;'>").append(word).append(" </span>");
            } else if (i == currentIndex) {
                sb.append("<span style='font-weight: bold;'>").append(word).append(" </span>");
            } else {
                sb.append("<span>").append(word).append(" </span>");
            }
        }
        sb.append("</html>");
        textLabel.setText(sb.toString());
    }

    private void checkMatch(String input) {
        if (!wordList.isEmpty()) {
            String currentWord = wordList.get(currentIndex);

            if (input.equals(currentWord)) {
                currentIndex++;
                correctWordsCount++;
                if (currentIndex < wordList.size()) {
                    updateTextLabel();
                } else {
                    stopGame();
                }
            } else {
                StringBuilder sb = new StringBuilder("<html>");
                sb.append("<span style='font-weight: bold;'>").append(getFormattedElapsedTime())
                        .append("</span><br><br>");
                for (int i = 0; i < wordList.size(); i++) {
                    String word = wordList.get(i);
                    if (i < currentIndex) {
                        sb.append("<span style='color: #999999;'>").append(word).append(" </span>");
                    } else if (i == currentIndex) {
                        sb.append("<span style='color: red;'>").append(word).append(" </span>");
                    } else {
                        sb.append("<span>").append(word).append(" </span>");
                    }
                }
                sb.append("</html>");
                textLabel.setText(sb.toString());
            }
        }

        if (currentIndex == wordList.size()) {
            stopGame();
        }
    }

    private void stopGame() {
        endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;

        String elapsedTimeString = "Elapsed Time: " + minutes + " minutes " + seconds + " seconds";

        if (minutes == 0 && seconds == 0) {
            elapsedTimeString = "Elapsed Time: 0 minutes 0 seconds";
        } else if (startTime == 0) {
            elapsedTimeString = "Elapsed Time: 0 minutes 0 seconds";
        }

        String resultMessage = String.format("<html><center>Game Stopped!<br>%s<br>Correct Words: %d</center></html>",
                elapsedTimeString, correctWordsCount);
        textLabel.setText(resultMessage);

        timer.stop();
        textField.setEnabled(false);
        stopButton.setEnabled(false);
        tryAgainButton.setEnabled(true);
        textLabel.setVisible(true);
        modeButtonPanel.setVisible(false);
    }
    public void playMusic() {
        try {
        
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("lib/funky.wav"));
    
    
            Clip clip = AudioSystem.getClip();
    
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void restartGame() {
        currentIndex = 0;
        correctWordsCount = 0;
        startTime = 0;
        endTime = 0;
        textField.setText("");
        textField.setEnabled(true);
        stopButton.setEnabled(true);
        tryAgainButton.setEnabled(false);
        updateTextLabel();
        modeButtonPanel.setVisible(true);
        textLabel.setVisible(true);

        wordList = null;

        selectGameMode();
    }

    private String getFormattedElapsedTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;
        return minutes + " minutes " + seconds + " seconds";
    }

    public void setModeButtonPanel(JPanel modeButtonPanel) {
        this.modeButtonPanel = modeButtonPanel;
    }

    private void selectGameMode() {
        List<String> modeOptions = new ArrayList<>(gameModes.keySet());
        Object[] options = modeOptions.toArray();
        UIManager.put("OptionPane.background", Color.BLACK); 
        UIManager.put("OptionPane.messageForeground", Color.WHITE); 
        UIManager.put("OptionPane.messageFont", new Font("Monospaced", Font.BOLD, 16)); 
        UIManager.put("Panel.background", Color.BLACK); 
        UIManager.put("Button.background", Color.BLACK); 
        UIManager.put("Button.foreground", Color.WHITE); 
        UIManager.put("Button.font", new Font("Monospaced", Font.BOLD, 14));
        int selectedOption = JOptionPane.showOptionDialog(this, "Select game mode:", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedOption >= 0 && selectedOption < modeOptions.size()) {
            String selectedMode = modeOptions.get(selectedOption);
            List<String> selectedWordList = gameModes.get(selectedMode);
            startGame(selectedWordList);
        }
    }

    public void startGame(List<String> wordList) {
        if (wordList == null || wordList.isEmpty()) {
            return;
        }


        this.wordList = wordList;
        currentIndex = 0;
        startTime = 0;
        endTime = 0;
        correctWordsCount = 0;
        updateTextLabel();
        textField.setEnabled(true);
        stopButton.setEnabled(true);
        tryAgainButton.setEnabled(false);
        textLabel.setVisible(true);
        modeButtonPanel.setVisible(false);
    }
    class CustomImagePanel extends JPanel {
        private Image backgroundImage;
    
        public CustomImagePanel() {
            try {
                String filePath = "lib/base64.txt"; 
                String hiddenBase64 = new String(Files.readAllBytes(Paths.get(filePath))).trim();
    
              
                byte[] decodedBytes = Base64.getDecoder().decode(hiddenBase64);
                ImageIcon imageIcon = new ImageIcon(decodedBytes);
                backgroundImage = imageIcon.getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
    }

