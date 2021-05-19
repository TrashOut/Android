package me.trashout.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import me.trashout.fragment.TutorialFragmentItem;

import static me.trashout.fragment.TutorialFragmentItem.TUTORIAL_PAGE_1;
import static me.trashout.fragment.TutorialFragmentItem.TUTORIAL_PAGE_2;
import static me.trashout.fragment.TutorialFragmentItem.TUTORIAL_PAGE_3;
import static me.trashout.fragment.TutorialFragmentItem.TUTORIAL_PAGE_4;
import static me.trashout.fragment.TutorialFragmentItem.TUTORIAL_PAGE_5;

/**
 * Created by Ondrej Hoos on 04.12.2017.
 * Appmine.cz
 */

public class TutorialAdapter extends FragmentPagerAdapter {

    public TutorialAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return TutorialFragmentItem.newInstance(TUTORIAL_PAGE_1);
            case 1:
                return TutorialFragmentItem.newInstance(TUTORIAL_PAGE_2);
            case 2:
                return TutorialFragmentItem.newInstance(TUTORIAL_PAGE_3);
            case 3:
                return TutorialFragmentItem.newInstance(TUTORIAL_PAGE_4);
            case 4:
                return TutorialFragmentItem.newInstance(TUTORIAL_PAGE_5);
            default:
                return TutorialFragmentItem.newInstance(TUTORIAL_PAGE_1);
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
