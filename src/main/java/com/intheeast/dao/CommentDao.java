package com.intheeast.dao;

import java.util.List;

import com.intheeast.entity.Comment;
import com.intheeast.entity.Post;

public interface CommentDao extends Dao<Comment>{

    List<Comment> findAllCommentsByPost(Post post);

}
