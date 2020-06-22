package com.scarlatti;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 6/19/2020
 */
public class JavaFxWindowCapturer {

    private Pane pane;
    private Window window;
    private Window stageWindow;
    private ObservableValue<Boolean> visibleProperty;

    public JavaFxWindowCapturer(Pane pane, ObservableValue<Boolean> visibleProperty) {
        this.pane = pane;
        this.visibleProperty = visibleProperty;
    }

    public void captureWindow(String title) {
        if (window != null)
            ejectWindow(window);

        window = findWindow(title).orElseThrow(IllegalStateException::new);
        captureWindow(window);
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
        window.embeddedStyle = styleLong;

        User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE, new Pointer(styleLong));

        // todo get the handle to the stage
        stageWindow = getStageWindow(pane);
        User32.INSTANCE.SetParent(hWnd, stageWindow.hWnd);
        User32.INSTANCE.SetForegroundWindow(hWnd);


        Bounds bounds = pane.localToScene(pane.getBoundsInLocal());

//        boolean setWindowPosSuccess = User32.INSTANCE.SetWindowPos(hWnd , stageWindow.hWnd, (int) bounds.getMinX() , (int) bounds.getMinY() , (int) bounds.getWidth(), (int) bounds.getHeight(), 0);
//        boolean setWindowPosSuccess = User32.INSTANCE.SetWindowPos(hWnd , stageWindow.hWnd, 5 , 5 , 600, 200, 0);
        boolean setWindowPosSuccess = User32.INSTANCE.MoveWindow(hWnd , (int) bounds.getMinX() , (int) bounds.getMinY() , (int) bounds.getWidth(), (int) bounds.getHeight(), true);
        System.out.println("set window position success: " + setWindowPosSuccess);

        // when the pane is resized, resize the window at the same time
        ChangeListener<Bounds> resizeListener = new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                Bounds bounds = pane.localToScene(pane.getBoundsInLocal());
                User32.INSTANCE.MoveWindow(hWnd , (int) bounds.getMinX() , (int) bounds.getMinY() , (int) bounds.getWidth(), (int) bounds.getHeight(), true);
            }
        };
        pane.boundsInLocalProperty().addListener(resizeListener);
        window.resizeListener = resizeListener;

        ChangeListener<Boolean> visibleListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    // make it visible
                    User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE, new Pointer(window.embeddedStyle));

//                    // redraw window
                    User32.INSTANCE.RedrawWindow(stageWindow.hWnd, null, null, new User32.DWORD(User32.RDW_ALLCHILDREN | User32.RDW_INVALIDATE));
//                    User32.INSTANCE.RedrawWindow(hWnd, null, null, new User32.DWORD(User32.RDW_UPDATENOW));
                } else {
                    // hide it
                    Long style = window.embeddedStyle;
                    style &= ~User32.WS_VISIBLE;
                    User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE, new Pointer(style));
                    User32.INSTANCE.RedrawWindow(stageWindow.hWnd, null, null, new User32.DWORD(User32.RDW_ALLCHILDREN | User32.RDW_INVALIDATE));
                }
            }
        };

        // when the pane is not displayed, hide the window
        visibleProperty.addListener(visibleListener);
        window.visibleListener = visibleListener;






//        ComponentListener componentListener = new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                User32.INSTANCE.SetWindowPos(hWnd, new WinDef.HWND(Pointer.NULL), 0, 0, embedComponent.getWidth(), embedComponent.getHeight(), 0);
//            }
//        };
//
//        User32.INSTANCE.SetWindowPos(hWnd, new WinDef.HWND(Pointer.NULL), 0, 0, embedComponent.getWidth(), embedComponent.getHeight(), 0);
//
//        embedComponent.addComponentListener(componentListener);
//        window.canvasListener = componentListener;
    }

    private Window getStageWindow(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        String title = stage.getTitle();
        return findWindow(title).orElseThrow(IllegalStateException::new);
    }

    private void ejectWindow(Window window) {
        WinDef.HWND hWnd = window.hWnd;
        Long styleLong = window.originalStyle;

        User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_STYLE, new Pointer(styleLong));

        User32.INSTANCE.SetParent(hWnd, null);
        User32.INSTANCE.SetForegroundWindow(hWnd);

        User32.INSTANCE.RedrawWindow(stageWindow.hWnd, null, null, new User32.DWORD(User32.RDW_ALLCHILDREN | User32.RDW_INVALIDATE));
        User32.INSTANCE.RedrawWindow(window.hWnd, null, null, new User32.DWORD(User32.RDW_ALLCHILDREN | User32.RDW_INVALIDATE));


        // remove embedComponent listener...
        pane.boundsInLocalProperty().removeListener(window.resizeListener);
        visibleProperty.removeListener(window.visibleListener);
    }

    private static class Window {
        String text;
        WinDef.HWND hWnd;
        Long originalStyle;
        Long embeddedStyle;
        ChangeListener<Bounds> resizeListener;
        ChangeListener<Boolean> visibleListener;

        @Override
        public String toString() {
            return "Window{" +
                "text='" + text + '\'' +
                ", hWnd=" + hWnd +
                '}';
        }
    }
}
