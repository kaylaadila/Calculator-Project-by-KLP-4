import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

public class KalkulatorKLP4 extends JFrame implements ActionListener {
    private static final String MUL="\u00d7", DIV="\u00f7", BACK="\u232b";
    private static final char MULC='\u00d7', DIVC='\u00f7';

    private JTextField display;
    private JDialog historyDialog;
    private DefaultListModel<String> historyModel;
    private ArrayList<String> history = new ArrayList<>();

    private String expr = "";
    private boolean justEvaluated = false, errorState = false;

    // ===== TEMA  =====
    private static final Color MAROON_DARK   = new Color(0x2B, 0x0A, 0x0A);
    private static final Color MAROON_MID    = new Color(0x4A, 0x10, 0x14);
    private static final Color MAROON_BRIGHT = new Color(0x80, 0x1F, 0x2A);
    private static final Color CREAM         = new Color(0xF5, 0xE6, 0xE0);
    private static final Color NUM_BG        = new Color(0xE8, 0xD5, 0xD0);
    private static final Color NUM_FG        = new Color(0x2B, 0x0A, 0x0A);
    private static final Color OP_RED        = new Color(0xB3, 0x2D, 0x3A);
    private static final Color OP_PEACH      = new Color(0xD9, 0x7A, 0x7A);
    private static final Color OP_GOLD       = new Color(0xD4, 0xA0, 0x4A);
    private static final Color OP_PURPLE     = new Color(0x7A, 0x2E, 0x5A);
    private static final Color OP_BLUE       = new Color(0x5A, 0x3A, 0x6E);
    private static final Color OP_GREEN      = new Color(0x3E, 0x6B, 0x4A);
    private static final Color OP_CORAL      = new Color(0xE0, 0x4A, 0x5A);

    public KalkulatorKLP4() {
        setTitle("Kalkulator - KLP 4");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(MAROON_MID);
        main.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ===== Display =====
        JPanel displayPanel = new JPanel(new BorderLayout(5, 5));
        displayPanel.setBackground(CREAM);
        displayPanel.setBorder(new EmptyBorder(10, 15, 12, 15));

        JPanel historyBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        historyBar.setOpaque(false);
        JButton historyBtn = new JButton("HISTORY");
        historyBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        historyBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        styleButton(historyBtn, MAROON_BRIGHT, Color.WHITE);
        historyBtn.addActionListener(ev -> showHistory());
        historyBar.add(historyBtn);

        display = new JTextField("0");
        display.setFont(new Font("SansSerif", Font.BOLD, 36));
        display.setForeground(NUM_FG);
        display.setBackground(CREAM);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBorder(null);
        display.setEditable(false);
        display.setFocusable(false);

        displayPanel.add(historyBar, BorderLayout.NORTH);
        displayPanel.add(display, BorderLayout.CENTER);
        main.add(displayPanel, BorderLayout.NORTH);

        // ===== Buttons (1 "=" saja, slot terakhir = "+/-") =====
        JPanel buttons = new JPanel(new GridLayout(5, 4, 6, 6));
        buttons.setOpaque(false);
        String[] cmds = {
            "C", BACK, DIV, MUL,
            "7", "8", "9", "-",
