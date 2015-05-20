package com.example.offerssample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import co.leonisand.offers.OffersKit;
import co.leonisand.platform.Leonis;

public class MainActivity extends ActionBarActivity{

	private ActionBar mActionBar;
	private Context mContext;

	private OnBackStackChangedListener mOnBackStackChangedListener;

	public ProgressBar progressBar;
	private FragmentTabHost mFragmentTabHost;
	private OnTouchListener mTabTouchListener;
	private Intent mIntent;

	public boolean onCreateOptionsMenu(Menu menu){
		menu.clear();
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem){
		boolean res = true;
		switch(menuitem.getItemId()){
		case android.R.id.home: 
			if(getSupportFragmentManager().getBackStackEntryCount() > 0){
				getSupportFragmentManager().popBackStack();
			} else {
			}
			break;
		default:
			res = super.onOptionsItemSelected(menuitem);
			break;
		}
		return res;
	}


	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);

		onNewIntent(getIntent());

		mContext = this.getApplicationContext();

		// SDKの初期化
		// Leonisの初期化
		// UID生成をSDKにまかせるとき
		Leonis.getInstance().init(getApplicationContext(), true);
		/*
		// UIDをアプリから指定するとき
		Leonis.getInstance().init(getApplicationContext(), "testuser", true);
		 */
		Leonis.getInstance().setRequestTimeoutInterval(10);
		System.out.println(Leonis.getInstance().uid());

		Leonis.getInstance().setEndUserExtension(
				"target_list", "pointcard_number", "1234567890",
				new Leonis.LeonisListener() {
					public void onDone(Map<String, Object> result) {
						System.out.println("extension ok");
					}
					public void onFail(Integer result) {
						System.out.println("extension ng");
					}
				}
				);

		JSONObject jsonobject = new JSONObject();
		try{
			JSONArray jsonarray = new JSONArray();
			jsonarray.put("key1");
			jsonarray.put("key2");
			jsonarray.put("key3");
			jsonobject.put("resouce1", jsonarray);
			jsonobject.put("resouce2", "key1");
		}catch(JSONException jsonexception){
			jsonexception.printStackTrace();
		}
		Leonis.getInstance().addEndUserExtensions(jsonobject, new Leonis.LeonisListener() {
			public void onDone(Map<String, Object> map){
			}
			public void onFail(Integer s){
			}
		});

		// OffersKitの初期化
		OffersKit.getInstance().init(getApplicationContext());

		OffersKit.getInstance().setRequestTimeoutInterval(10);

		Bundle bundle1 = new Bundle();
		bundle1.putString("test", "TESTID");
		bundle1.putString("info", "INFOUSERID");
		OffersKit.getInstance().externalServiceRegistrations(bundle1, null);

		setContentView(R.layout.main_activity);

		progressBar = (ProgressBar) findViewById(R.id.progressbar);

		mOnBackStackChangedListener = new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
			public void onBackStackChanged(){
				if(getSupportFragmentManager().getBackStackEntryCount() > 0){
					mActionBar.setHomeAsUpIndicator(R.drawable.ic_transparent);
				}else{
					mActionBar.setHomeAsUpIndicator(R.drawable.ic_transparent);
				}
				
				if(getSupportFragmentManager().getFragments() == null){
					return;
				}
				Fragment fragment = (Fragment)getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount());
				if(fragment.isVisible()){
					fragment.onResume();
				}
			}
		};

		getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setHomeAsUpIndicator(R.drawable.ic_transparent);

		mFragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mFragmentTabHost.setup(mContext, getSupportFragmentManager(), R.id.container);

		mTabTouchListener = new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(getSupportFragmentManager().getBackStackEntryCount() > 0){
					getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
				}
				v.performClick();
				return false;
			}
		};

		mFragmentTabHost.setOnTabChangedListener(new OnTabChangeListener(){
			@Override
			public void onTabChanged(String tabId) {
				
				for(int i=0; i<mFragmentTabHost.getTabWidget().getChildCount(); i++){
					View tabIndicator = mFragmentTabHost.getTabWidget().getChildAt(i);
					
					if(((TextView) tabIndicator.findViewById(R.id.title)).getText().equals(tabId)){
						((TextView) tabIndicator.findViewById(R.id.title)).setTextColor(Color.rgb(51, 181, 229)); //android.R.color.holo_blue_light
						((ImageView) tabIndicator.findViewById(R.id.icon)).setColorFilter(Color.rgb(51, 181, 229), PorterDuff.Mode.SRC_IN);
					}else{
						((TextView) tabIndicator.findViewById(R.id.title)).setTextColor(getResources().getColor(android.R.color.secondary_text_light_nodisable));
						((ImageView) tabIndicator.findViewById(R.id.icon)).setColorFilter(getResources().getColor(android.R.color.secondary_text_light_nodisable), PorterDuff.Mode.SRC_IN);
					}
					

					setTabBadge(CouponsFragment.TITLE, OffersKit.getInstance().unreadCouponsCount());
					setTabBadge(RecommendationsFragment.TITLE, OffersKit.getInstance().unreadRecommendationsCount());
					setTabBadge(OffersFragment.TITLE, OffersKit.getInstance().unreadOffersCount());
				}
			}
		});
		
		TabSpec tabSpecCoupons = mFragmentTabHost.newTabSpec(CouponsFragment.TITLE);
		View tabIndicatorCoupons = LayoutInflater.from(mContext).inflate(R.layout.tab_indicator, mFragmentTabHost.getTabWidget(), false);
		((TextView) tabIndicatorCoupons.findViewById(R.id.title)).setText(CouponsFragment.TITLE);
		((ImageView) tabIndicatorCoupons.findViewById(R.id.icon)).setImageResource(R.drawable.coupons);
		//((ImageView) tabIndicatorCoupons.findViewById(R.id.icon)).setColorFilter(0xcc0000ff, PorterDuff.Mode.SRC_IN);
		tabSpecCoupons.setIndicator(tabIndicatorCoupons);
		mFragmentTabHost.addTab(tabSpecCoupons, CouponsFragment.class, null);
		mFragmentTabHost.getTabWidget().getChildTabViewAt(0).setOnTouchListener(mTabTouchListener);

		TabSpec tabSpecRecommendations = mFragmentTabHost.newTabSpec(RecommendationsFragment.TITLE);
		View tabIndicatorRecommendations = LayoutInflater.from(mContext).inflate(R.layout.tab_indicator, mFragmentTabHost.getTabWidget(), false);
		((TextView) tabIndicatorRecommendations.findViewById(R.id.title)).setText(RecommendationsFragment.TITLE);
		((ImageView) tabIndicatorRecommendations.findViewById(R.id.icon)).setImageResource(R.drawable.recommendations);
		tabSpecRecommendations.setIndicator(tabIndicatorRecommendations);
		mFragmentTabHost.addTab(tabSpecRecommendations, RecommendationsFragment.class, null);
		mFragmentTabHost.getTabWidget().getChildTabViewAt(1).setOnTouchListener(mTabTouchListener);

		TabSpec tabSpecStampCards = mFragmentTabHost.newTabSpec(StampCardsFragment.TITLE);
		View tabIndicatorStampCards = LayoutInflater.from(mContext).inflate(R.layout.tab_indicator, mFragmentTabHost.getTabWidget(), false);
		((TextView) tabIndicatorStampCards.findViewById(R.id.title)).setText(StampCardsFragment.TITLE);
		((ImageView) tabIndicatorStampCards.findViewById(R.id.icon)).setImageResource(R.drawable.stamp_cards);
		tabSpecStampCards.setIndicator(tabIndicatorStampCards);
		mFragmentTabHost.addTab(tabSpecStampCards, StampCardsFragment.class, null);
		mFragmentTabHost.getTabWidget().getChildTabViewAt(2).setOnTouchListener(mTabTouchListener);

		TabSpec tabSpecOffers = mFragmentTabHost.newTabSpec(OffersFragment.TITLE);
		View tabIndicatorOffers = LayoutInflater.from(mContext).inflate(R.layout.tab_indicator, mFragmentTabHost.getTabWidget(), false);
		((TextView) tabIndicatorOffers.findViewById(R.id.title)).setText(OffersFragment.TITLE);
		((ImageView) tabIndicatorOffers.findViewById(R.id.icon)).setImageResource(R.drawable.offers);
		tabSpecOffers.setIndicator(tabIndicatorOffers);
		mFragmentTabHost.addTab(tabSpecOffers, OffersFragment.class, null);
		mFragmentTabHost.getTabWidget().getChildTabViewAt(3).setOnTouchListener(mTabTouchListener);

		TabSpec tabSpecOther = mFragmentTabHost.newTabSpec(OtherFragment.TITLE);
		View tabIndicatorOther = LayoutInflater.from(mContext).inflate(R.layout.tab_indicator, mFragmentTabHost.getTabWidget(), false);
		((TextView) tabIndicatorOther.findViewById(R.id.title)).setText(OtherFragment.TITLE);
		((ImageView) tabIndicatorOther.findViewById(R.id.icon)).setImageResource(R.drawable.menu);
		tabSpecOther.setIndicator(tabIndicatorOther);
		mFragmentTabHost.addTab(tabSpecOther, OtherFragment.class, null);
		mFragmentTabHost.getTabWidget().getChildTabViewAt(4).setOnTouchListener(mTabTouchListener);

		mFragmentTabHost.onTabChanged(CouponsFragment.TITLE);
		
	}

	@Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);

		mIntent = intent;

	}

	public void onStart(){
		super.onStart();


		if(mIntent != null && mIntent.getAction().equals("android.intent.action.VIEW")){
			Uri uri = mIntent.getData();
			if(uri != null){
				// スタック除去
				if(getSupportFragmentManager().getBackStackEntryCount() > 0){
					getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
				}
				mFragmentTabHost.setCurrentTab(0);
			}
		}
	}
	
	public void onResme(){
		super.onResume();
	}

	public void onDestroy(){
		getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
		super.onDestroy();
	}

	public void onFragmentStart(Fragment fragment){
		
		if(mIntent != null && mIntent.getAction().equals("android.intent.action.VIEW")){
			Uri uri = mIntent.getData();
			if(uri != null){
				
				mIntent = null;
				
				String as[] = uri.toString().split("/");
				PreviewCouponFragment previewcouponfragment = new PreviewCouponFragment();
				Bundle bundle2 = new Bundle();
				bundle2.putString("previewKey", as[-1 + as.length]);
				previewcouponfragment.setArguments(bundle2);

				getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.container, previewcouponfragment, PreviewCouponFragment.TITLE)
				.addToBackStack(null)
				.commit();
			}
		}
		
	}
	public void setTabBadge(String tabTag, int count){
		
		for(int i=0; i<mFragmentTabHost.getTabWidget().getChildCount(); i++){
			View tabIndicator = mFragmentTabHost.getTabWidget().getChildTabViewAt(i);
			
			if(((TextView)tabIndicator.findViewById(R.id.title)).getText().equals(tabTag)){
				((TextView)tabIndicator.findViewById(R.id.badge)).setText(String.valueOf(count));
				if(count > 0){
					((TextView)tabIndicator.findViewById(R.id.badge)).setVisibility(View.VISIBLE);
				}else{
					((TextView)tabIndicator.findViewById(R.id.badge)).setVisibility(View.GONE);
				}
			}
		}
	}
	
	public class CommonDialogFragment extends DialogFragment {

		public static final String FIELD_LAYOUT = "layout";
		public static final String FIELD_TITLE = "title";
		public static final String FIELD_MESSAGE = "message";
		public static final String FIELD_LIST_ITEMS = "list_items";
		public static final String FIELD_LIST_ITEMS_STRING = "list_items_string";
		public static final String FIELD_LABEL_POSITIVE = "label_positive";
		public static final String FIELD_LABEL_NEGATIVE = "label_negative";
		public static final String FIELD_LABEL_NEUTRAL = "label_neutral";

		private DialogInterface.OnShowListener mListenerShow;
		private DialogInterface.OnClickListener mListenerNegativeClick;
		private DialogInterface.OnClickListener mListenerPositiveClick;
		private DialogInterface.OnClickListener mListenerNeutralClick;

		private View mView;

		private AlertDialog mAlertDialog;

		/** ダイアログの共通タグ */
		private static final String TAG = "exclusive_dialog";

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			Bundle args = getArguments();

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			// dialog title
			if (args.containsKey(FIELD_TITLE)) {
				builder.setTitle(args.getString(FIELD_TITLE));
			}

			// dialog message
			if (args.containsKey(FIELD_MESSAGE)) {
				builder.setMessage(args.getString(FIELD_MESSAGE));
			}

			// dialog customize content view
			if (args.containsKey(FIELD_LAYOUT)) {
				builder.setView(mView);
			}

			// positive button title and click listener
			if (args.containsKey(FIELD_LABEL_POSITIVE)) {
				builder.setPositiveButton(args.getString(FIELD_LABEL_POSITIVE), mListenerPositiveClick);
			}

			// negative button title and click listener
			if (args.containsKey(FIELD_LABEL_NEGATIVE)) {
				builder.setNegativeButton(args.getString(FIELD_LABEL_NEGATIVE), mListenerNegativeClick);
			}

			// neutral button title and click listener
			if (args.containsKey(FIELD_LABEL_NEUTRAL)) {
				builder.setNeutralButton(args.getString(FIELD_LABEL_NEUTRAL), mListenerNeutralClick);
			}

			// make dialog
			mAlertDialog = builder.create();

			// show listener
			if (mListenerShow != null) {
				mAlertDialog.setOnShowListener(mListenerShow);
			}

			return mAlertDialog;
		}

		public void setView(View view){
			mView = view;
		}
		public void setShowListener(DialogInterface.OnShowListener listener){
			mListenerShow = listener;
		}
		public void setPotitiveClickListener(DialogInterface.OnClickListener listener){
			mListenerPositiveClick = listener;
		}
		public void setNegativeClickListener(DialogInterface.OnClickListener listener){
			mListenerNegativeClick = listener;
		}
		public void setNeutralClickListener(DialogInterface.OnClickListener listener){
			mListenerNeutralClick = listener;
		}

		@Override
		public final void show(final FragmentManager manager, final String tag) {
			deleteDialogFragment(manager);
			super.show(manager, TAG);
		}
		private void deleteDialogFragment(final FragmentManager fragmentManager) {
			CommonDialogFragment prev = (CommonDialogFragment) fragmentManager.findFragmentByTag(TAG);
			if (prev == null) {
				return;
			}
			Dialog dialog = prev.getDialog();
			if (dialog == null) {
				return;
			}
			if (!dialog.isShowing()) {
				return;
			}
			prev.dismiss();
		}
	}

	public DialogFragment alert(String title, String message){

		Bundle args = new Bundle();
		args.putString(CommonDialogFragment.FIELD_TITLE, title);
		args.putString(CommonDialogFragment.FIELD_MESSAGE, message);
		args.putString(CommonDialogFragment.FIELD_LABEL_POSITIVE, getString(android.R.string.ok));
		CommonDialogFragment dialogFragment = new CommonDialogFragment(){

		};
		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "alert");

		return dialogFragment;
	}

	public DialogFragment viewConfirm(String title, View view, String positive_title, DialogInterface.OnClickListener positive_listener, String negative_title, DialogInterface.OnClickListener negative_listener, OnShowListener show_listener) {

		Bundle args = new Bundle();
		args.putString(CommonDialogFragment.FIELD_TITLE, title);
		args.putInt(CommonDialogFragment.FIELD_LAYOUT, view.getId());
		args.putString(CommonDialogFragment.FIELD_LABEL_POSITIVE, positive_title);
		args.putString(CommonDialogFragment.FIELD_LABEL_NEGATIVE, negative_title);


		CommonDialogFragment dialogFragment = new CommonDialogFragment();
		dialogFragment.setView(view);

		dialogFragment.setPotitiveClickListener(positive_listener);
		dialogFragment.setNegativeClickListener(negative_listener);

		dialogFragment.setShowListener(show_listener);

		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "viewConfirm");

		return dialogFragment;
	}

	public DialogFragment confirm(String title, String message, String positive_title, android.content.DialogInterface.OnClickListener positive_listener, String negative_title, DialogInterface.OnClickListener negative_listener) {

		Bundle args = new Bundle();
		args.putString(CommonDialogFragment.FIELD_TITLE, title);
		args.putString(CommonDialogFragment.FIELD_MESSAGE, message);
		args.putString(CommonDialogFragment.FIELD_LABEL_POSITIVE, positive_title);
		args.putString(CommonDialogFragment.FIELD_LABEL_NEGATIVE, negative_title);

		CommonDialogFragment dialogFragment = new CommonDialogFragment();
		dialogFragment.setPotitiveClickListener(positive_listener);
		dialogFragment.setNegativeClickListener(negative_listener);

		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "confirm");

		return dialogFragment;
	}

}
