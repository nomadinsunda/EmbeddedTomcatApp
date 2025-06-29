package com.intheeast.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intheeast.dao.CommentDao;
import com.intheeast.entity.Comment;
import com.intheeast.entity.Post;
import com.intheeast.service.CommentService;
import com.intheeast.service.DefaultTextFilter;
import com.intheeast.service.EntityCallback;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    @Inject private DefaultTextFilter textFilter;
    @Inject private CommentMailSender mailSender;
    @Inject private CommentDao commentDao;  // CommentDao 주입


    @Inject private EntityCallback<Comment> commentCallbackImpl; // EntityCallback을 사용한 의존성 주입
    
    public DefaultTextFilter getTextFilter() { return textFilter; }

    public void setTextFilter(DefaultTextFilter filter) { this.textFilter = filter; }

    @Override
    public void post(final Comment comment, final EntityCallback<Comment> callback) {
        prepareComment(comment);
        callback.post(comment);
        mailSender.sendNotificationEmail(comment);
    }

    private void prepareComment(final Comment comment) {
        comment.setText(textFilter.filter(comment.getText()));
    }

    @Transactional(readOnly=true)
    @Override
    public Comment findById(Long id, final EntityCallback<Comment> callback) {
        return callback.findById(id);
    }

    @Transactional(readOnly=true)
    @Override
    public List<Comment> findAll(final EntityCallback<Comment> callback) {
        return callback.findAll();
    }

    @Override
    public void delete(Comment comment, final EntityCallback<Comment> callback) {
        callback.delete(comment);
    }

    @Transactional(readOnly=true)
    @Override
    public List<Comment> findAllCommentsByPost(Post post, final EntityCallback<Comment> callback) {
    	 return commentDao.findAllCommentsByPost(post);
    }

	@Override
	public void update(Comment entity, EntityCallback<Comment> callback) {
		callback.update(entity);		
	}

}