package com.demo.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

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
}
