package com.demo.android;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import co.leonisand.cupido.CupidoAttachment;
import co.leonisand.cupido.CupidoKit;
import co.leonisand.cupido.CupidoKit.CupidoListener;
import co.leonisand.cupido.CupidoNotification;
import co.leonisand.cupido.CupidoStatus;

public class NotificationActivity extends ActionBarActivity {
	private int id;
	private ProgressBar progressBar;
	private ArrayAdapter<String[]> listAdapter;
	private Context context;
	private CupidoNotification notification;
	private LinearLayout mNotificationDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.in, R.anim.out);
		setContentView(R.layout.activity_notification_detail);
		mNotificationDetail = (LinearLayout) findViewById(R.id.linear_notification_detail);
		context = this.getApplicationContext();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.actionbar)));
		Intent intent = getIntent();
		id = intent.getIntExtra("id", 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final String response_type = (intent.getStringExtra("response_type") != null) ? intent
				.getStringExtra("response_type") : "view";

		Bundle bundle = new Bundle();
		bundle.putString("response_type", "view");
		CupidoNotification.responseType(id, bundle, new CupidoListener() {
			@Override
			public void onDone(Map<String, Object> result) {
				CupidoStatus status = (CupidoStatus) result.get("status");
//				Toast.makeText(
//						context,
//						"response_type:" + response_type + ";code:"
//								+ status.getCode() + ";message:"
//								+ status.getMessage(), Toast.LENGTH_LONG)
//						.show();
			}

			@Override
			public void onFail(String message) {
//				Toast.makeText(context,
//						"response_type:" + response_type + "error:" + message,
//						Toast.LENGTH_LONG).show();
			}
		});

		progressBar = new ProgressBar(context, null,
				android.R.attr.progressBarStyle);
		progressBar.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {
			{
				gravity = Gravity.CENTER;
			}
		});
		progressBar.setVisibility(View.GONE);

		final TextView mTitle = (TextView) findViewById(R.id.tv_title);
		final TextView mPayLoad = (TextView) findViewById(R.id.tv_push_message);
		final ImageView mImageView = (ImageView) findViewById(R.id.img_icon);
		final TextView mBody = (TextView) findViewById(R.id.tv_body);

		// Log.e("man", "" + bitmap);
		// mImageView.setImageBitmap(bitmap);
		CupidoKit.getInstance(this).notification(id, new CupidoListener() {
			@Override
			public void onDone(Map<String, Object> result) {
				// progressBar.setVisibility(View.GONE);
				Log.e("cupido status notification", "" + result);
				CupidoStatus status = (CupidoStatus) result.get("status");
				if (result.get("notification") != null) {

					notification = (CupidoNotification) result
							.get("notification");

					mTitle.setText(notification.getTitle());
					mPayLoad.setText(notification.getPayload());
//					notification.icon().getImageListener(mImageView,
//							new ImageListener() {
//
//								@Override
//								public void onDone(View view, Bitmap bitmap) {
//									// TODO Auto-generated method stub
//									((ImageView) view).setImageBitmap(bitmap);
//								}
//							});
					mBody.setText(notification.getBody());
					// attatchments
					List<CupidoAttachment> attachments = notification
							.getAttachmentsList();
					Log.e("so luong attachments ", "" + attachments.size());
					for (CupidoAttachment attatchment : attachments) {
						LayoutInflater inflater = getLayoutInflater();
						View view = inflater.inflate(
								R.layout.layout_attached_view, null);
						ImageView image = (ImageView) view
								.findViewById(R.id.img_attached);
						new DownloadImageTask(image).execute(attatchment
								.getUrl());
						
						mNotificationDetail.addView(view);

					}

				} else {
					Toast.makeText(
							context,
							"notification#code:" + status.getCode()
									+ " ;message:" + status.getMessage(),
							Toast.LENGTH_LONG).show();
					System.out.println("notification#code:" + status.getCode()
							+ " ;message:" + status.getMessage());

				}

			}

			@Override
			public void onFail(String result) {
				Toast.makeText(context, "notification#error:" + result,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	String html = "";
	RelativeLayout.LayoutParams params ;
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				if (myBitmap == null) {
					html = "<a href='" + urls[0] + "'>" + urls[0] + "</a>";
				}
				return myBitmap;
			} catch (Exception e) {
				e.printStackTrace(); 
				return null;
			}
		}

		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				WebView mWebView = new WebView(NotificationActivity.this);
				mWebView.setId(0X100);
				params = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				mWebView.setLayoutParams(params);
				String mimeType = "text/html";
				String encoding = "UTF-8";
				mWebView.requestFocusFromTouch();
				mWebView.getSettings().setJavaScriptEnabled(true);
				
				mWebView.loadDataWithBaseURL("", html, mimeType, encoding, "");
				mNotificationDetail.addView(mWebView);
			}
			bmImage.setImageBitmap(result);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// progressBar.setVisibility(View.VISIBLE);
	}
	@Override
	public void onBackPressed() {
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}
