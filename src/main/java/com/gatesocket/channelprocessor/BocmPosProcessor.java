package com.gatesocket.channelprocessor;


import java.util.List;
import java.util.Map;

import com.capinfo.common.util.MoneyUtil;
import com.capinfo.payment.appcomm.ApplicationException;
import com.capinfo.payment.base.CDbUtil;
import com.capinfo.payment.payment.Payment4_0;
import com.gatesocket.Request;
import com.gatesocket.Response;
import com.gatesocket.Transaction;
import com.gatesocket.channel.ChannelException;
import com.gatesocket.channel.ChannelProcessor;
import com.gatesocket.channel.stream.StreamParser;
import com.gatesocket.channel.stream.Stream;


/**
 * 
 * @Creator Fx
 * @Create-Date 2012-10-09
 */
public class BocmPosProcessor  implements ChannelProcessor{

	private StreamParser streamParser;
	private static WriteLog log = WriteLog.instance();
	
	public void process(Transaction transaction) throws ChannelException {
		
		try {
			log.writeStringToFile("��ʼҵ����");
			
		    Request request= transaction.getRequest();
		    
		    byte[] content = (byte[]) request.getBody();
		    
		    log.writeStringToFile(content.toString());

		    BocReqHeader bocheader = this.getHeader(content);
		    
		    String TRAN_CODE = bocheader.getTRAN_CODE();
		    	        		    
		    Payment4_0 payment40 = new Payment4_0();
		    
		    if("000010".equals(TRAN_CODE)){  //��ѯ������Ϣ����
		    	log.writeStringToFile("������ѯ���״���ʼ��");
		    	String PAY_APP_NO = this.getMsg(content, 112, 32).trim();  //֧�������
		    	log.writeStringToFile("PAY_APP_NO:["+PAY_APP_NO+"]");
//		    	DbUtil dbutil = new DbUtil();
		    	
		    	payment40.BankOID = PAY_APP_NO; //ԭʼ������
		    	payment40.PmodeID = 143;  //�ڿ�֧��  
					
		    	Response response = transaction.getResponse();
		    	byte[] responsebody = new byte[324]; //��ѯ�������Ķ�������324
				for(int i=0;i<responsebody.length;i++){  
					responsebody[i]=' '; //��ʼ��Ϊ�ո�  
				}
				
				this.buildresponse(responsebody, "000011".getBytes(), 0, 6);   //��������

				this.buildresponse(responsebody, rightfillblank(PAY_APP_NO.getBytes(), 20), 6, 20);  //��˾ҵ����ˮ��
				
				this.buildresponse(responsebody, rightfillblank(PAY_APP_NO.getBytes(), 32), 72, 32); //֧�������
				
				CDbUtil dbUtil = null;
		    	try {
		    		log.writeStringToFile("���ݶ�����"+PAY_APP_NO+"��ѯ���ݿ��¼");
		    		
			        dbUtil = new CDbUtil(false);
			        payment40.setDBConn(dbUtil.getConnection());
			        payment40.LoadPayIDByBankMsg(null);
			        log.writeStringToFile("����״̬Ϊ"+payment40.PayStatus);
			        if(payment40.PayStatus!=2){  //�ö�����֧��֧����
			        	throw new ApplicationException(0, "300001", "0", "�ö�����֧��");  
			        }
					
					String XAmountCY = MoneyUtil.yuanToFen(payment40.XAmountCY.toString());   //�������
					log.writeStringToFile("�������Ϊ"+XAmountCY);
					String CardHolder = payment40.CardHolder;       //����
					String certificate = payment40.IDCardNo;     //֤����
					    
						this.buildresponse(responsebody, "000000".getBytes(), 26, 6);  //������� 	
						
						this.buildresponse(responsebody, rightfillblank("�ɹ�".getBytes(), 40), 32, 40); //���������Ϣ
						
						this.buildresponse(responsebody,this.leftfillzero(XAmountCY.getBytes(), 12), 112, 12);
						
						if(null!=CardHolder&&CardHolder.trim().length()>0)
							this.buildresponse(responsebody,this.rightfillblank(CardHolder.getBytes(), 20), 124, 20);  //֧��������
							
						if(null!=certificate&&certificate.trim().length()>0)
						    this.buildresponse(responsebody,this.rightfillblank(certificate.getBytes(), 32), 144, 20);  //������֤����
					
					    this.buildresponse(responsebody,"00000000".getBytes(), 104, 8);  //У����
					    
					    response.setBody(responsebody);										
					
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					this.buildresponse(responsebody, "300001".getBytes(), 26, 6);  //������� 	
					
					this.buildresponse(responsebody, "δ�ҵ�������Ķ�������ȷ�϶�����������ȷ".getBytes(), 32, 40); //���������Ϣ
					
					response.setBody(responsebody);
					log.writeStringToFile(e.getMessage());
				}finally {
		            dbUtil.closeConnection();
		            dbUtil = null;
		        }

		    }
		    if("000020".equals(TRAN_CODE)){  //�ɷ�ȷ��
		    	String PAY_APP_NO = this.getMsg(content, 112, 32).trim();  //֧�������
		    	String AMOUNT = this.getMsg(content, 144, 12).trim();  //֧�����
		    	String PAYCOUNT = this.getMsg(content, 156, 2).trim();  //֧������
		    	String TRAN_TYPE = this.getMsg(content, 158, 3).trim();  //CUP ���п�
		    	String BATCH_NO = this.getMsg(content, 161, 6).trim();  //���κ�
		    	String BILL_NO = this.getMsg(content, 167, 6).trim();  //Ʊ�ݺ�
		    	String Seq_NO = this.getMsg(content, 173, 6).trim();  //POS��ˮ��
		    	String CARD_NO = this.getMsg(content, 179, 19).trim();  //����
		    	String SUB_AMOUNT = this.getMsg(content, 198, 12).trim();  //���ʵ�֧�����
		    	String TRAN_DATE = this.getMsg(content, 210, 6).trim();  //��������
		    	String TRAN_TIME = this.getMsg(content, 216, 6).trim();  //����ʱ��
		    	String SYSTRC_NO = this.getMsg(content, 222, 12).trim();  //ϵͳ�ο���
		    	String AUTH_NO = this.getMsg(content, 234, 6).trim();  //��Ȩ��
		    	String Issuer_Id = this.getMsg(content, 240, 8).trim();  //Ԥ���ѿ���˾��
		    	String CARD_TYP = this.getMsg(content, 248, 3).trim();  //�����
		    	String CARD_NAME = this.getMsg(content, 251, 20).trim();  //���������
		    	
		    	payment40.BankOID = PAY_APP_NO; //ԭʼ������
		    	payment40.PmodeID = 143;  //�ڿ�֧��
		    	
		    	Response response = transaction.getResponse();
		    	byte[] responsebody = new byte[268]; //��ѯ�������Ķ�������324
				for(int i=0;i<responsebody.length;i++){
					responsebody[i]=' '; //��ʼ��Ϊ�ո�
				}
		    	
				log.writeStringToFile("�ɷ�ȷ�ϴ�����");
				this.buildresponse(responsebody, "000021".getBytes(), 0, 6);   //��������

				this.buildresponse(responsebody, rightfillblank(PAY_APP_NO.getBytes(),20), 6, 20);  //��˾ҵ����ˮ��
				CDbUtil dbUtil = null;
				
		    	try {
		    		log.writeStringToFile("���ݶ�����"+PAY_APP_NO+"��ѯ���ݿ��¼");
			        dbUtil = new CDbUtil(false);
			        payment40.setDBConn(dbUtil.getConnection());
					payment40.LoadPayIDByBankMsg(null); //���ݶ����Ų�ѯPAYMENT40����
					
					if (CARD_NO.trim().length()>0)
				    	payment40.CardNo = CARD_NO;
				    	
				    	String authStr = "";
				    	if (AUTH_NO.trim().length()>0) {
			                //��Ȩ��
			                authStr += "AC=" + AUTH_NO+ "|";
			            }
			            if (SYSTRC_NO.trim().length()>0) {
			                //ϵͳ�ο���
			                authStr += "SN=" + SYSTRC_NO + "|";
			            }
				    	payment40.AuthCode = authStr;
				    	
				    	if(CARD_TYP.startsWith("5"))  {
				    		log.writeStringToFile("�⿨����");
				    	    payment40.updatePmodeId(payment40.OrderCDFK, 144);
				    	}

				    	else if(CARD_TYP.startsWith("0")){
				    		log.writeStringToFile("�ڿ�����");
				    	    payment40.updatePmodeId(payment40.OrderCDFK, 143);
				    	}

				    	payment40.PayStatus=1;  //����״̬Ϊ��֧��
				    	payment40.update();
				    	
				    	this.buildresponse(responsebody, "000000".getBytes(), 26, 6);  //������� 	
						
						this.buildresponse(responsebody, rightfillblank("�ɹ�".getBytes(), 40), 32, 40); //���������Ϣ
						
						if(null!=PAY_APP_NO&&PAY_APP_NO.trim().length()>0)
						this.buildresponse(responsebody,rightfillblank(PAY_APP_NO.getBytes(), 32), 72, 32); //֧�������
						
						this.buildresponse(responsebody,"00000000".getBytes(), 104, 8);  //У����
						
						if(null!=AMOUNT&&AMOUNT.trim().length()>0){
							this.buildresponse(responsebody,leftfillzero(AMOUNT.getBytes(), 12), 112, 12);  //�����ܽ��
							
							this.buildresponse(responsebody,leftfillzero(AMOUNT.getBytes(), 12), 124, 12);  //���׽��
						}
						
						
						this.buildresponse(responsebody,leftfillzero("0".getBytes(), 12), 136, 12);  //����ʣ��֧�����
										    	
				  
						response.setBody(responsebody);
				    	
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					
                    this.buildresponse(responsebody, "300001".getBytes(), 26, 6);  //������� 	
					
					this.buildresponse(responsebody, "δ�ҵ�������Ķ�������ȷ�϶�����������ȷ".getBytes(), 32, 40); //���������Ϣ
					
					response.setBody(responsebody);
					log.writeStringToFile(e.getMessage());
				}
				finally {
		            dbUtil.closeConnection();
		            dbUtil = null;
		        }
		    	
		    }
		    		    		    
		} catch (ChannelException e) {
			throw new ChannelException(e);
		}
		catch (Exception e) {
			log.writeStringToFile("ҵ�����쳣");
			log.writeStringToFile(e.getMessage());
			e.printStackTrace();
		}
		log.writeStringToFile("ҵ�������");

	}
	public static String getMsg(byte[] content,int start,int length){
		
        byte[] msg = new byte[length];
        for(int i = start;i<start+length;i++){
        	msg[i-start] = content[i];
        }
		return new String(msg);
		
	}
	
