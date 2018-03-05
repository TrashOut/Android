package me.trashout.activity;

import me.trashout.activity.base.BaseActivity;
import me.trashout.fragment.TutorialFragment;

/**
 * Created by Ondrej Hoos on 04.12.2017.
 * Appmine.cz
 */

public class TutorialActivity extends BaseActivity {

    @Override
    protected String getFragmentName() {
        return TutorialFragment.class.getName();
    }

}
