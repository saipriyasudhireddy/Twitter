package org.saipriya.prototype.twitter;
import redis.clients.jedis.Jedis;
import java.util.Set;


public class User {
	public void follow(String follower,String followee) {
		Jedis jedis1 = App.getJedis(1);
		// Add followee to follower
		jedis1.sadd(follower, followee);
		Jedis jedis4 = App.getJedis(4);
		// Add follower to followee
		jedis4.sadd(followee, follower);
	}
	
	public static void main(String[] args) {
		User user = new User();
		user.follow("1", "2");
		user.follow("1", "3");
		user.follow("2", "4");
		user.follow("2", "5");
		user.follow("3", "1");
		user.follow("3", "2");
		user.follow("3", "4");
		user.follow("3", "5");
	}

}
