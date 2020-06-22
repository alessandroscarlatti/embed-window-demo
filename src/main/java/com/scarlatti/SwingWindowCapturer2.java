package com.scarlatti;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 6/19/2020
 */
public class SwingWindowCapturer2 {

    private Component embedComponent;
    private Window window;

    public SwingWindowCapturer2(Component embedComponent) {
        this.embedComponent = embedComponent;
    }

    public void captureWindow(String title) {
        if (window != null)
            ejectWindow(window);

        window = findWindow(title).orElseThrow(IllegalStateException::new);
        captureWindow(window);
        embedComponent.repaint();
    }

    public void ejectWindow() {
        if (window != null)
            ejectWindow(window);
    }

    private Optional<Window> findWindow(String text) {
        System.out.println(Kernel32.INSTANCE.GetCurrentProcessId());

        java.util.List<Window> windows = new ArrayList<>();

        User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hWnd, Pointer data) {

                char[] windowText = new char[512];
                User32.INSTANCE.GetWindowText(hWnd, windowText, 512);

                Window window = new Window();
                window.hWnd = hWnd;
                window.text = Native.toString(windowText).trim();
                window.originalStyle = User32.INSTANCE.GetWindowLongPtr(hWnd, WinUser.GWL_STYLE).longValue();

                windows.add(window);

                return true;
            }
        }, null);


        for (Window window : windows) {
            if (!text.trim().equals("") && window.text.contains(text)) {
                System.out.println(">>>" + window);
                return Optional.of(window);
            }
        }

        return Optional.empty();
    }

    private void captureWindow(Window window) {
        WinDef.HWND hWnd = window.hWnd;
        Long styleLong = window.originalStyle;
        styleLong &= ~WinUser.WS_CAPTION;
        styleLong &= ~WinUser.WS_THICKFRAME;

        User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE, new Pointer(styleLong));


        Pointer canvasPointer = Native.getComponentPointer(embedComponent);
        WinDef.HWND canvasHandle = new WinDef.HWND(canvasPointer);
//        User32.INSTANCE.SetParent(hWnd, canvasHandle);
        User32.INSTANCE.SetForegroundWindow(hWnd);

        // get the position of the window

        WinDef.HWND jFrameHandle = new WinDef.HWND(Native.getComponentPointer(SwingUtilities.getWindowAncestor(embedComponent)));


        WinDef.HWND HWND_TOP = new WinDef.HWND(new Pointer(0));
        WinDef.HWND HWND_TOPMOST = new WinDef.HWND(new Pointer(-1));

//        boolean setParentSuccess = User32.INSTANCE.SetWindowPos(hWnd , canvasHandle, embedComponent.getLocationOnScreen().x , embedComponent.getLocationOnScreen().y , embedComponent.getWidth(), embedComponent.getHeight(), 0);
//        boolean setParentSuccess = User32.INSTANCE.SetWindowPos(hWnd , new WinDef.HWND(Pointer.NULL), embedComponent.getLocationOnScreen().x , embedComponent.getLocationOnScreen().y , embedComponent.getWidth(), embedComponent.getHeight(), 0);
        boolean setParentSuccess = User32.INSTANCE.SetWindowPos(hWnd , HWND_TOP, embedComponent.getLocationOnScreen().x , embedComponent.getLocationOnScreen().y , embedComponent.getWidth(), embedComponent.getHeight(), 0);
        System.out.println("set parent success: " + setParentSuccess);

        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                User32.INSTANCE.SetWindowPos(hWnd , HWND_TOP, embedComponent.getLocationOnScreen().x , embedComponent.getLocationOnScreen().y , embedComponent.getWidth(), embedComponent.getHeight(), 0);            }
        };

//        User32.INSTANCE.SetWindowPos(hWnd, new WinDef.HWND(Pointer.NULL), 0, 0, embedComponent.getWidth(), embedComponent.getHeight(), 0);

        embedComponent.addComponentListener(componentListener);
        window.canvasListener = componentListener;

        SwingUtilities.getWindowAncestor(embedComponent).addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                System.out.println("activated");

                User32.INSTANCE.SetWindowPos(hWnd , HWND_TOPMOST, embedComponent.getLocationOnScreen().x , embedComponent.getLocationOnScreen().y , embedComponent.getWidth(), embedComponent.getHeight(), 0);


//                SwingWorker swingWorker = new SwingWorker() {
//                    @Override
//                    protected Object doInBackground() throws Exception {
//                    }
//                };
//
//                swingWorker.execute();
            }
        });

        SwingUtilities.getWindowAncestor(embedComponent).addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {
                User32.INSTANCE.SetWindowPos(hWnd , HWND_TOP, embedComponent.getLocationOnScreen().x , embedComponent.getLocationOnScreen().y , embedComponent.getWidth(), embedComponent.getHeight(), 0);
            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    private void ejectWindow(Window window) {
        WinDef.HWND hWnd = window.hWnd;
        Long styleLong = window.originalStyle;

        User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE, new Pointer(styleLong));

        User32.INSTANCE.SetParent(hWnd, null);
        User32.INSTANCE.SetForegroundWindow(hWnd);

        // remove embedComponent listener...
        embedComponent.removeComponentListener(window.canvasListener);
    }

    private static class Window {
        String text;
        WinDef.HWND hWnd;
        Long originalStyle;
        ComponentListener canvasListener;

        @Override
        public String toString() {
            return "Window{" +
                "text='" + text + '\'' +
                ", hWnd=" + hWnd +
                '}';
        }
    }
}
