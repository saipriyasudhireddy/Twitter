package org.saipriya.prototype.twitter;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class Tweet {

	public BlockingQueue<String> queue;

	public Tweet() {
		queue = new LinkedBlockingQueue<String>(10000);
		// TODO Use thread pool instead of single thread
		Consumer mp = new Consumer(queue);
		new Thread(mp).start();
	}

	@SuppressWarnings("resource")
	public String tweet(String employeeid,String tweet) throws InterruptedException {
		Jedis jedis2 = null;
		Jedis jedis0= null;
		String tweetId = null;
		try {
			jedis2 = JedisConfig.getInstance().getJedis(2);
			tweetId = UUID.randomUUID().toString();
			long now = Instant.now().toEpochMilli();
			// Add tweet into to tweet database
			jedis2.hset(tweetId, "employeeid", employeeid);
			String timestamp = Long.toString(now);
			jedis2.hset(tweetId, "timestamp", timestamp);
			jedis2.hset(tweetId, "tweet", tweet);
			jedis0 = JedisConfig.getInstance().getJedis(0);
			System.out.println(timestamp);
			// Add tweet info the list of tweets made by the user to the front
			jedis0.lpush(employeeid, tweetId);
			// Queue employeeid to the queue to refresh the feed of all his followers
			this.queue.put(tweetId);
		}
		catch (JedisException e) {
            jedis2 = JedisConfig.getInstance().returnBrokenResource(jedis2);
            jedis0 = JedisConfig.getInstance().returnBrokenResource(jedis0);
            
        } finally {
        	JedisConfig.getInstance().returnResource(jedis2);
        	JedisConfig.getInstance().returnResource(jedis0);
        }
		return tweetId;
	}
	public static void main(String[] args) throws InterruptedException {
		Tweet tweet = new Tweet();
//		String tweetId=tweet.tweet("1", "This is my first tweet");
//		System.out.println(tweetId);
//		Thread.sleep(100);
//		String tweetId1=tweet.tweet("1", "This is my second tweet");
//		System.out.println(tweetId1);
//		Thread.sleep(100);
//		String tweetId13=tweet.tweet("3", "User3: my first tweet");
//		System.out.println(tweetId13);
//		Thread.sleep(100);
//		String tweetId2=tweet.tweet("1", "This is my third tweet");
//		System.out.println(tweetId2);
//		Thread.sleep(100);
//		String tweetId3=tweet.tweet("1", "This is my fourth tweet");
//		System.out.println(tweetId3);
//		Thread.sleep(100);
//		String tweetId23=tweet.tweet("3", "User3: my second tweet");
//		System.out.println(tweetId23);
//		Thread.sleep(100);
//		String tweetId4=tweet.tweet("1", "This is my fifth tweet");
//		System.out.println(tweetId4);
//		Thread.sleep(100);
//		String tweetId33=tweet.tweet("3", "User3: my third tweet");
//		System.out.println(tweetId33);
//		Thread.sleep(100);
		String tweetId5=tweet.tweet("1", "This is my sixth tweet");
		System.out.println(tweetId5);
	}


}
