package org.saipriya.prototype.twitter;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import redis.clients.jedis.Jedis;

public class Tweet {

	public BlockingQueue<String> queue;
	
	public Tweet() {
		queue = new LinkedBlockingQueue<String>(10000);
		// TODO Use thread pool instead of single thread
		Consumer mp = new Consumer(queue);
		new Thread(mp).start();
	}
	
	
	public String tweet(String employeeid,String tweet) throws InterruptedException {
		
		Jedis jedis = App.getJedis(2);
		UUID uuid = UUID.randomUUID();
		long now = Instant.now().toEpochMilli();
		// Add tweet into to tweet database
		jedis.hset(uuid.toString(), "employeeid", employeeid);
		String timestamp = Long.toString(now);
		jedis.hset(uuid.toString(), "timestamp", timestamp);
		jedis.hset(uuid.toString(), "tweet", tweet);
		Jedis jedis1 = App.getJedis(0);
		System.out.println(timestamp);
		// Add tweet info the list of tweets made by the user to the front
		jedis1.lpush(employeeid, uuid.toString());
		// Queue employeeid to the queue to refresh the feed of all his followers
		this.queue.put(employeeid);
		return uuid.toString();
	}
	public static void main(String[] args) throws InterruptedException {
		Tweet tweet = new Tweet();
		String tweetId=tweet.tweet("1", "This is my first tweet");
		Thread.sleep(100);
		String tweetId1=tweet.tweet("1", "This is my second tweet");
		Thread.sleep(100);
		String tweetId13=tweet.tweet("3", "User3: my first tweet");
		Thread.sleep(100);
		String tweetId2=tweet.tweet("1", "This is my third tweet");
		Thread.sleep(100);
		String tweetId3=tweet.tweet("1", "This is my fourth tweet");
		Thread.sleep(100);
		String tweetId23=tweet.tweet("3", "User3: my second tweet");
		Thread.sleep(100);
		String tweetId4=tweet.tweet("1", "This is my fifth tweet");
		Thread.sleep(100);
		String tweetId33=tweet.tweet("3", "User3: my third tweet");
		Thread.sleep(100);
		String tweetId5=tweet.tweet("1", "This is my sixth tweet");
		Thread.sleep(100);
	}
	

}
