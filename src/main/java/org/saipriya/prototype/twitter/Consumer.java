package org.saipriya.prototype.twitter;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

class CustomComparator implements Comparator<String>{ 
	Jedis jedis2 = null;
	public int compare(String tweet1, String tweet2) { 
		try {
			jedis2 = JedisConfig.getInstance().getJedis(2);
			Map<String,String> tweet1_info = jedis2.hgetAll(tweet1);
			Map<String,String> tweet2_info = jedis2.hgetAll(tweet2);
			Long timestamp1 = Long.parseLong(tweet1_info.get("timestamp"));
			Long timestamp2 = Long.parseLong(tweet2_info.get("timestamp"));
			if(timestamp1<timestamp2) {
				return -1;
			}
			else if(timestamp1>timestamp2) {
				return 1;
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (JedisException e) {
			jedis2 = JedisConfig.getInstance().returnBrokenResource(jedis2);

		} finally {
			JedisConfig.getInstance().returnResource(jedis2);
		}

		return 0;
	} 
}

public class Consumer implements Runnable {

	private final BlockingQueue<String> queue;
	private static final int NEWSFEED_SIZE =5;
	public Consumer(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	public void run(){
		Jedis jedis2 = null;
		try {
			while (true) {
				jedis2 = JedisConfig.getInstance().getJedis(2);
				String tweetId = queue.take();
				Map<String,String> tweet_info = jedis2.hgetAll(tweetId);
				process(tweet_info.get("employeeid"),tweetId);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	private void process(String employeeId,String tweetId) throws InterruptedException {
		System.out.println("Started processing employeeId "+employeeId);
		Jedis jedis3 = null;
		// Get all the followers of employeeId
		try {
			Set<String> followers_of_employeeId = JedisConfig.getInstance().getJedis(1).smembers(employeeId);
			for(String emp: followers_of_employeeId) {
				jedis3 = JedisConfig.getInstance().getJedis(3);
				long total_tweets_len = JedisConfig.getInstance().getJedis(0).llen(emp);
				if(total_tweets_len<NEWSFEED_SIZE) {
					jedis3.lpush(emp, tweetId);
				}
				else {
					jedis3.rpop(emp);
					jedis3.lpush(emp, tweetId);
				}
			}
		} 
		catch (JedisException e) {
			jedis3 = JedisConfig.getInstance().returnBrokenResource(jedis3);
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			JedisConfig.getInstance().returnResource(jedis3);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10);
		Consumer mp = new Consumer(queue);
		mp.process("1","1");

	}


}