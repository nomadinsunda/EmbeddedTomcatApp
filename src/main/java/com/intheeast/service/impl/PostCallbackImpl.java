package com.intheeast.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.intheeast.dao.PostDao;
import com.intheeast.entity.Post;
import com.intheeast.service.EntityCallback;


@Service
//@Transactional(
//	propagation = Propagation.REQUIRED,
//	isolation = Isolation.DEFAULT,
//	readOnly = true)
public class PostCallbackImpl implements EntityCallback<Post>{

	@Inject private PostDao postDao;
	
//	@Transactional(readOnly = false)
	public void post(Post post) {
		Assert.notNull(post, "post can't be null");
		postDao.save(post);			
	}

	@Override
	public Post findById(Long id) {
		return postDao.findById(id);
	}

	@Override
	public List<Post> findAll() {
		return postDao.findAll();
	}	

//	@Transactional(readOnly = false)
	@Override
	public void delete(Post post) {
		postDao.delete(post);
	}

//	@Transactional(readOnly = false)
	@Override
	public void update(Post entity) {
		postDao.update(entity);
		
	}

}
