package paymentMonitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentCountPojo {

	private String count;
	private String paymentType;
	
	
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	
	
public PaymentCountPojo[] getCountDetails(String dateStart, String dateEnd) {
		
		List<PaymentCountPojo> list = new ArrayList<PaymentCountPojo>();
		PaymentCountPojo [] pay = new PaymentCountPojo[0];
		loadProps props = new loadProps();
		PaymentCountPojo pcp = null;
		Connection con = null;
	    PreparedStatement pc = null;
	    ResultSet rs = null;
		
		try{
			
			String query2 = "select count(*) as Number, payment_type from ZENBASENET..zib_cib_pmt_payments where ptystatus in ('pending') and approval_date is not null and payment_due_date <= getdate() and time_stamp >= '" + dateStart +" 00:00:00.000 AM'  and time_stamp <= '"+ dateEnd +" 11:59:59.999 PM' group by payment_type";
            con = props.getInstance();
            System.out.println("Query : " + query2);
		     pc = con.prepareStatement(query2);
		     rs = pc.executeQuery();
		     
		     while (rs.next()) {
		    	 pcp = new PaymentCountPojo();
		    	pcp.setPaymentType(rs.getString("payment_type")); 
		    	pcp.setCount(rs.getString("Number")); 
		    	
		    	list.add(pcp);
		     }
		     pay = new PaymentCountPojo[list.size()];
		     pay = list.toArray(pay);
		}catch(Exception e){
			 e.printStackTrace();
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
		return pay;
	}
	
}




