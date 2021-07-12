package com.beanclasses;

import com.beanclasses.NotificationBean;

import java.util.ArrayList;

/**
 * 
 * first level item
 * 
 */
public class NotifyNetwork {

	private String pNetworkCode;

	private ArrayList<NotifyStation> mNotifyStationList;

	public NotifyNetwork(String pNetworkCode, ArrayList<NotifyStation> mNotifyStationList) {
		super();
		this.pNetworkCode = pNetworkCode;
		this.mNotifyStationList = mNotifyStationList;
	}

	public String getpNetworkCode() {
		return pNetworkCode;
	}

	public void setpNetworkCode(String pNetworkCode) {
		this.pNetworkCode = pNetworkCode;
	}

	public ArrayList<NotifyStation> getmNotifyStationList() {
		return mNotifyStationList;
	}

	public void setmNotifyStationList(ArrayList<NotifyStation> mNotifyStationList) {
		this.mNotifyStationList = mNotifyStationList;
	}

	/**
	 * 
	 * second level item
	 * 
	 */

	public static class NotifyStation {

		private String pStationName;
		private ArrayList<NotificationBean> mNotificationListArray;

		public NotifyStation(String pStationName,
				ArrayList<NotificationBean> mNotificationListArray) {
			super();
			this.pStationName = pStationName;
			this.mNotificationListArray = mNotificationListArray;
		}

		public String getpStationName() {
			return pStationName;
		}

		public void setpStationName(String pStationName) {
			this.pStationName = pStationName;
		}

		public ArrayList<NotificationBean> getmNotificationListArray() {
			return mNotificationListArray;
		}

		public void setmNotificationListArray(ArrayList<NotificationBean> mNotificationListArray) {
			this.mNotificationListArray = mNotificationListArray;
		}

		/**
		 * 
		 * third level item
		 * 
		 */
		/*public static class ItemList {

			private String itemName;
			private String itemPrice;

			public ItemList(String itemName, String itemPrice) {
				super();
				this.itemName = itemName;
				this.itemPrice = itemPrice;
			}

			public String getItemName() {
				return itemName;
			}

			public void setItemName(String itemName) {
				this.itemName = itemName;
			}

			public String getItemPrice() {
				return itemPrice;
			}

			public void setItemPrice(String itemPrice) {
				this.itemPrice = itemPrice;
			}

		}*/

	}

}
