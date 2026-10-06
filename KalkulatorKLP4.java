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
