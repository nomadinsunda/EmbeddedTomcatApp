package com.intheeast.dao.hbn;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.intheeast.dao.AbstractHbnDao;
import com.intheeast.dao.CommentDao;
import com.intheeast.entity.Comment;
import com.intheeast.entity.Post;
import com.intheeast.entity.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository("commentDao")
public final class HbnCommentDao extends AbstractHbnDao<Comment> 
	implements CommentDao{
	
	// 기본 생성자 추가
    public HbnCommentDao() {
        super(Comment.class); // 여기서 Comment 클래스 타입을 명시적으로 전달
    }

	public HbnCommentDao(Class<Comment> entityClass) {
		super(entityClass);
		// TODO Auto-generated constructor stub
	}
	
	// 게시글에 해당하는 모든 댓글 조회
    @SuppressWarnings("unchecked")
    @Override
    public List<Comment> findAllCommentsByPost(Post post) {
    	
    	JPAQueryFactory queryFactory = new JPAQueryFactory(getSession());

    	QComment comment = QComment.comment;
        return queryFactory.selectFrom(comment)
                           .where(comment.post.eq(post))
                           .fetch();
        
        // HQL을 사용하여 Post에 속한 모든 댓글을 조회
//        return getSession()
//                .createQuery("FROM Comment c WHERE c.post = :post")
//                .setParameter("post", post)
//                .getResultList();
    }

}
