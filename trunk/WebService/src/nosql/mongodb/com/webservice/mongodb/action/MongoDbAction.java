package com.webservice.mongodb.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
		Query searchUserQuery = new Query(Criteria.where("userName").is("名字2"));
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
