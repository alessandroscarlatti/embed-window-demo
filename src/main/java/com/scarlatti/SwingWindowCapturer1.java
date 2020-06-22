package com.scarlatti;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 6/19/2020
 */
public class SwingWindowCapturer1 {

    private Component embedComponent;
    private Window window;

    public SwingWindowCapturer1(Component embedComponent) {
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
        User32.INSTANCE.SetParent(hWnd, canvasHandle);
        User32.INSTANCE.SetForegroundWindow(hWnd);
        boolean setParentSuccess = User32.INSTANCE.SetWindowPos(hWnd , canvasHandle, 0 , 0 , embedComponent.getWidth(), embedComponent.getHeight(), User32.SWP_ASYNCWINDOWPOS);
        System.out.println("set parent success: " + setParentSuccess);

        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                User32.INSTANCE.SetWindowPos(hWnd, new WinDef.HWND(Pointer.NULL), 0, 0, embedComponent.getWidth(), embedComponent.getHeight(), User32.SWP_ASYNCWINDOWPOS);
            }
        };

        User32.INSTANCE.SetWindowPos(hWnd, new WinDef.HWND(Pointer.NULL), 0, 0, embedComponent.getWidth(), embedComponent.getHeight(), User32.SWP_ASYNCWINDOWPOS);

        embedComponent.addComponentListener(componentListener);
        window.canvasListener = componentListener;
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
