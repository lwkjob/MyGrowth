package com.gatesocket.channelprocessor;

/**
 * 
 * @Creator Fx
 * @Create-Date 2012-10-09
 */
public class BocmResHeader {

	private String TRAN_CODE; //��������
	private String RCPT_NO;  //��˾ҵ����ˮ��
	private String RSLT_CODE; //�������
	private String RSLT_MSG;  //���������Ϣ
	
	public String getTRAN_CODE() {
		return TRAN_CODE;
	}
	public void setTRAN_CODE(String tran_code) {
		TRAN_CODE = tran_code;
	}
	public String getRCPT_NO() {
		return RCPT_NO;
	}
	public void setRCPT_NO(String rcpt_no) {
		RCPT_NO = rcpt_no;
	}
	public String getRSLT_CODE() {
		return RSLT_CODE;
	}
	public void setRSLT_CODE(String rslt_code) {
		RSLT_CODE = rslt_code;
	}
	public String getRSLT_MSG() {
		return RSLT_MSG;
	}
	public void setRSLT_MSG(String rslt_msg) {
		RSLT_MSG = rslt_msg;
	}
}
