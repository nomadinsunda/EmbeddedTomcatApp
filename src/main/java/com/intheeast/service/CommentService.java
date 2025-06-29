package com.intheeast.service;

import java.util.List;

import com.intheeast.entity.Comment;
import com.intheeast.entity.Post;
import com.intheeast.service.impl.CommentCallbackImpl;

public interface CommentService extends EntityService<Comment, EntityCallback<Comment>>{

	List<Comment> findAllCommentsByPost(Post post, final EntityCallback<Comment> callback);	
}