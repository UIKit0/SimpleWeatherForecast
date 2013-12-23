package fr.tvbarthel.apps.simpleweatherforcast;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

import fr.tvbarthel.apps.simpleweatherforcast.fragments.ForecastFragment;
import fr.tvbarthel.apps.simpleweatherforcast.utils.ColorUtils;

public class MainActivity extends ActionBarActivity {

	ForecastPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(130, 0, 0, 0)));

		mSectionsPagerAdapter = new ForecastPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setPageTransformer(true, new ForecastPageTransformer());
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {
				final GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
						new int[]{getColor(i, v), getColor(i, (float) Math.sqrt(v))});
				getWindow().setBackgroundDrawable(g);
			}

			@Override
			public void onPageSelected(int i) {

			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});
	}

	private int getColor(int currentPosition, float positionOffset) {
		//retrieve current color and next color relative to the current position.
		final int colorSize = ColorUtils.COLORS.length - 1;
		final int[] currentColor = ColorUtils.COLORS[(currentPosition) % colorSize];
		final int[] nextColor = ColorUtils.COLORS[(currentPosition + 1) % colorSize];

		//Compute the deltas relative to the current position offset.
		final int deltaR = (int) ((nextColor[0] - currentColor[0]) * positionOffset);
		final int deltaG = (int) ((nextColor[1] - currentColor[1]) * positionOffset);
		final int deltaB = (int) ((nextColor[2] - currentColor[2]) * positionOffset);

		return Color.argb(255, currentColor[0] + deltaR, currentColor[1] + deltaG, currentColor[2] + deltaB);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public class ForecastPagerAdapter extends FragmentPagerAdapter {

		public ForecastPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ForecastFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			// Show 14 total pages.
			return 14;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return ("Section " + String.valueOf(position)).toUpperCase(Locale.getDefault());
		}
	}

	public class ForecastPageTransformer implements ViewPager.PageTransformer {

		public void transformPage(View view, float position) {
			final int pageWidth = view.getWidth();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				if (position < -2.0) { // [-Infinity,-2.0)
					// This page is way off-screen to the left.
					view.setAlpha(0);

				} else if (position <= 2.0) { // [-2.0,2.0]
					final float normalizedPosition = Math.abs(position / 2);
					final float scaleFactor = 1f - normalizedPosition;
					final float horizontalMargin = pageWidth * normalizedPosition;

					//Translate back the page.
					if (position < 0) {
						//left
						view.setTranslationX(horizontalMargin);
					} else {
						//right
						view.setTranslationX(-horizontalMargin);
					}

					// Scale the page down relative to its size.
					view.setScaleX((float) Math.pow(scaleFactor, 0.80));
					view.setScaleY((float) Math.pow(scaleFactor, 0.80));

					// Fade the page relative to its size.
					view.setAlpha(1f * scaleFactor - (scaleFactor * 0.25f));

				} else { // (2.0,+Infinity]
					// This page is way off-screen to the right.
					view.setAlpha(0);
				}
			}

		}
	}

}
