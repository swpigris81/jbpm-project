1：将该包中文件编译之后打包成jar文件，打包方法：
   a) 将编译之后的class文件按照目录结构放在一个新建文件夹中
   b) 从cmd中到达该新建文件夹中使用下面的命令即可
   c) 打包命令：jar cvf atomikos-tomcat-beanfactory.jar *
2: 将第一步中打包的jar文件放在tomcat/lib目录下即可。


atomikos3.8+tomcat6+hibernate3+JBPM5.3配置数据源JNDI步骤
1: 下载所需文件以及jar包：
   由于atomikos3.8默认有JMS框架，因此需要JMS的相关jar包。
   下载atomikos3.8，将其中AtomikosTransactionsEssentials-3.8.0\dist目录下的所有jar放在tomcat/lib目录
   下载jta-1.1.jar放在tomcat/lib目录
   编译文件，并且打包atomikos-tomcat-beanfactory.jar放在tomcat/lib目录
   下载数据库驱动（MySql5）mysql-connector-java-5.1.7-bin.jar放在tomca/lib目录
   下载JMS相关jar包放在tomcat/lib目录
   由于atomikos3.8采用common-logging记录日志，因此需要相关jar包：commons-logging-1.1.1.jar放在tomcat/lib目录
2：修改server.xml文件
   在Context节点中添加以下内容：
   <Listener className="com.atomikos.tomcat.ContextLifecycleListener" webappName="WebServiceApp" />
   <Resource auth="Container" name="jdbc/jbpm-ds" uniqueResourceName="jdbc/jbpm-ds" factory="com.atomikos.tomcat.AtomikosTomcatFactoryFactory" maxPoolSize="100" minPoolSize="10" xaProperties.user="jbpm5" xaProperties.password="jbpm5" 
      		xaProperties.url="jdbc:mysql://localhost:3306/jbpm5?useUnicode=true&amp;characterEncoding=UTF-8" xaDataSourceClassName="com.mysql.jdbc.jdbc2.optional.MysqlXADataSource"
      		type="com.atomikos.jdbc.AtomikosDataSourceBean" xaProperties.databaseName="jbpm5" xaProperties.serverName="localhost" xaProperties.port="3306"
      		url="jdbc:mysql://localhost:3306/jbpm5?useUnicode=true&amp;characterEncoding=UTF-8" driverClassName="com.mysql.jdbc.Driver">
   </Resource>
   <Transaction name="UserTransaction" auth="Container" type="javax.transaction.UserTransaction" factory="com.atomikos.icatch.jta.UserTransactionFactory"/>
3：添加jta.properties文件，该文件必须部署在classpath根目录下。
4：修改persistence.xml文件
   <jta-data-source>java:comp/env/jdbc/jbpm-ds</jta-data-source>（此处必须是java:comp/env/开头）
   hibernate.transaction.manager_lookup_class = com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup
   hibernate.transaction.factory_class = com.atomikos.icatch.jta.hibernate3.AtomikosJTATransactionFactory（或者是org.hibernate.transaction.JTATransactionFactory）
5：修改web.xml文件，添加以下内容：
   <resource-ref>
    <description>This is a database connection</description>
    <res-ref-name>jdbc/jbpm-ds</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
  </resource-ref>
6：在调用transaction的时候，必须开启事务，完成之后，必须关闭事务，否则异常

refer to: http://www.atomikos.com/Documentation/Tomcat6Integration35Lifecycle#AtomikosTomcatFactoryFactory_jav