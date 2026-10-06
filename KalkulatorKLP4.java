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
            "4", "5", "6", "+",
            "1", "2", "3", "=",
            "0", ".", "00", "+/-"
        };

        Font btnFont = new Font("SansSerif", Font.BOLD, 20);

        for (String cmd : cmds) {
            JButton btn = new JButton(cmd);
            btn.setActionCommand(cmd);
            btn.setFont(btnFont);
            btn.addActionListener(this);
            Color bg;
            if (cmd.equals("C")) bg = OP_RED;
            else if (cmd.equals(BACK)) bg = OP_PEACH;
            else if (cmd.equals(MUL)) bg = OP_GOLD;
            else if (cmd.equals(DIV)) bg = OP_PURPLE;
            else if (cmd.equals("+")) bg = OP_BLUE;
            else if (cmd.equals("-")) bg = OP_GREEN;
            else if (cmd.equals("=")) bg = OP_CORAL;
            else bg = NUM_BG;
            Color fg = bg.equals(NUM_BG) ? NUM_FG : Color.WHITE;
            styleButton(btn, bg, fg);
            buttons.add(btn);
        }
        main.add(buttons, BorderLayout.CENTER);
        add(main);

        buildHistoryDialog();
    }

    private void styleButton(JButton b, Color bg, Color fg) {
        b.setFocusPainted(false);
        b.setFocusable(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.getModel().addChangeListener(ev -> {
            ButtonModel m = b.getModel();
            if (m.isPressed()) b.setBackground(scale(bg, 0.72));
            else if (m.isRollover()) b.setBackground(scale(bg, 1.15));
            else b.setBackground(bg);
        });
    }

    private Color scale(Color c, double f) {
        return new Color(
            Math.min(255, (int)(c.getRed()*f)),
            Math.min(255, (int)(c.getGreen()*f)),
            Math.min(255, (int)(c.getBlue()*f)));
    }

    // ===== HISTORY =====
    private void buildHistoryDialog() {
        historyDialog = new JDialog(this, "History", false);
        historyDialog.setSize(340, 500);
        historyDialog.setResizable(false);
        historyDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAROON_MID);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("HISTORY", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(CREAM);
        panel.add(title, BorderLayout.NORTH);
        
        historyModel = new DefaultListModel<>();
        JList<String> list = new JList<>(historyModel);
        list.setBackground(MAROON_DARK);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer((l, value, index, sel, focus) -> {
            JPanel cell = new JPanel();
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBackground(sel ? MAROON_BRIGHT : MAROON_DARK);
            cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x5A, 0x20, 0x20)),
                new EmptyBorder(8, 12, 8, 12)));

            int idx = value.lastIndexOf(" = ");
            String e = idx >= 0 ? value.substring(0, idx) : value;
            String r = idx >= 0 ? value.substring(idx + 3) : "";

             JLabel el = new JLabel(e);
            el.setFont(new Font("SansSerif", Font.PLAIN, 14));
            el.setForeground(new Color(0xD0, 0xA8, 0xA8));
            JLabel rl = new JLabel("= " + r);
            rl.setFont(new Font("SansSerif", Font.BOLD, 18));
            rl.setForeground(CREAM);
            cell.add(el);
            cell.add(rl);
            return cell;
        });
        list.addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int idx = list.getSelectedIndex();
            if (idx >= 0) { useHistoryItem(idx); list.clearSelection(); }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x6A, 0x28, 0x28)));
        scroll.getViewport().setBackground(MAROON_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        JButton clearBtn = new JButton("Clear History");
        clearBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        styleButton(clearBtn, OP_RED, Color.WHITE);
        clearBtn.addActionListener(ev -> { history.clear(); refreshHistoryList(); });

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        styleButton(closeBtn, NUM_BG, NUM_FG);
        closeBtn.addActionListener(ev -> historyDialog.setVisible(false));

        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        row.add(clearBtn);
        row.add(closeBtn);
        panel.add(row, BorderLayout.SOUTH);

        historyDialog.add(panel);
        refreshHistoryList();
    }

    private void showHistory() {
        refreshHistoryList();
        historyDialog.setSize(340, 500);
        historyDialog.setLocationRelativeTo(this);
        historyDialog.setVisible(true);
        historyDialog.toFront();
    }

    private void refreshHistoryList() {
        historyModel.clear();
        for (int i = history.size() - 1; i >= 0; i--) historyModel.addElement(history.get(i));
    }

    private void addHistory(String e, double r) {
        if (Double.isNaN(r) || Double.isInfinite(r)) return;
        if (e.matches("-?[0-9.]+")) return;
        history.add(e + " = " + formatResult(r));
        refreshHistoryList();
    }

    private void useHistoryItem(int modelIndex) {
        int hi = history.size() - 1 - modelIndex;
        if (hi < 0 || hi >= history.size()) return;
        String entry = history.get(hi);
        int idx = entry.lastIndexOf(" = ");
        expr = idx >= 0 ? entry.substring(0, idx) : entry;
        justEvaluated = false;
        errorState = false;
        updateDisplay();
        historyDialog.setVisible(false);
    }
    
    // ===== AKSI TOMBOL =====
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (errorState && !cmd.equals("C")) {
            errorState = false; expr = ""; justEvaluated = false;
        }

                if (cmd.matches("[0-9]") || cmd.equals(".") || cmd.equals("00")) {
            if (justEvaluated) { expr = ""; justEvaluated = false; }
            if (cmd.equals(".")) {
                String run = currentNumber(expr);
                if (run.isEmpty()) expr += "0.";
                else if (!run.contains(".")) expr += ".";
            } else {
                if (currentNumber(expr).equals("0")) expr = expr.substring(0, expr.length() - 1) + cmd;
                else expr += cmd;
            }
                    } else if (cmd.equals("C")) {
            expr = ""; justEvaluated = false; errorState = false;
        } else if (cmd.equals(BACK)) {
            if (errorState) { errorState = false; expr = ""; return; }
            justEvaluated = false;
            if (!expr.isEmpty()) expr = expr.substring(0, expr.length() - 1);
        } else if (cmd.equals("+/-")) {
            handleNegation();
        } else if (cmd.equals("=")) {
            if (justEvaluated || expr.isEmpty()) return;
            try {
                                double r = evaluate(expr);
                addHistory(expr, r);
                expr = formatResult(r);
                justEvaluated = true;
            } catch (RuntimeException ex) {
                errorState = true; expr = ""; justEvaluated = false;
            }
                    } else {
            if (justEvaluated) justEvaluated = false;
            if (expr.isEmpty()) expr = "0" + cmd;
            else if (isOperator(expr.charAt(expr.length() - 1)))
                expr = expr.substring(0, expr.length() - 1) + cmd;
            else expr += cmd;
        }
        updateDisplay();
    }

    // Ubah tanda operand terakhir (+/-)
    private void handleNegation() {
        if (errorState) { errorState = false; expr = ""; justEvaluated = false; }
        justEvaluated = false;
        if (expr.isEmpty()) { expr = "-"; return; }

        // cari awal angka terakhir
        int end = expr.length();
        // lewati kalau ada spasi di akhir? tidak ada, format kita tanpa spasi
        int i = end;
        while (i > 0 && (Character.isDigit(expr.charAt(i - 1)) || expr.charAt(i - 1) == '.')) i--;

        if (i == 0) {
            // seluruh expr angka → tinggal tambah/hapus minus di depan
            if (expr.startsWith("-")) expr = expr.substring(1);
            else expr = "-" + expr;
        } else if (expr.charAt(i - 1) == '-') {
            // kalau sebelumnya minus dan bukan operator minus (mis. "5-3" jangan diubah jadi "53")
            // kita hanya ubah kalau '-' itu tanda negatif, bukan operator.
            // Cek karakter sebelum '-':
            if (i - 2 >= 0 && isOperator(expr.charAt(i - 2))) {
                // "5--3" → "5-3"? terlalu ribet, cukup:
                expr = expr.substring(0, i - 1) + expr.substring(i);
            } else {
                // "-3" di awal → "3"
                expr = expr.substring(0, i - 1) + expr.substring(i);
            }
        } else {
            expr = expr.substring(0, i) + "-" + expr.substring(i);
        }
    }
    
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == MULC || c == DIVC;
    }
    
    private String currentNumber(String s) {
        int i = s.length();
        while (i > 0 && (Character.isDigit(s.charAt(i - 1)) || s.charAt(i - 1) == '.')) i--;
        return s.substring(i);
    }
    
    private void updateDisplay() {
        String text = errorState ? "Error" : (expr.isEmpty() ? "0" : expr);
        display.setText(text);
    }
    
    // ===== Parser sederhana =====
    private String src;
    private int pos;

    private double evaluate(String s) {
        src = s; pos = 0;
        double v = parseExpr();
        if (pos != src.length()) throw new ArithmeticException();
        if (Double.isNaN(v) || Double.isInfinite(v)) throw new ArithmeticException();
        return v;
    }
    
    private double parseExpr() {
        double v = parseTerm();
        while (pos < src.length()) {
            char c = src.charAt(pos);
            if (c == '+') { pos++; v += parseTerm(); }
            else if (c == '-') { pos++; v -= parseTerm(); }
            else break;
        }
        return v;
    }
