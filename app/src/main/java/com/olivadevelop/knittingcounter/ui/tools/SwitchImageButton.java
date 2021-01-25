package com.olivadevelop.knittingcounter.ui.tools;

import android.widget.Button;
import android.widget.ImageButton;

public class SwitchImageButton {
    private Button button;
    private ImageButton imageButton;
    private boolean active;

    public SwitchImageButton(Button button) {
        this.button = button;
    }

    public SwitchImageButton(ImageButton button) {
        this.imageButton = button;
    }

    public Button getButton() {
        return button;
    }

    public ImageButton getImageButton() {
        return imageButton;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
