package com.example.kkBazar.entity.payment;

public class CCAvenueRequest {
	 private String merchantId;
	    private String accessCode;
	    private String workingKey;
	    private String amount;
	    private String orderId;
	    private String redirectUrl;
		public String getMerchantId() {
			return merchantId;
		}
		public void setMerchantId(String merchantId) {
			this.merchantId = merchantId;
		}
		public String getAccessCode() {
			return accessCode;
		}
		public void setAccessCode(String accessCode) {
			this.accessCode = accessCode;
		}
		public String getWorkingKey() {
			return workingKey;
		}
		public void setWorkingKey(String workingKey) {
			this.workingKey = workingKey;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public String getOrderId() {
			return orderId;
		}
		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}
		public String getRedirectUrl() {
			return redirectUrl;
		}
		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}
		public CCAvenueRequest() {
			super();
		}
		public CCAvenueRequest(String merchantId2, String accessCode2, String workingKey2, String amount2,
				String orderId2, String redirectUrl2) {
			// TODO Auto-generated constructor stub
		}
	    
}
