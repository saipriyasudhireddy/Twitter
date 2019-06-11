package org.saipriya.prototype.twitter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;



public class User {
	JedisConfig jc = null;
	public User() {
		jc = JedisConfig.getInstance(); 
	}
	
	public void follow(String followee,String follower) {
		Jedis jedis1 = null;
		Jedis jedis4= null;
		try {
			jedis1 = JedisConfig.getInstance().getJedis(1);
			// Add followee to follower
			jedis1.sadd(followee, follower);
			jedis4 = JedisConfig.getInstance().getJedis(4);
			// Add follower to followee
			jedis4.sadd(follower, followee);
		} catch (JedisException e) {
			jedis1 = JedisConfig.getInstance().returnBrokenResource(jedis1);
			jedis4 = JedisConfig.getInstance().returnBrokenResource(jedis4);
            
        } finally {
        	JedisConfig.getInstance().returnResource(jedis1);
        	JedisConfig.getInstance().returnResource(jedis4);
        }
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
