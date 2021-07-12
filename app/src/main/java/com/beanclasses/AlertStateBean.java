package com.beanclasses;

public class AlertStateBean {

		String StateName = "";
		int scount = 0;
		int AdCnt = 0;


		public String GetStateName() {
			return StateName;
		}

		public void SetStateName(String s) {
			this.StateName = s;
		}

		public int GetSCount() {
			return scount;
		}

		public void SetCount(int s) {
			this.scount = s;
		}

		public int GetAdCnt() {
			return AdCnt;
		}

		public void SetAdCnt(int s) {
			this.AdCnt = s;
		}

	

}
