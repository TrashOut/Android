/*
 * TrashOut is an environmental project that teaches people how to recycle 
 * and showcases the worst way of handling waste - illegal dumping. All you need is a smart phone.
 *  
 *  
 * There are 10 types of programmers - those who are helping TrashOut and those who are not.
 * Clean up our code, so we can clean up our planet. 
 * Get in touch with us: help@trashout.ngo
 *  
 * Copyright 2017 TrashOut, n.f.
 *  
 * This file is part of the TrashOut project.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See the GNU General Public License for more details: <https://www.gnu.org/licenses/>.
 */

package me.trashout.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageButton;

import me.trashout.R;

public class SelectableImageButton extends ImageButton {


    private int backgroundSelectedColor = Color.TRANSPARENT;

    public SelectableImageButton(Context context) {
        super(context);
    }

    public SelectableImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(context, attrs);
        init();
    }

    public SelectableImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttribute(context, attrs);
        init();
    }

    private void setAttribute(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectableImageButton, 0, 0);
            backgroundSelectedColor = a.getColor(R.styleable.SelectableImageButton_sibBackgroundSelectedColor, backgroundSelectedColor);
            a.recycle();
        }

        setBackgroundSelectedColor(backgroundSelectedColor);
    }


    private void init() {

    }

    /**
     * Change background selected color
     * @param color
     */
    public void setBackgroundSelectedColor(int color) {

        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.background_trash_circle_button_selected);
        GradientDrawable shape = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.selected_background_shape).mutate();
        shape.setColor(color);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, shape);
        states.addState(new int[]{android.R.attr.state_selected}, shape);
        states.addState(new int[]{}, ContextCompat.getDrawable(getContext(), R.drawable.background_trash_circle_button));

        setBackground(states);
        invalidate();
    }

}
