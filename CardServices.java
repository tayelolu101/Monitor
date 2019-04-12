package com.zenithbank.banking.cardsummary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.zenithbank.stringhelper.JSONArray;
import com.zenithbank.stringhelper.JSONObject;

public class CardServices {

	private static String GET_TRANS_URL = null;
	private static String GET_CARDS_URL = null;
	private static String X_CALLER_ID = null;
	private static String X_CALLER_NAME = null;
	private static String X_CALLER_PASSWORD = null;
	private static String X_CALLER_SESSION_ID = null;

	private static boolean IS_DEV;
	/*
	 * public static void main(String[] args) { CardServices cardServices = new
	 * CardServices(); //List<CardPojo> cards =
	 * cardServices.getCardsByAccountNumber("1020057054");
	 * 
	 * CardTransaction cardTransaction =
	 * cardServices.getCardTransactions("1020155668", 7012731L, "20180101",
	 * "20180320");
	 * 
	 * System.out.println("SIZE  " +
	 * cardTransaction.getResponseCode().contains("00"));
	 * 
	 * 
	 * 
	 * 
	 * }
	 */
	static {
		Properties ps = new Properties();
		try {
			ps.load(CardServices.class.getResourceAsStream("application.properties"));
			IS_DEV = ps.getProperty("cardservice.dev.mode").contains("true");
			GET_TRANS_URL = ps.getProperty("zencore.cardservices.gettrans.uri");
			GET_CARDS_URL = ps.getProperty("zencore.cardservices.getcards.uri");

			// System.out.println("GET " + GET_TRANS_URL);

			X_CALLER_ID = ps.getProperty("zencore.caller.id");
			X_CALLER_NAME = ps.getProperty("zencore.caller.name");
			X_CALLER_PASSWORD = ps.getProperty("zencore.caller.password");
			X_CALLER_SESSION_ID = ps.getProperty("zencore.cust.session.id");
		} catch (FileNotFoundException ex) {
			Logger.getLogger(CardServices.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(CardServices.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private HttpURLConnection getHttpConnectionWithHeader(String apiUrl) throws IOException {
		URL url = new URL(apiUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("X-CallerID", X_CALLER_ID);
		con.setRequestProperty("X-CallerName", X_CALLER_NAME);
		con.setRequestProperty("X-CallerPassword", X_CALLER_PASSWORD);
		con.setRequestProperty("X-CUST-SESSIONID", X_CALLER_SESSION_ID);
		con.setRequestProperty("Content-Type", "application/json");

		return con;

	}

	// public C
	public CardTransaction getCardTransactions(String accountNumber, Long cardId, String startDate, String endDate) {

		CardTransaction cardTransaction = new CardTransaction();

		// System.out.println("IS DEV " + IS_DEV);
		// TODO remove this on production
		if (IS_DEV) {
			System.setProperty("javax.net.ssl.trustStore",
					"C:\\Program Files\\Java\\jdk1.8.0_111\\jre\\lib\\security\\cacerts");
		}
		try {
			System.out.print("API URL" + GET_TRANS_URL);
			JSONObject postObject = new JSONObject();
			postObject.put("CardID", cardId);
			postObject.put("AccountNumber", accountNumber);
			postObject.put("StartDate", startDate);
			postObject.put("EndDate", endDate);
			
			byte[] postDataBytes = postObject.toString().getBytes("UTF-8");
			
			HttpURLConnection con = getHttpConnectionWithHeader(GET_TRANS_URL);
			
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			
			System.out.println("Postbytes " + postObject.toString());
			con.getOutputStream().write(postDataBytes);
			
			int responseCode = con.getResponseCode();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			
			StringBuilder inputBuffer = new StringBuilder();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				while ((inputLine = in.readLine()) != null) {
					inputBuffer.append(inputLine);

				}
				in.close();

				System.out.print(" out " + inputBuffer.toString());
				ObjectMapper objectMapper = new ObjectMapper();

				cardTransaction = objectMapper.readValue(inputBuffer.toString(), cardTransaction.getClass());

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		return cardTransaction;

	}

	public List<CardPojo> getCardsByAccountNumber(String accountNumber) {
		// Todo get this from config

		// System.out.println("IS DEV " + IS_DEV);
		// TODO remove this on production
		if (IS_DEV) {
			System.setProperty("javax.net.ssl.trustStore",
					"C:\\Program Files\\Java\\jdk1.8.0_111\\jre\\lib\\security\\cacerts");
		}
		List<CardPojo> cards = new ArrayList<CardPojo>();

		URL url;
		try {
			String API_URL = GET_CARDS_URL;

			API_URL = API_URL.replace("{accountNumber}", accountNumber);
			 System.out.println("API URL FOR RETRIEVAL " + API_URL);
			url = new URL(API_URL);
			HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();

			// con.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			StringBuilder inputBuffer = new StringBuilder();
			if (responseCode == HttpURLConnection.HTTP_OK) {

				while ((inputLine = in.readLine()) != null) {

					inputBuffer.append(inputLine);
				}
			
				JSONArray cardArray = new JSONArray(inputBuffer.toString());
				System.out.println("Json Array " + cardArray);
				
				//ObjectMapper objectMapper = new ObjectMapper();
				
				//cards = Arrays.asList(objectMapper.readValue(inputBuffer.toString(), CardPojo[].class));

				for (int i = 0; i < cardArray.length(); i++) {
					CardPojo card = new CardPojo();
					JSONObject cardObject = cardArray.getJSONObject(i);
					card.setAccountName(cardObject.optString("AccountName"));
					card.setAccountNumber(cardObject.optString("AccountNumber"));
					card.setId(cardObject.optLong("ID"));
					card.setCardNumber(cardObject.optString("CardNo"));
					card.setCurrency(cardObject.optString("Currency"));
					card.setBalance(cardObject.optString("Balance"));
					card.setProdId(cardObject.optString("ProdID"));
					cards.add(card);
				}

			}

			in.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cards;

	}
	


}

