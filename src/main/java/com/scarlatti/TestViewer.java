package com.scarlatti;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TestViewer extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel capturePanel;
    private JTextField captureThisWindow;
    private JButton captureButton;
    private JButton ejectButton;
    private JPasswordField passwordField1;
    private Panel embeddedWindowPanel;
    private JComponent jComponent;
    private JInternalFrame jInternalFrame;
    private Container container;
    private org.eclipse.swt.widgets.Canvas swtCanvas;
    private SwingWindowCapturer1 windowCapturer;
//    private SwingWindowCapturer2 windowCapturer;

    public TestViewer() {
        setContentPane(contentPane);
//        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        embeddedWindowPanel = new Panel();
        embeddedWindowPanel.setBackground(Color.GRAY);
        embeddedWindowPanel.setIgnoreRepaint(false);

        // try this
        capturePanel.add(embeddedWindowPanel);

        jComponent = new JComponent() {};
        jComponent.setSize(new Dimension(200,200));
        jComponent.setPreferredSize(new Dimension(200,200));
        jComponent.setVisible(true);
        jComponent.setOpaque(true);
        jComponent.setBackground(Color.GRAY);

        container = new Container();

        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captureWindow();
            }
        });

        ejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejectWindow();
            }
        });

        windowCapturer = new SwingWindowCapturer1(embeddedWindowPanel);
//        windowCapturer = new SwingWindowCapturer2(embeddedWindowPanel);

        setTitle("Test Capture Window");

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(new FlatIntelliJLaf());
        TestViewer dialog = new TestViewer();
        dialog.setVisible(true);
    }

    private void captureWindow() {
        windowCapturer.captureWindow(captureThisWindow.getText());
    }

    private void ejectWindow() {
        windowCapturer.ejectWindow();
    }

    public void setData(TestBean1 data) {
        captureThisWindow.setText(data.getName());
    }

    public void getData(TestBean1 data) {
        data.setName(captureThisWindow.getText());
    }

    public boolean isModified(TestBean1 data) {
        if (captureThisWindow.getText() != null ? !captureThisWindow.getText().equals(data.getName()) : data.getName() != null)
            return true;
        return false;
    }
}
