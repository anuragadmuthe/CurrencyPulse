import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealtimeCurrencyConverter extends JFrame {
    private JTextField amountField;
    private JComboBox<String> fromCurrency, toCurrency;
    private JLabel resultLabel;
    private JButton convertButton;

    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "INR"};
    private static final String API_KEY = "2e2f6a527e56f2a605a3c212";

    private static final Color BACKGROUND_START = new Color(25, 25, 112);
    private static final Color BACKGROUND_END = new Color(70, 130, 180);
    private static final Color FOREGROUND_COLOR = new Color(240, 240, 245);
    private static final Color ACCENT_COLOR = new Color(255, 215, 0);

    public RealtimeCurrencyConverter() {
        setTitle("Realtime Currency Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = createStyledLabel("Currency Converter", 36, Font.BOLD);
        titleLabel.setForeground(ACCENT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(createStyledLabel("Amount:", 18, Font.PLAIN), gbc);

        gbc.gridx = 1;
        amountField = createStyledTextField();
        mainPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(createStyledLabel("From:", 18, Font.PLAIN), gbc);

        gbc.gridx = 1;
        fromCurrency = createStyledComboBox();
        mainPanel.add(fromCurrency, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(createStyledLabel("To:", 18, Font.PLAIN), gbc);

        gbc.gridx = 1;
        toCurrency = createStyledComboBox();
        mainPanel.add(toCurrency, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        convertButton = createStyledButton("Convert");
        mainPanel.add(convertButton, gbc);

        gbc.gridy++;
        resultLabel = createStyledLabel("Result will appear here", 24, Font.BOLD);
        resultLabel.setForeground(ACCENT_COLOR);
        mainPanel.add(resultLabel, gbc);

        add(mainPanel);
    }

    private JLabel createStyledLabel(String text, int fontSize, int fontStyle) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", fontStyle, fontSize));
        label.setForeground(FOREGROUND_COLOR);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 18));
        textField.setForeground(BACKGROUND_START);
        textField.setBackground(FOREGROUND_COLOR);
        return textField;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(CURRENCIES);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        comboBox.setForeground(BACKGROUND_START);
        comboBox.setBackground(FOREGROUND_COLOR);
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(BACKGROUND_START);
        button.setBackground(ACCENT_COLOR);
        button.addActionListener(e -> convert());
        return button;
    }

    private void convert() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();

            String urlStr = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/" + from + "/" + to;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Pattern pattern = Pattern.compile("\"conversion_rate\":(\\d+\\.?\\d*)");
            Matcher matcher = pattern.matcher(response.toString());
            if (matcher.find()) {
                double rate = Double.parseDouble(matcher.group(1));
                double result = amount * rate;

                DecimalFormat df = new DecimalFormat("#,##0.00");
                resultLabel.setText(df.format(amount) + " " + from + " = " + df.format(result) + " " + to);
            } else {
                resultLabel.setText("Error: Couldn't parse the response");
            }
        } catch (Exception ex) {
            resultLabel.setText("Error: " + ex.getMessage());
        }
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_START, w, h, BACKGROUND_END);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RealtimeCurrencyConverter().setVisible(true);
        });
    }
}