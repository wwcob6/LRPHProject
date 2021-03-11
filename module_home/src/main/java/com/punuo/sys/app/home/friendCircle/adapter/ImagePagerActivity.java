package com.punuo.sys.app.home.friendCircle.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipImageShareRequest;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.StatusBarUtil;

import java.util.ArrayList;

/**
 * 图片查看器
 */
@Route(path = HomeRouter.ROUTER_IMAGE_PAGER_ACTIVITY)
public class ImagePagerActivity extends BaseSwipeBackActivity {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private ViewPager mPager;
	private TextView indicator;

	@Autowired(name = EXTRA_IMAGE_URLS)
	ArrayList<String> urls;

	@Autowired(name = EXTRA_IMAGE_INDEX)
	int pagerPosition;

	private int selectedPosition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);
		StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, false);
		ARouter.getInstance().inject(this);
		mPager = (ViewPager) findViewById(R.id.pager);

		ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				CharSequence text = getString(R.string.viewpager_indicator, position + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
				selectedPosition = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
		selectedPosition = pagerPosition;
		findViewById(R.id.share).setOnClickListener(v -> {
			String url = urls.get(selectedPosition);
			SipImageShareRequest request = new SipImageShareRequest(url);
			SipUserManager.getInstance().addRequest(request);
		});
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private static class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ArrayList<String> fileList;

		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			return ImageDetailFragment.newInstance(url);
		}

	}
}