	public static BocReqHeader getHeader(byte[] content){
		BocReqHeader bocheader = new BocReqHeader();
		bocheader.setTRAN_CODE(BocmPosProcessor.getMsg(content, 0, 6));
		bocheader.setUSER(BocmPosProcessor.getMsg(content,6, 20));
		bocheader.setPASSWORD(BocmPosProcessor.getMsg(content,26, 10));
		bocheader.setBANK_CODE(BocmPosProcessor.getMsg(content,36, 8));
		bocheader.setINSURE_ID(BocmPosProcessor.getMsg(content,44, 8));
		bocheader.setMIDNO(BocmPosProcessor.getMsg(content,52, 15));
		bocheader.setTIDNO(BocmPosProcessor.getMsg(content,67, 8));
		bocheader.setBK_ACCT_DATE(BocmPosProcessor.getMsg(content,75, 8));
		bocheader.setBK_ACCT_TIME(BocmPosProcessor.getMsg(content,83, 6));
		bocheader.setBK_SERIAL(BocmPosProcessor.getMsg(content,89, 20));
		bocheader.setBK_TRAN_CHNL(BocmPosProcessor.getMsg(content,119, 3));
		log.writeStringToFile("TRAN_CODE:["+bocheader.getTRAN_CODE()+"]");
		log.writeStringToFile("USER:["+bocheader.getUSER()+"]");
		log.writeStringToFile("PASSWORD:["+bocheader.getPASSWORD()+"]");
		log.writeStringToFile("BANK_CODE:["+bocheader.getBANK_CODE()+"]");
		log.writeStringToFile("INSURE_ID:["+bocheader.getINSURE_ID()+"]");
		log.writeStringToFile("MIDNO:["+bocheader.getMIDNO()+"]");
		log.writeStringToFile("TIDNO:["+bocheader.getTIDNO()+"]");
		log.writeStringToFile("BK_ACCT_DATE:["+bocheader.getBK_ACCT_DATE()+"]");
		log.writeStringToFile("BK_ACCT_TIME:["+bocheader.getBK_ACCT_TIME()+"]");
		log.writeStringToFile("BK_SERIAL:["+bocheader.getBK_SERIAL()+"]");
		log.writeStringToFile("BK_TRAN_CHNL:["+bocheader.getBK_TRAN_CHNL()+"]");
		return bocheader;
	}
	
