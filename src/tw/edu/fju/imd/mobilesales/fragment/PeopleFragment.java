package tw.edu.fju.imd.mobilesales.fragment;

import com.parse.FindCallback;

import tw.edu.fju.imd.mobilesales.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PeopleFragment extends Fragment {
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	private ViewPager mViewPager;
	private PagerTabStrip mPagerTab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.people_layout_viewpager, container, false);
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getChildFragmentManager());

		mViewPager = (ViewPager) v.findViewById(R.id.people_viewpager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);

		mPagerTab = (PagerTabStrip) v.findViewById(R.id.pagerTab);
		mPagerTab.setTabIndicatorColorResource(R.color.aquamarine);

		return v;
	}

	public static class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

		final static String[] TABTITLE = { "通訊錄", "群組" };

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			Log.d("debug", "AppSectionsPagerAdapter");
		}

		@Override
		public Fragment getItem(int i) {
			Log.d("debug", "getItem" + i);
			switch (i) {
			case 0:
				return new PeopleListFragment();

			case 1:
				return new People_tag();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TABTITLE[position];
		}
	}

}
