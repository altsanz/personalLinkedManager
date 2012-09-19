package twitter;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService {

	private String consumerKey = "7bS6Zca9JpaRbrZA8L66pw";
	private String consumerKeySecret = "WITiNhM2PsaH1sq5ef8jSJMWOX2VVxL0vt088U2E";
	private String oauthToken = "755527897-gNiDDNZjzwNXv0xskTUsRmP4mDljvSJce3UJ7wja";
	private String oauthTokenSecret = "YfrDkPi1i67idWqCou8reKsUOfaVD5xjtKIuS5Sj1Q";
	private Twitter twitter;
	private static TwitterService tweetr;

	public static TwitterService getInstance() {
		if (tweetr == null) {
			tweetr = new TwitterService();
		}
		return tweetr;
	}

	private TwitterService() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerKeySecret)
				.setOAuthAccessToken(oauthToken)
				.setOAuthAccessTokenSecret(oauthTokenSecret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	public void sendTweet(String frase) {
		try {
			if (frase != null) {
				frase = "#semprerecordaré " + frase;
				Status status;

				status = twitter.updateStatus(frase);

				String respuesta = "Successfully updated the status to ["
						+ status.getText() + "].";
				System.out.println(respuesta);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}