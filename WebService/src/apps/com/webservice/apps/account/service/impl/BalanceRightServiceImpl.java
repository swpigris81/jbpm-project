package com.webservice.apps.account.service.impl;

import java.util.Collection;
import java.util.List;

import com.webservice.apps.account.bean.BalanceRight;
import com.webservice.apps.account.dao.IBalanceRightDAO;
import com.webservice.apps.account.service.IBalanceRightService;
import com.webservice.common.dao.impl.BaseDao;

public class BalanceRightServiceImpl implements IBalanceRightService {

	private IBalanceRightDAO rightDao;
	private BaseDao baseDao;

	public IBalanceRightDAO getRightDao() {
		return rightDao;
	}

	public void setRightDao(IBalanceRightDAO rightDao) {
		this.rightDao = rightDao;
	}

	public BaseDao getBaseDao() {
        return baseDao;
    }

    public void setBaseDao(BaseDao baseDao) {
        this.baseDao = baseDao;
    }

    @Override
	public void delete(BalanceRight persistentInstance) {
		this.rightDao.delete(persistentInstance);
	}

	@Override
	public void deleteAll(Collection<BalanceRight> persistentInstance) {
		this.rightDao.deleteAll(persistentInstance);
	}

	@Override
	public List findByExample(BalanceRight instance) {
		return this.rightDao.findByExample(instance);
	}

	@Override
	public BalanceRight findById(String id) {
		return this.rightDao.findById(id);
	}

	@Override
	public void save(BalanceRight transientInstance) {
		this.rightDao.save(transientInstance);
	}

	@Override
	public void saveOrUpdate(BalanceRight instance) {
		this.rightDao.saveOrUpdate(instance);
	}

	@Override
	public List findBalanceRightByPage(int start, int limit, String[] params) {
		return this.rightDao.findBalanceRightByPage(start, limit, params);
	}

}
