package com.example.offerssample;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersStampAction;
import co.leonisand.offers.OffersStampCard;
import co.leonisand.offers.OffersStampCardView;
import co.leonisand.offers.OffersStatus;


public class StampCardFragment  extends Fragment {

	public class DynamicImageView extends ImageView {

		public DynamicImageView(final Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
			final Drawable d = this.getDrawable();
			if (d != null) {
				final int width = MeasureSpec.getSize(widthMeasureSpec);
				final int height = (int) Math.ceil(width
						* (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
				this.setMeasuredDimension(width, height);
			} else {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}
		}
	}

	public static String TITLE = "スタンプカード";
	private Context mContext;
	private DynamicImageView mImageView;
	private FrameLayout mFrameLayout;
	private ProgressBar mProgressBar;
	private OffersStampCard mStampCard;
	private int mStamp_card_id;
	private OffersStampCardView mStampView;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setHasOptionsMenu(true);

		mStamp_card_id = getArguments().getInt("stamp_card_id");

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menu.clear();
		menuinflater.inflate(R.menu.main_stamp_card, menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		boolean flag = true;

		switch (menuitem.getItemId()) {
		case R.id.action_refresh:
			onStart();
			break;
		case R.id.action_unread:

			((MainActivity) getActivity()).confirm("プレゼント", "未取得のプレゼントを取得しますか？", getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					getPresent();
				}
			}, getString(android.R.string.cancel), null);

			break;

		default:
			flag = super.onOptionsItemSelected(menuitem);
			break;
		}

		return flag;

	}

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {

		mContext = layoutinflater.getContext();

		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		mFrameLayout = new FrameLayout(mContext);
		mFrameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mImageView = new DynamicImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mImageView.setScaleType(android.widget.ImageView.ScaleType.FIT_START);
		mFrameLayout.addView(mImageView);

		mStampView = new OffersStampCardView(mContext);

		mStampView.setOffersStampCardViewListener(new OffersStampCardView.OffersStampCardViewListener(){
			@Override
			public void matchStamp(Map<String, Object> release) {
				Vibrator vib = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
				vib.vibrate(100);
			}

			@Override
			public void releaseStamp(List<Map<String, Object>> result) {
				System.out.println(result);

			}

			@Override
			public void beforeStamp(Map<String, Object> result) {

			}

			@Override
			public void afterStamp(Map<String, Object> result) {

				int stamp_id = Integer.parseInt(result.get("stamp_id").toString());
				int x = (int)Float.parseFloat(result.get("x").toString());
				int y = (int)Float.parseFloat(result.get("y").toString());
				int width = (int)Float.parseFloat(result.get("width").toString());
				int height = (int)Float.parseFloat(result.get("height").toString());
				int angle =  (int)Float.parseFloat(result.get("angle").toString());
				int grid_id = (int)Float.parseFloat(result.get("grid_id").toString());

				mProgressBar.setVisibility(View.VISIBLE);

				mStampCard.stamp(grid_id, x, y, width, height, angle, stamp_id, new OffersListener(){
					@Override
					public void onDone(Map<String, Object> result) {

						mProgressBar.setVisibility(View.GONE);

						mStampCard = (OffersStampCard) result.get("stamp_card");

						OffersStatus status = (OffersStatus)result.get("status");

						setStampCard();

						final List<OffersStampAction> actions = (List<OffersStampAction>) result.get("actions");

						if(actions.size() > 0){
							//プレゼント取得
							boolean hasPresent = false;
							
							for(OffersStampAction action : actions){
								
								if(action.hasPresents()){
									hasPresent = true;
								}
								
							}
							
							
							if(hasPresent){

								((MainActivity)mContext).confirm(actions.get(0).getTitle(), actions.get(0).getMessage()+"\n今すぐプレゼントを取得しますか？", getString(android.R.string.ok), new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
										getPresent();
									}
								}, getString(android.R.string.cancel), null);

							}else{
								((MainActivity)mContext).alert(actions.get(0).getTitle(), actions.get(0).getMessage());
							}

						}
					}

					@Override
					public void onFail(Integer result) {
						mProgressBar.setVisibility(View.GONE);

						((MainActivity) mContext).alert("onFail", "stamp");

						//再描画
						setStampCard();
					}
				});

			}
			@Override
			public void drawStamp(Map<String, Object> arg0, Canvas arg1) {
			}
		});

		mFrameLayout.addView(mStampView);

		linearLayout.addView(mFrameLayout);

		return view;
	}

	public void onStart() {
		super.onStart();

		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().stampCard(mStamp_card_id, true, new OffersListener() {

			public void onDone(Map<String, Object> map) {
				mProgressBar.setVisibility(View.GONE);

				OffersStatus offersstatus = (OffersStatus) map.get("status");
				if(offersstatus != null) {
					if(offersstatus.getCode() != 0){
						((MainActivity) getActivity()).alert("stampCard.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
					}
				}

				if (map.get("stamp_card") != null) {
					mStampCard = (OffersStampCard) map.get("stamp_card");
					if(mStampCard.hasDetail()){
						setStampCard();
					}else{
						((MainActivity) getActivity()).alert("onDone","オフラインデータなし");
					}
				}
			}

			public void onFail(Integer s) {

				mProgressBar.setVisibility(View.GONE);

				((MainActivity) getActivity()).alert("stampCard.onFail", s.toString());
			}

		});
	}


	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
		getActivity().supportInvalidateOptionsMenu();

	}
	
	public void onDestroyView() {
		mStampView.setStampViewListener(null);
		super.onDestroyView();
	}
	
	private void getPresent(){
		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().stampCardPresents(mStamp_card_id, new OffersListener(){
			@Override
			public void onDone(Map<String, Object> result) {
				mProgressBar.setVisibility(View.GONE);

				OffersStatus status = (OffersStatus) result.get("status");

				if(status.getCode() == 0){
					List<OffersCoupon> coupons = (List<OffersCoupon>) ((Map<String, Object>) result.get("presents")).get("coupons");

					((MainActivity) mContext).alert("プレゼント取得", "クーポンを「"+coupons.size()+"」件取得しました。");

				}else{
					((MainActivity) mContext).alert("present", status.getCode()+":"+status.getMessage() );
				}
			}

			@Override
			public void onFail(Integer result) {
				mProgressBar.setVisibility(View.GONE);

				//オフライン取得
				((MainActivity) mContext).alert("onFail", "オンラインになったら再度取得して下さい。" );

			}
		});
	}
	private void setStampCard(){

		List<Map<String, Object>> json_grids = (mStampCard.getGrids() != null) ? mStampCard.getGrids() : null;
		List<Map<String, Object>> json_groups = (mStampCard.getStampGroups() != null) ? mStampCard.getStampGroups() : null;
		List<Map<String, Object>> json_histories = (mStampCard.getHistories() != null) ? mStampCard.getHistories() : null;

		mStampView.setCard(json_grids, mStampCard.isKeepOrder(), json_groups, json_histories);

		mStampCard.backgroundImageBitmap(mImageView, new ImageListener() {
			public void onDone(View view, Bitmap bitmap) {
				((ImageView) view).setImageBitmap(bitmap);
			}
		});

	}

	
}
