package org.saipriya.prototype.twitter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisConfig 
{
//	static Jedis jedis0=null;
//	static Jedis jedis1=null;
//	static Jedis jedis2=null;
//	static Jedis jedis3=null;
//	static Jedis jedis4=null;
	
	// We can also use jedis pool to have multiple connections
	private static JedisConfig single_instance = null; 
	private static JedisPool pool;
    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    
    public static JedisConfig getInstance() 
    { 
        if (single_instance == null) 
            single_instance = new JedisConfig(); 
  
        return single_instance; 
    } 
    
    public JedisConfig(){
    	pool = new JedisPool(redisHost, redisPort);
    	
    }
    
	
	public  Jedis getJedis(int database) {
		 Jedis jedis = pool.getResource();
		 jedis.select(database);
		 return jedis;
	}
	
	public  Jedis returnBrokenResource(Jedis jedis) {
		if(jedis != null) {
			 pool.returnBrokenResource(jedis);
		}
		jedis = null;
		return jedis;
	}
	
	public  void returnResource(Jedis jedis) {
		if(jedis != null) {
			 pool.returnResource(jedis);
		}
	}
	
	
	
//	public static Jedis getJedis(int database) {
//		// TODO: Optimize this to return spring like objects for every database
//		if(database ==0) {
//			if(jedis0 != null) {
//				return jedis0;
//			}
//			jedis0 = new Jedis("localhost"); 
//			jedis0.select(0);
//			return jedis0;
//		}
//		if(database ==1) {
//			if(jedis1 != null) {
//				return jedis1;
//			}
//			jedis1 = new Jedis("localhost"); 
//			jedis1.select(1);
//			return jedis1;
//		}
//		if(database ==2) {
//			if(jedis2 != null) {
//				return jedis2;
//			}
//			jedis2 = new Jedis("localhost"); 
//			jedis2.select(2);
//			return jedis2;
//		}
//		if(database ==3) {
//			if(jedis3 != null) {
//				return jedis3;
//			}
//			jedis3 = new Jedis("localhost"); 
//			jedis3.select(3);
//			return jedis3;
//		}
//		if(database ==4) {
//			if(jedis4 != null) {
//				return jedis4;
//			}
//			jedis4 = new Jedis("localhost"); 
//			jedis4.select(4);
//			return jedis4;
//		}
//		return null;
//	}
	

	
	public static void main(String[] args) throws Exception {

		
	  }
}
