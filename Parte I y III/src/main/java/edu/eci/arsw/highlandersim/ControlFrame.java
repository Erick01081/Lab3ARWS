package edu.eci.arsw.highlandersim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class ControlFrame extends JFrame {

    private static final int DEFAULT_IMMORTAL_HEALTH = 100;
    private static final int DEFAULT_DAMAGE_VALUE = 10;

    private JPanel contentPane;
    private List<Immortal> immortals;
    private JTextArea output;
    private JLabel statisticsLabel;
    private JScrollPane scrollPane;
    private JTextField numOfImmortals;
    private final Object immortalsLock = new Object();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ControlFrame frame = new ControlFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ControlFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 647, 248);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JToolBar toolBar = new JToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);

        final JButton btnStart = new JButton("Start");
        btnStart.addActionListener(e -> {
            immortals = setupInmortals();
            if (immortals != null) {
                for (Immortal im : immortals) {
                    im.start();
                }
            }
            btnStart.setEnabled(false);
        });
        toolBar.add(btnStart);

        JButton btnPauseAndCheck = new JButton("Pause and check");
        btnPauseAndCheck.addActionListener(e -> {
            synchronized (immortalsLock) {
                for (Immortal i : immortals) {
                    i.pause();
                }
                try {
                    Thread.sleep(100);  // Give time for all immortals to pause
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                int sum = immortals.stream().mapToInt(Immortal::getHealth).sum();
                statisticsLabel.setText("<html>" + immortals.toString() + "<br>Health sum:" + sum);
            }
        });
        toolBar.add(btnPauseAndCheck);

        JButton btnResume = new JButton("Resume");
        btnResume.addActionListener(e -> {
            synchronized (immortalsLock) {
                for (Immortal i : immortals) {
                    i.resumeThread();
                }
            }
        });
        toolBar.add(btnResume);

        JLabel lblNumOfImmortals = new JLabel("num. of immortals:");
        toolBar.add(lblNumOfImmortals);

        numOfImmortals = new JTextField();
        numOfImmortals.setText("3");
        toolBar.add(numOfImmortals);
        numOfImmortals.setColumns(10);

        JButton btnStop = new JButton("STOP");
        btnStop.setForeground(Color.RED);
        btnStop.addActionListener(e -> {
            synchronized (immortalsLock) {
                for (Immortal i : immortals) {
                    i.stopThread();
                }
            }
        });
        toolBar.add(btnStop);

        scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        output = new JTextArea();
        output.setEditable(false);
        scrollPane.setViewportView(output);

        statisticsLabel = new JLabel("Immortals total health:");
        contentPane.add(statisticsLabel, BorderLayout.SOUTH);
    }

    public List<Immortal> setupInmortals() {
        ImmortalUpdateReportCallback ucb = new TextAreaUpdateReportCallback(output, scrollPane);

        try {
            int ni = Integer.parseInt(numOfImmortals.getText());
            List<Immortal> il = new CopyOnWriteArrayList<>();
            for (int i = 0; i < ni; i++) {
                Immortal i1 = new Immortal("im" + i, il, DEFAULT_IMMORTAL_HEALTH, DEFAULT_DAMAGE_VALUE, ucb);
                il.add(i1);
            }
            return il;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number.");
            return null;
        }
    }
}

class TextAreaUpdateReportCallback implements ImmortalUpdateReportCallback {
    JTextArea ta;
    JScrollPane jsp;

    public TextAreaUpdateReportCallback(JTextArea ta, JScrollPane jsp) {
        this.ta = ta;
        this.jsp = jsp;
    }

    @Override
    public void processReport(String report) {
        SwingUtilities.invokeLater(() -> {
            ta.append(report);
            JScrollBar bar = jsp.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}