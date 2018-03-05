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
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.RadioGroup;


public class UncheckedRadioButton extends AppCompatRadioButton {
    private RadioGroup radioGroup;
    private boolean hack = true;

    public UncheckedRadioButton(Context context) {
        super(context);
        init();
    }

    public UncheckedRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UncheckedRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(radioClickListener);
        setOnCheckedChangeListener(radioCheckChangeListener);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (UncheckedRadioButton.this.getParent() != null && UncheckedRadioButton.this.getParent() instanceof RadioGroup) {
                    radioGroup = (RadioGroup) UncheckedRadioButton.this.getParent();
                }
                hack = true;
                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    View.OnClickListener radioClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (radioGroup == null) {
                return;
            }
            if (v.getId() == radioGroup.getCheckedRadioButtonId() && hack) {
                radioGroup.clearCheck();
            } else {
                hack = true;
            }
        }
    };

    CompoundButton.OnCheckedChangeListener radioCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            hack = false;
        }
    };
}
