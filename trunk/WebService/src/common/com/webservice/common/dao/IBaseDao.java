package com.webservice.common.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessResourceFailureException;

import com.webservice.common.util.SQLParameter;

public interface IBaseDao {
    /**
     * <p>Discription:[使用Hql语句查询数据]</p>
     * @param hql
     * @param params
     * @return
     * @author: 代超
     * @update: 2011-6-2 代超[变更描述]
     */
    public List queryByHQL(String hql, Object[] params);
    /**
     * <p>Discription:[使用Sql查询数据]</p>
     * @param sql
     * @param params
     * @return
     * @author: 代超
     * @update: 2011-6-2 代超[变更描述]
     */
    public List queryBySQL(String sql, Object[] params);
    
    /**
     * <p>Discription:[使用Hql语句查询分页数据]</p>
     * @param hql
     * @param params
     * @return
     * @author: 代超
     * @update: 2011-6-2 代超[变更描述]
     */
    public List queryPageByHQL(String hql, Object[] params, int start, int limit);
    
    /**
     * <p>Discription:[使用Sql查询分页数据]</p>
     * @param sql
     * @param params
     * @return
     * @author: 代超
     * @update: 2011-6-2 代超[变更描述]
     */
    public List queryPageBySQL(String sql, Object[] params, int start, int limit);
    /**
     * <p>Discription:[调用jdbc执行sql语句,用于insert/update/delete,需要自行组装sql语句, 已过时，请参考: <code>com.webservice.common.dao.IBaseDao#excuteBySQL</code>]</p>
     * @deprecated
     * @param sql
     * @return
     * @throws DataAccessResourceFailureException
     * @throws HibernateException
     * @throws IllegalStateException
     * @throws SQLException
     * @author: 代超
     * @update: 2011-7-3 代超[变更描述]
     */
    public int excuteSQL(String sql, Object[] params) throws DataAccessResourceFailureException, HibernateException, IllegalStateException, SQLException;
    
    /**
     * <p>Discription:[调用Hibernate自带的jdbc执行sql语句,用于insert/update/delete,需要自行组装sql语句]</p>
     * @param sql
     * @param parameter
     * @return
     * @author 大牙-小白
     * @update 2012-8-30 大牙-小白 [变更描述]
     */
    public Integer excuteBySQL(final String sql, final Object parameter[]);
    /**
     * <p>Discription:[执行HQL的insert/update/delete]</p>
     * @param hql hql语句
     * @param parameter 参数
     * @return
     * @author:大牙
     * @update:2012-10-29
     */
    public Integer excuteByHQL(final String hql, final Object parameter[]);
    /**
     * <p>Discription:[调用jdbc执行复杂sql的查询]</p>
     * @param sql sql语句
     * @param start 分页查询起始
     * @param limit 分页查询终止
     * @param params 查询的其他参数
     * @return
     * @throws SQLException
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List queryListByPageByJDBC(String sql, int start, int limit, Object[] params) throws SQLException;
    /**
     * <p>Discription:[使用Hibernate中自带的JDBC查询]</p>
     * @param sql sql语句
     * @param start 分页查询起始
     * @param limit 分页查询终止
     * @param params 查询参数数组，参考SQLParameter类说明
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List<Object []> queryListByPageByJDBC(String sql, int start, int limit, SQLParameter[] params);
    
    /**
     * <p>Discription:[JDBC批量执行SQL语句, 已过时, 请参考: <code>com.webservice.common.dao.IBaseDao#excuteBySQL</code>]</p>
     * @deprecated
     * @param sql
     * @param paramList 一个数组集合
     * @return
     * @throws Exception
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int excuteSQLBatch(String sql, List<Object[]> paramList) throws Exception;
    /**
     * <p>Discription:[使用Hibernate自带的批量执行sql语句]</p>
     * @param sql : update Table ref set ref.rulecode = ? where ref.rscerefcode = ?
     * @param paramList
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public int excuteSqlBatchWithHibernate(String sql, Object ... paramList);
    /**
     * <p>Discription:[分页查询, 使用Hibernate的传值方式即：where a = :a]</p>
     * @param hql
     * @param params 参数-值 集合
     * @param start
     * @param limit
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List queryPageByHQL(String hql, Map<String, Object> params, int start, int limit);
    /**
     * <p>Discription:[分页查询, 使用Hibernate的传值方式即：where a = :a]</p>
     * @param sql
     * @param params
     * @param start
     * @param limit
     * @return
     * @author:[代超]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List queryPageBySQL(String sql, Map<String, Object> params, int start, int limit);
    /**
     * <p>Discription:[根据指定条件分页查询]</p>
     * @param instance 含有指定条件的POJO对象
     * @param start
     * @param limit
     * @return
     * @author:[创建者中文名字]
     * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public List findByExample(Object instance, int start, int limit);
}
