package com.intheeast.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.intheeast.dao.CommentDao;
import com.intheeast.entity.Comment;
import com.intheeast.entity.Post;
import com.intheeast.service.EntityCallback;

@Service
//@Transactional(
//	propagation = Propagation.REQUIRED,
//	isolation = Isolation.DEFAULT,
//	readOnly = true)
public class CommentCallbackImpl implements EntityCallback<Comment>{

	// 아래 코드가 문제인 것 같음.
	@Inject private CommentDao commentDao;
	
//	@Transactional(readOnly = false)
	public void post(Comment comment) {
		Assert.notNull(comment, "comment can't be null");
		commentDao.save(comment);	
	}
	
	@Override
	public Comment findById(Long id) {
		return commentDao.findById(id);
	}

	@Override
	public List<Comment> findAll() {
		return commentDao.findAll();
	}

//	@Transactional(readOnly = false)
	@Override
	public void delete(Comment comment) {
		commentDao.delete(comment);
	}
	
	
	public List<Comment> findAllCommentsByPost(Post post) {
		return commentDao.findAllCommentsByPost(post);
	}

	@Override
	public void update(Comment entity) {
		commentDao.update(entity);
	}

}
