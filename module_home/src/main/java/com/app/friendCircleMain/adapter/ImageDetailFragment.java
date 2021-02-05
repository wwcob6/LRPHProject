package com.app.friendCircleMain.adapter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.R;
import com.app.friendCircleMain.photoview.PhotoViewAttacher;
import com.app.friendCircleMain.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.punuo.sys.sdk.PnApplication;


/**
 * 单张图片显示Fragment
 */
public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});

		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		progressBar.setVisibility(View.VISIBLE);
		Glide.with(PnApplication.getInstance()).load(mImageUrl).listener(new RequestListener<Drawable>() {
			@Override
			public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
				progressBar.setVisibility(View.GONE);
				return false;
			}

			@Override
			public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
				return false;
			}
		}).into(mImageView);
	}
}
