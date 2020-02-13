package com.ani.twitterdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import twitter4j.PagableResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * @author Anitesh
 * 
 * This is just a demo app which shows how to use twitter4j to get followers of any user,
 * It can be converted to big app and can be used to do analytics for twitter app
 *
 */
/**
 * @author I-tech_Anitesh
 *
 */
public class TwitterDataJavaApplication {

	public static void main(String[] args) {
		Twitter t = TwitterFactory.getSingleton();
		TwitterDataJavaApplication app = new TwitterDataJavaApplication();

		try {
			System.out.println("1. Post a tweet ");
			System.out.println("2. get followers ");
			System.out.println("3. authorize your account ");
			System.out.println("===================");
			Scanner s = new Scanner(new InputStreamReader(System.in));
			int option = s.nextInt();

			switch (option) {

			case 1:
				System.out.println(" Enter your tweet message to be posted ");
				String msg = s.next();
				String status = app.postTweet(msg, t);
				if (status.equalsIgnoreCase(msg)) {
					System.out.println("Yayyyy!!! tweet succcessfully posted ------- " + status);
				} else {
					System.out.println("sorry unable to post teh tweet due to server issue");
				}
				break;
			case 2:
				System.out.println(" Enter the username whose followers are needed");
				String userName = s.next();
				int result = app.getFollowers(userName, t);
				if (result == 1) {
					System.out.println("successfully returned all followers");
				} else {
					System.out.println("sorry unable to get followers due to server issue");
				}
				break;

			case 3:
				//not applicable if you are storing access token in twitter4j.properties file, 
				//please remove the access token from there and then call this method
				/*
				 * try { AccessToken token = app.getAccessToken(t);
				 * System.out.println("access token :"+token.getToken()
				 * +"and secret is :"+token.getTokenSecret()); } catch (IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } break;
				 */
			}

		} catch (TwitterException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Used for posting user tweet
	 * 
	 * @param tweetMsg
	 * @param t
	 * @return
	 * @throws TwitterException
	 */
	public String postTweet(String tweetMsg, Twitter t) throws TwitterException {

		Status updateStatus = t.updateStatus(tweetMsg);
		return updateStatus.getText();
	}

	/**Used to get followers of any user
	 * 
	 * @param screenName
	 * @param t
	 * @return
	 * @throws TwitterException
	 * @throws InterruptedException
	 */
	public int getFollowers(String screenName, Twitter t) throws TwitterException, InterruptedException {
		long cursor = -1;
		List<User> followers = new ArrayList<>();
		PagableResponseList<User> ids = null;
		int i = 0;
		int j = 0;
		int k;
		do {
			try {
				ids = t.getFollowersList(screenName, cursor);
				for (k = 0, i = j; k < ids.size(); i++, k++) {
					User user = ids.get(k);
					String name = user.getName();
					System.out.println("Name" + (i + 1) + ":" + name);
				}
				cursor = ids.getNextCursor();
				j = j + 20;
			} catch (TwitterException e) {
				if (e.getStatusCode() == 429) {
					System.out.println("got rate limit exception, sleeping for 15 mins");
					Thread.sleep(900000);
				}
				continue;
			}
		} while (ids.hasNext());
		
		return 1;
	}

	/**Used to get oauth acess of any user
	 * 
	 * @param t
	 * @return
	 * @throws IOException
	 * @throws TwitterException
	 */
	public AccessToken getAccessToken(Twitter t) throws IOException, TwitterException {
		Twitter twit = TwitterFactory.getSingleton();
		RequestToken requestToken = twit.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = t.getOAuthAccessToken();
				} else {
					accessToken = t.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}

		return accessToken;
	}
}
