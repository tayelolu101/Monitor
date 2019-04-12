package paymentMonitor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentMonitorPojo {

	private String PaymentID;
	private String BatchID;
	private String BeneficiaryName;
	private String Status;
	private String PaymentType;
	private Date Authorizationdate;
	private String Duration;
	private String CompanyName;
	private String CompanyCode;
	private Date Transactiondate;
	private Date Paymentdate;
 
	
	
	
	public String getCompanyName() {
		return CompanyName;
	}
	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}
	public String getCompanyCode() {
		return CompanyCode;
	}
	public void setCompanyCode(String companyCode) {
		CompanyCode = companyCode;
	}
	public Date getTransactiondate() {
		return Transactiondate;
	}
	public void setTransactiondate(Date transactiondate) {
		Transactiondate = transactiondate;
	}
	public Date getPaymentdate() {
		return Paymentdate;
	}
	public void setPaymentdate(Date paymentdate) {
		Paymentdate = paymentdate;
	}
	public String getDuration() {
		return Duration;
	}
	public void setDuration(String duration) {
		Duration = duration;
	}
	public String getPaymentID() {
		return PaymentID;
	}
	public void setPaymentID(String paymentID) {
		PaymentID = paymentID;
	}
	public String getBatchID() {
		return BatchID;
	}
	public void setBatchID(String batchID) {
		BatchID = batchID;
	}
	public String getBeneficiaryName() {
		return BeneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		BeneficiaryName = beneficiaryName;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getPaymentType() {
		return PaymentType;
	}
	public void setPaymentType(String paymentType) {
		PaymentType = paymentType;
	}
	public Date getAuthorizationdate() {
		return Authorizationdate;
	}
	public void setAuthorizationdate(Date authorizationdate) {
		Authorizationdate = authorizationdate;
	}
	
	
	
	public PaymentMonitorPojo[] getDetails(String dateStart, String DateEnd){
		
		List<PaymentMonitorPojo> lists = new ArrayList<PaymentMonitorPojo>();
		PaymentMonitorPojo [] pojos = new PaymentMonitorPojo[0];
		loadProps props = new loadProps();
	    PaymentMonitorPojo  paymentMonitorPojo = null;
	    Connection con = null;
	    PreparedStatement pc = null;
	    ResultSet rs = null;
	   
	   try{
		 
		    
		     String query = "select b.time_stamp, b.payment_due_date, b.payment_id, b.batchid, b.vendor_name, b.approval_date , b.ptystatus, b.payment_type, a.company_name, a.company_code as code, DATEDIFF (hh, b.approval_date ,Getdate()) as Duration from zenbasenet..zib_cib_pmt_payments b, ZENBASENET..zib_cib_gb_company a where b.ptystatus in ('pending') and b.approval_date is not null and a.company_code = b.company_code and b.payment_due_date <= getdate() and b.time_stamp >= '" + dateStart + " 00:00:00.000 AM'  and b.time_stamp <= '" + DateEnd +" 11:59:59.999 PM' order by b.approval_date desc";
             con = props.getInstance();
		     System.out.println("Query : " + query);
		     pc = con.prepareStatement(query);
		     rs = pc.executeQuery();
		     
		     while (rs.next()) {
		    	 paymentMonitorPojo = new PaymentMonitorPojo();
		    	 
		    	 paymentMonitorPojo.setAuthorizationdate(rs.getDate("approval_date"));
		    	 paymentMonitorPojo.setPaymentType(rs.getString("payment_type"));
		    	 paymentMonitorPojo.setStatus(rs.getString("ptystatus"));
		    	 paymentMonitorPojo.setBeneficiaryName(rs.getString("vendor_name"));
		    	 paymentMonitorPojo.setBatchID(rs.getString("batchid"));
		    	 paymentMonitorPojo.setPaymentID(rs.getString("payment_id"));
		    	 paymentMonitorPojo.setDuration(rs.getString("Duration"));
		    	 paymentMonitorPojo.setCompanyName(rs.getString("company_name"));
		    	 paymentMonitorPojo.setCompanyCode(rs.getString("code"));
		    	 paymentMonitorPojo.setTransactiondate(rs.getDate("time_stamp"));
		    	 paymentMonitorPojo.setPaymentdate(rs.getDate("payment_due_date"));
		    	 lists.add(paymentMonitorPojo);
			}
		     pojos = new PaymentMonitorPojo[lists.size()];
		     pojos = lists.toArray(pojos);
		     return pojos;
	   }catch(Exception ex){

		   ex.printStackTrace();
	   }finally{
		   if(rs != null){
			   try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		   if(pc != null){
			   try {
				pc.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		   if(con != null){
			   try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	   }
	   return pojos;
		
	}
	
	
	
	
	 public static String formatAmount(String amount)
	    {
	        double myAmount = Double.parseDouble(amount);
	        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH);
	        formatter.setMinimumFractionDigits(2);
	        formatter.setMaximumFractionDigits(2);
	        return  formatter.format(myAmount);

	    }
	
}
