package com.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersGroup;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersKitStatusCode;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersRecommendation;
import co.leonisand.offers.OffersStampCard;
import co.leonisand.offers.OffersStatus;

import com.demo.offers.MainActivity;

public class OfferDataController {

	/**
	 * request Coupon with Type Search
	 * @param mSearch
	 * @param mListCoupon
	 * @param mainActivity
	 * @param myHandler
	 */
	public void requestCouponsWithType(
			final ArrayList<OffersCoupon> mListCoupon,
			final MainActivity mainActivity, final Handler myHandler) {

		OffersListener listener = new OffersListener() {
			public void onDone(Map<String, Object> map) {
				if (mListCoupon == null) {
					return;
				}
				mListCoupon.clear();
				if (map.get("coupons") != null) {
					@SuppressWarnings("unchecked")
					List<OffersCoupon> items = (List<OffersCoupon>) map
							.get("coupons");
					for (OffersCoupon item : items) {
							mListCoupon.add(item);
					}
					myHandler.sendEmptyMessage(0);
				} else {
					OffersStatus offersstatus = (OffersStatus) map
							.get("status");
					if (offersstatus != null) {
						mainActivity.alert(
								"coupons.onDone",
								offersstatus.getCode() + ":"
										+ offersstatus.getMessage());
					} else {
						mainActivity.alert("coupons.onDone", "取得できませんでした。");
					}
					return;
				}
			}

			public void onFail(Integer s) {
				mainActivity.alert("coupons.onFail", s.toString());
			}
		};
		
		Bundle params = new Bundle();
		params.putString("sort_target", "delivery_from");
		params.putString("sort_direction", "descending");
		params.putString("offset", "0");
		params.putString("limit", "20");
		OffersKit.getInstance().coupons(true, params, listener);

	}

