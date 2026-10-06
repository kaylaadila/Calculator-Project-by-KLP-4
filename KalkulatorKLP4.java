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
