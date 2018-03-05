package me.trashout.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.trashout.R;
import me.trashout.adapter.TutorialAdapter;
import me.trashout.fragment.base.BaseFragment;

/**
 * Created by Ondrej Hoos on 04.12.2017.
 * Appmine.cz
 */

public class TutorialFragment extends BaseFragment {

    private static final int INDEX_OF_LAST_PAGE = 4;

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.viewpager_indicator)
    CirclePageIndicator viewpagerIndicator;
    @BindView(R.id.btn_skip)
    TextView btnSkip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        pager.setAdapter(new TutorialAdapter(getActivity().getSupportFragmentManager()));
        viewpagerIndicator.setViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                btnSkip.setVisibility(position == INDEX_OF_LAST_PAGE ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.btn_skip)
    public void clickOnSkipBtn(){
        pager.setCurrentItem(INDEX_OF_LAST_PAGE);
    }

}