	/**
	 * request Shops with coupons
	 * @param shops
	 * @param mListCoupon
	 * @param mainActivity
	 * @param myHandler
	 */
	public void requestShops(final int[] shops,
			final ArrayList<OffersCoupon> mListCoupon,
			final MainActivity mainActivity, final Handler myHandler) {
		OffersKit.getInstance().coupons(shops, new OffersListener() {

			@Override
			public void onFail(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDone(Map<String, Object> arg0) {
				// TODO Auto-generated method stub
				mListCoupon.clear();
				OffersStatus offersstatus = (OffersStatus) arg0.get("status");
				if (offersstatus.getCode() == OffersKitStatusCode.OffersKitStatusSuccess
						.toInt()) {
					List<OffersCoupon> items = (List<OffersCoupon>) arg0
							.get("coupons");
					for (OffersCoupon item : items) {
						mListCoupon.add(item);
					}
					if (mListCoupon.size() == 0) {
						mainActivity.alert("MSG_ERR_0042",
								"ご利用可能な%@がありません。\n次の%@を楽しみにお待ちください。");
					}
					myHandler.sendEmptyMessage(0);
				}
			}
		});
	}

	/**
	 * request recommend with shops
	 * @param params
	 * @param shops
	 * @param mListRecommendation
	 * @param mainActivity
	 * @param myHandler
	 */
	public void requestShopsRecommend(Bundle params, final String shops,
			final ArrayList<OffersRecommendation> mListRecommendation,
			final MainActivity mainActivity, final Handler myHandler) {
		OffersKit.getInstance().recommendations(true, params,
				new OffersListener() {

					public void onDone(Map<String, Object> map) {

						if (mListRecommendation == null) {
							return;
						}

						mListRecommendation.clear();

						if (map.get("recommendations") != null) {

							@SuppressWarnings("unchecked")
							List<OffersRecommendation> items = (List<OffersRecommendation>) map
									.get("recommendations");
							for (OffersRecommendation item : items) {
								if (item.getGroups().contains(shops))
									mListRecommendation.add(item);
							}
							if (mListRecommendation.size() == 0) {
								mainActivity.alert("MSG_ERR_0042",
										"ご利用可能な%@がありません。\n次の%@を楽しみにお待ちください。");
							}
							myHandler.sendEmptyMessage(0);
						} else {
							OffersStatus offersstatus = (OffersStatus) map
									.get("status");
							if (offersstatus != null) {
								mainActivity.alert("recommendations.onDone",
										offersstatus.getCode() + ":"
												+ offersstatus.getMessage());
							} else {
								mainActivity.alert("recommendations.onDone",
										"取得できませんでした。");
							}
							return;
						}
					}

					public void onFail(Integer s) {
//						mainActivity.alert("onFail", s.toString());
					}

				});
	}

	/**
	 * request StampCard with shops
	 * @param params
	 * @param shops
	 * @param mListStampCard
	 * @param mainActivity
	 * @param myHandler
	 */
	public void requestShopsStampCard(Bundle params, final String shops,
			final ArrayList<OffersStampCard> mListStampCard,
			final MainActivity mainActivity, final Handler myHandler) {
		OffersKit.getInstance().stampCards(true, params, new OffersListener() {

			public void onDone(Map<String, Object> map) {
				Log.e("man1", "" + map);
				if (mListStampCard == null) {
					return;
				}

				mListStampCard.clear();

				if (map.get("stampcards") != null) {

					@SuppressWarnings("unchecked")
					List<OffersStampCard> items = (List<OffersStampCard>) map
							.get("stamp_cards");
					for (OffersStampCard item : items) {
						if (item.getGroups().contains(shops)) {
							mListStampCard.add(item);
						}
					}
					if (mListStampCard.size() == 0) {
						mainActivity.alert("MSG_ERR_0042",
								"ご利用可能な%@がありません。\n次の%@を楽しみにお待ちください。");
					}
					myHandler.sendEmptyMessage(0);
				} else {
					OffersStatus offersstatus = (OffersStatus) map
							.get("status");
					if (offersstatus != null) {
						mainActivity.alert(
								"stampCards.onDone",
								offersstatus.getCode() + ":"
										+ offersstatus.getMessage());
					} else {
						mainActivity.alert("stampCards.onDone", "取得できませんでした。");
					}
					return;
				}
			}

			public void onFail(Integer s) {
//				mainActivity.alert("onFail", s.toString());
			}

		});
	}

	/**
	 * request All Groups
	 * @param mList
	 * @param mainActivity
	 * @param myHandler
	 */
	public void requestGroups(final ArrayList<OffersGroup> mList,
			final MainActivity mainActivity, final Handler myHandler) {
		OffersKit.getInstance().groups(true, new OffersListener() {

			@Override
			public void onFail(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDone(Map<String, Object> arg0) {
				// TODO Auto-generated method stub
				mList.clear();
				List<OffersGroup> items = (List<OffersGroup>) arg0
						.get("groups");
				mList.add(null);
				for (OffersGroup item : items) {
					mList.add(item);
				}
				myHandler.sendEmptyMessage(1);
			}
		});
	}

	/**
	 * reset all data
	 * @param mainActivity
	 */
	public void requestResetData(final MainActivity mainActivity) {
		OffersKit.getInstance().authenticationToken(new OffersListener() {
			@Override
			public void onDone(Map<String, Object> arg0) {
				mainActivity.alert("ユーザーを再作成しました。", "ユーザーを再作成しました。");
				mainActivity.resetData();
			}

			@Override
			public void onFail(Integer arg0) {
				mainActivity.alert("エラー", "ユーザー再作成に失敗しました。");
			}
		});
	}

	/**
	 * requeset get coupon with campaign
	 * @param codes
	 * @param mainActivity
	 */
	public void requestCampaignCoupons(String[] codes, final ArrayList<OffersCoupon> mListCoupon,
			final MainActivity mainActivity, final Handler myHandler) {
		OffersKit.getInstance().campaignCoupons(codes,
				new OffersKit.OffersListener() {

					@Override
					public void onFail(Integer arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDone(Map<String, Object> arg0) {
						// TODO Auto-generated method stub
						OffersStatus offersstatus = (OffersStatus) arg0
								.get("status");
						if (offersstatus.getCode() == OffersKitStatusCode.OffersKitStatusSuccess
								.toInt()) {
							List<OffersCoupon> items = (List<OffersCoupon>) arg0
									.get("coupons");
							for (OffersCoupon item : items) {
									mListCoupon.add(item);
							}
							myHandler.sendEmptyMessage(0);
						} else {
							checkStatusCode(offersstatus.getCode(),
									mainActivity);
						}

					}
				});
	}

	/**
	 * get recommend with bundle
	 * @param params
	 * @param mListRecommendation
	 * @param mainActivity
	 * @param myHandler
	 */
	public void requestRecommendWithBundle(Bundle params,
			final ArrayList<OffersRecommendation> mListRecommendation,
			final MainActivity mainActivity, final Handler myHandler) {
		OffersKit.getInstance().recommendations(true, params,
				new OffersListener() {

					public void onDone(Map<String, Object> map) {

						if (mListRecommendation == null) {
							return;
						}

						mListRecommendation.clear();

						if (map.get("recommendations") != null) {

							@SuppressWarnings("unchecked")
							List<OffersRecommendation> items = (List<OffersRecommendation>) map
									.get("recommendations");
							for (OffersRecommendation item : items) {
								mListRecommendation.add(item);
							}
							myHandler.sendEmptyMessage(0);
						} else {
							OffersStatus offersstatus = (OffersStatus) map
									.get("status");
							if (offersstatus != null) {
								mainActivity.alert("recommendations.onDone",
										offersstatus.getCode() + ":"
												+ offersstatus.getMessage());
							} else {
								mainActivity.alert("recommendations.onDone",
										"取得できませんでした。");
							}
							return;
						}
					}

					public void onFail(Integer s) {
//						mainActivity.alert("onFail", s.toString());
					}

				});
	}
	/**
	 * check status code for show message
	 * @param code
	 * @param mainActivity
	 */
	public void checkStatusCode(int code, MainActivity mainActivity) {

		if (code == OffersKitStatusCode.OffersKitStatusUnavailableCampaign
				.toInt()) {
			mainActivity.alert("MSG_ERR_0028", "このキャンペーンコードは無効になっています。");
		} else if (code == OffersKitStatusCode.OffersKitStatusUsedCampaign
				.toInt()) {
			mainActivity.alert("MSG_ERR_0029", "このキャンペーンコードは既にご利用済みです。");
		} else if (code == OffersKitStatusCode.OffersKitStatusOutOfServiceCampaign
				.toInt()) {
			mainActivity.alert("MSG_ERR_0030",
					"キャンペーンコードを確認できません。\n有効期限内かご確認ください。");
		} else if (code == OffersKitStatusCode.OffersKitStatusCampaignNotFound
				.toInt()) {
			mainActivity.alert("MSG_ERR_0031",
					"キャンペーンコードを確認できません。\nキャンペーンコードに誤りがないかご確認ください。");
		} else if (code == OffersKitStatusCode.OffersKitStatusCampaignCouponRequestLockout
				.toInt()) {
			mainActivity.alert("MSG_ERR_0035",
					"キャンペーンコード入力失敗回数が上限を超えたため、ロックアウトされました。");
		} else if (code == OffersKitStatusCode.OffersKitStatusInvalidatedCampaign
				.toInt()) {
			mainActivity
					.alert("MSG_ERR_0036", "このキャンペーンコードは一時的にご利用できなくなっています。");
		} else if (code == OffersKitStatusCode.OffersKitStatusSuspendedCampaign
				.toInt()) {
			mainActivity.alert("MSG_ERR_0037", "このキャンペーンコードは失効になっています。");
		} else {
			mainActivity.alert("MSG_ERR_0024",
					"システムエラー\n（恐れ入りますが、お問い合せ窓口までご連絡ください）");
		}
	}
}
