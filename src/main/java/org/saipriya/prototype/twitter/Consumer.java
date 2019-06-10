package org.saipriya.prototype.twitter;

import java.util.List;
import java.util.*;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import redis.clients.jedis.Jedis;

class CustomComparator implements Comparator<String>{ 
    Jedis jedis2 = App.getJedis(2);
    public int compare(String tweet1, String tweet2) { 
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
    	return 0;
} 
}

public class Consumer implements Runnable {

    private final BlockingQueue<String> queue;
    
    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void run(){
        try {
            while (true) {
                String employeeId = queue.take();
                process(employeeId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private void process(String employeeId) throws InterruptedException {
    	System.out.println("Started processing employeeId "+employeeId);
        // Get all the followers of employeeId
        Set<String> followers_of_employeeId = App.getJedis(1).smembers(employeeId);
        for(String emp: followers_of_employeeId) {
        	//Get all the followees of the employeeId
        	Set<String> followees_of_emp = App.getJedis(4).smembers(emp);
        	 PriorityQueue<String> pq = new
                     PriorityQueue<String>(5, new CustomComparator()); 
        	 //Get top 100 tweets made by each followee(sort by recency)
         	 for(String followee: followees_of_emp) {
         		long total_tweets_len = App.getJedis(0).llen(followee);
         		List<String> top100Tweets = App.getJedis(0).lrange(followee,-total_tweets_len,-total_tweets_len+99);
         		pq.addAll(top100Tweets);
         	 }
  
         	Jedis jedis3 = App.getJedis(3);
         	jedis3.del(emp);
         	for(int i=0;i<5;i++) {
         		if(!pq.isEmpty()) {
         			jedis3.sadd(emp, pq.poll());
         		}
            }
        	 
        }  
    }

    public static void main(String[] args) throws InterruptedException {
    	BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10);
		Consumer mp = new Consumer(queue);
    	mp.process("1");
    	
	}


}