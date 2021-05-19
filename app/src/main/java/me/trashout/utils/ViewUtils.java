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

package me.trashout.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.trashout.R;
import me.trashout.model.Image;

public class ViewUtils {

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Hide system keyboard
     *
     * @param target
     */
    public static void hideIme(View target) {
        if (target != null) {
            InputMethodManager imm = (InputMethodManager) target.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
        }
    }

    /**
     * Check email valid
     *
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (android.text.TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Check password valid
     *
     * @param target
     * @return
     */
    public static boolean isValidPassword(CharSequence target) {
        if (android.text.TextUtils.isEmpty(target)) {
            return false;
        } else {
            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=\\S+$).{8,50}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(target);
            return matcher.matches();
        }
    }

    /**
     * Create and return divider view
     *
     * @return
     */
    public static View getDividerView(Context context) {
        View divider = new View(context);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        int sideMargin = ViewUtils.getPixelsFromDp(context, 20);
        int topBottomMargin = ViewUtils.getPixelsFromDp(context, 10);
        lp.setMargins(sideMargin, topBottomMargin, sideMargin, topBottomMargin);
        divider.setLayoutParams(lp);
        divider.setBackgroundColor(ContextCompat.getColor(context, R.color.separator_light));
        return divider;
    }


    /**
     * Check if image storage is not empty
     * @param image
     * @return
     */
    public static boolean checkImageStorage(Image image) {
        return image != null && !TextUtils.isEmpty(image.getFullStorageLocation());
    }
}
