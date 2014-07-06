package com.webservice.mongodb.action;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.webservice.common.action.BaseAction;
import com.webservice.mongodb.model.TestUser;

/** 
 * <p>Description: [大数据处理]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class MongoDbAction extends BaseAction {
	private static final Logger logger = Logger.getLogger(MongoDbAction.class);
	private MongoTemplate mongoTemplate;
	
	public String showMongoDbTest(){
		logger.info("请求showMongoDbTest");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
		
		TestUser user = new TestUser();
		//若存在ID，则设置为主键，若不存在ID，则自动插入主键
		//user.setId("1112");
		//若某属性为NULL，则该列将不会存储在数据库中
		//user.setuId("testUser");
		user.setUserName("名字6");
		
		//保存数据,持久化
		//mongoTemplate.save(user);
		
		Map<String, Object> testMap = new HashMap<String, Object>();
		testMap.put("id", "abcd");
		testMap.put("name", "姓名");
		testMap.put("age", 25);
		testMap.put("sex", "男");
		testMap.put("success", false);
		//下面这种直接存储将会导致异常报错，大意是：没有持久化的Model对象
		//mongoTemplate.save(testMap);
		//使用原生的驱动可以存储
		try {
			MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017),
					Arrays.asList(new MongoCredential[] { MongoCredential.createMongoCRCredential("swpigris81", "mydb","812877".toCharArray()) }));
			DB db = mongoClient.getDB("mydb");
			db.requestStart();
			//事务开始
			DBCollection dbCollection = db.getCollection("testUser");
			BasicDBObject bdbo = new BasicDBObject();
			bdbo.putAll(testMap);
			//插入
			dbCollection.insert(bdbo);
			//事务结束
			db.requestDone();
			//查询
			DBCursor dbCursor = dbCollection.find();
			while(dbCursor.hasNext()){
				logger.info("查询结果：" + dbCursor.next());
			}
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		
		//使用原生driver插入数据之后，使用下面的查询语句，不会报错，仍然能转化为TestUser对象。
		//只是其中的属性若存在则赋值，否则为空
		Query searchUserQuery = new Query(Criteria.where("name").is("姓名"));
		TestUser queryUser = mongoTemplate.findOne(searchUserQuery, TestUser.class);
		resultMap.put("msg", queryUser);
		
		writeMeessage(resultMap);
		return null;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
}
