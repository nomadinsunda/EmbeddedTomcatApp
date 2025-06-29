package com.intheeast.dao.hbn;

import org.springframework.stereotype.Repository;

import com.intheeast.dao.AbstractHbnDao;
import com.intheeast.dao.UserMessageDao;
import com.intheeast.entity.UserMessage;

@Repository("userMessageDao")
public final class HbnUserMessageDao extends AbstractHbnDao<UserMessage>
	implements UserMessageDao {

	public HbnUserMessageDao(Class<UserMessage> entityClass) {
		super(entityClass);
		// TODO Auto-generated constructor stub
	}
	
}