	public static byte[] buildresponse(byte[] responsebody,byte[] msg,int start,int length){
		for(int i=start;i<start+length;i++){
			responsebody[i] = msg[i-start];
		}
		return responsebody;
		
	}
	
	public static byte[] leftfillzero(byte[] msg,int length){
		byte[] newmsg = new byte[length];
		for(int i=0;i<length;i++){
			int dec = newmsg.length-msg.length;
			if(i<dec)
				newmsg[i] = '0';
			else
				newmsg[i] = msg[i-dec];
		}
		return newmsg;
	}
	
	public static byte[] rightfillblank(byte[] msg,int length){
		byte[] newmsg = new byte[length];
		for(int i=0;i<length;i++){
			if(i<msg.length)
				newmsg[i] = msg[i];
			else
				newmsg[i] = ' ';
		}
		return newmsg;
	}
	
	public static void main(String args[]){
		
//		String aa= "������111";
//		
//		byte[] bb = BocmPosProcessor.rightfillblank(aa.getBytes(), 15);
//		
//		byte[] responsebody = new byte[324];
//		for(int i=0;i<responsebody.length;i++){
//			responsebody[i]=' ';
//		}
//		
//		
//		BocmPosProcessor.buildresponse(responsebody, bb, 15, 15);
//		
//		BocmPosProcessor.buildresponse(responsebody, bb, 35, 15);
//		
//		System.out.println("["+new String(responsebody)+"]");
		


		
	}


}
