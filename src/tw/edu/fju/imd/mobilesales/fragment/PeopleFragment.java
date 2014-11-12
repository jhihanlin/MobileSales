package tw.edu.fju.imd.mobilesales.fragment;

import tw.edu.fju.imd.mobilesales.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PeopleFragment extends Fragment implements ActionBar.TabListener {
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.people_layout_viewpager, container, false);
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getActivity().getSupportFragmentManager());

		mViewPager = (ViewPager) v.findViewById(R.id.people_viewpager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		return v;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new People();

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
			return "Section " + (position + 1);
		}
	}
}
