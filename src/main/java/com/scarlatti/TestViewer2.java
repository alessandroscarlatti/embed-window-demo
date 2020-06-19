//package com.scarlatti;
//
//import com.sun.jna.Native;
//import com.sun.jna.Pointer;
//import com.sun.jna.platform.win32.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.util.ArrayList;
//import java.util.Optional;
//
//public class TestViewer2 extends JFrame {
//    private JPanel contentPane;
//    private JButton buttonOK;
//    private JButton buttonCancel;
//    private JPanel capturePanel;
//    private JTextField captureThisWindow;
//    private JButton captureButton;
//    private JButton ejectButton;
//    private Canvas canvas;
//    private JComponent jComponent;
//    private JInternalFrame jInternalFrame;
//    private Container container;
//    private org.eclipse.swt.widgets.Canvas swtCanvas;
//
//    public TestViewer2() {
//        setContentPane(contentPane);
////        setModal(true);
//        getRootPane().setDefaultButton(buttonOK);
//
//        buttonOK.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onOK();
//            }
//        });
//
//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });
//
//        // call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });
//
//        // call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//
//        canvas = new Canvas() {
//            @Override
//            public void setSize(int width, int height) {
//                System.out.println("setting size " + width + ", " + height);
//                super.setSize(width, height);
//            }
//
//            @Override
//            public void setSize(Dimension d) {
//                System.out.println("setting size " + d.getWidth() + ", " + d.getHeight());
//                super.setSize(d);
//            }
//        };
//        canvas.setBackground(Color.GRAY);
//        canvas.setIgnoreRepaint(false);
//
//        // try this
//        capturePanel.add(canvas);
//
//        jComponent = new JComponent() {};
//        jComponent.setSize(new Dimension(200,200));
//        jComponent.setPreferredSize(new Dimension(200,200));
//        jComponent.setVisible(true);
//        jComponent.setOpaque(true);
//        jComponent.setBackground(Color.GRAY);
//
//        container = new Container();
//
//        captureButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                captureWindow();
//            }
//        });
//
//        ejectButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        });
//    }
//
//    private void onOK() {
//        // add your code here
//        dispose();
//    }
//
//    private void onCancel() {
//        // add your code here if necessary
//        dispose();
//    }
//
//    public static void main(String[] args) {
//        TestViewer2 dialog = new TestViewer2();
//        dialog.pack();
//
////        dialog.capturePanel.setLayout(new BorderLayout());
////        dialog.canvas.setPreferredSize(new Dimension(200, 200));
////        dialog.canvas.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
//
////        dialog.swtCanvas = new org.eclipse.swt.widgets.Canvas(dialog.capturePanel, 0)
//
//
////        dialog.capturePanel.add(dialog.container, BorderLayout.CENTER);
//        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        System.out.println("displayable: " + dialog.jComponent.isDisplayable());
//
//        dialog.setVisible(true);
//    }
//
//    public void captureWindow() {
//        System.out.println(Kernel32.INSTANCE.GetCurrentProcessId());
//
////        JDesktopPane jDesktopPane = new JDesktopPane();
//
////        jInternalFrame = new JInternalFrame("Embed Squirrel");
////        jInternalFrame.setLocation(0,0);
////        jInternalFrame.setSize(100,100);
////        jInternalFrame.setVisible(true);
//
////        capturePanel.add(jInternalFrame, new GridConstraints());  // Grid constraints necessary to avoid nullptrexception
//
//
//        WinDef.HWND fakeSquirrel;
//
//
//
//
//    }
//
//    public Optional<Window> findWindow(String text) {
//
//        java.util.List<Window> windows = new ArrayList<>();
//
//        User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
//            @Override
//            public boolean callback(WinDef.HWND hWnd, Pointer data) {
//
//                char[] windowText = new char[512];
//                User32.INSTANCE.GetWindowText(hWnd, windowText, 512);
//
//                Window window = new Window();
//                window.hWnd = hWnd;
//                window.text = Native.toString(windowText).trim();
//
//                windows.add(window);
//
//                return true;
//            }
//        }, null);
//
//
//        for (Window window : windows) {
//            if (!captureThisWindow.getText().trim().equals("") && window.text.contains(captureThisWindow.getText())) {
//                System.out.println(">>>" + window);
//                captureWindow(window.hWnd);
//            }
//        }
//    }
//
//    public void captureWindow(WinDef.HWND hWnd) {
//        BaseTSD.LONG_PTR style = User32.INSTANCE.GetWindowLongPtr(hWnd, WinUser.GWL_STYLE);
//        Long styleLong = style.longValue();
////        styleLong |= WinUser.WS_CHILD;
////        styleLong |= WinUser.WS_CAPTION;
////        styleLong |= WinUser.WS_POPUP;
////        styleLong &= ~WinUser.WS_CAPTION;
////        styleLong &= ~WinUser.WS_POPUP;
//
//         User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE, new Pointer(styleLong));
//
//
//        Pointer canvasPointer = Native.getComponentPointer(canvas);
//        WinDef.HWND canvasHandle = new WinDef.HWND(canvasPointer);
//        User32.INSTANCE.SetParent(hWnd, canvasHandle);
//        User32.INSTANCE.SetForegroundWindow(hWnd);
//        boolean setParentSuccess = User32.INSTANCE.SetWindowPos(hWnd , canvasHandle, 0 , 0 , canvas.getWidth(), canvas.getHeight(), 0);
//        System.out.println("set parent success: " + setParentSuccess);
//
//        canvas.addComponentListener(
//            new ComponentAdapter() {
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    User32.INSTANCE.SetWindowPos(hWnd, new WinDef.HWND(Pointer.NULL), 0, 0, canvas.getWidth(), canvas.getHeight(), 0);
//                }
//            }
//        );
//
//
////        User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
////            @Override
////            public boolean callback(WinDef.HWND ideHWND, Pointer data) {
////
////                char[] windowText = new char[512];
////                User32.INSTANCE.GetWindowText(ideHWND, windowText, 512);
////                String wText = Native.toString(windowText);
////
////                System.out.println(">>>" + wText.trim());
////
////                if (wText.trim().equals(captureThisWindow.getText())) {  // this should fail, but that's OK.
////
////                    User32.INSTANCE.SetParent(hWnd, ideHWND);
////                    User32.INSTANCE.SetForegroundWindow(hWnd);
////
////                }
////
////                return true;
////            }
////        }, null);
//    }
//
//    private void ejectWindow() {
//
//    }
//
//    private static class Window {
//        String text;
//        WinDef.HWND hWnd;
//
//        @Override
//        public String toString() {
//            return "Window{" +
//                "text='" + text + '\'' +
//                ", hWnd=" + hWnd +
//                '}';
//        }
//    }
//}
