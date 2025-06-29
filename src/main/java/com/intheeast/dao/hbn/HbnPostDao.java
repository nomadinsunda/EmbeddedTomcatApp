package com.intheeast.dao.hbn;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.intheeast.dao.AbstractHbnDao;
import com.intheeast.dao.PostDao;
import com.intheeast.entity.Post;
import com.intheeast.entity.QComment;
import com.intheeast.entity.QPost;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository("postDao")
public class HbnPostDao extends AbstractHbnDao<Post> 
	implements PostDao{
	
	@PersistenceContext
    private EntityManager entityManager;
	
	public HbnPostDao() {
		super(Post.class);
	}	
	
	public HbnPostDao(Class<Post> entityClass) {
		super(entityClass);
		// TODO Auto-generated constructor stub
	}
	
	private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
	
	/**
     * Fetch join을 사용하여 Post와 관련된 Comment들을 함께 가져오는 메서드.
     */
    @SuppressWarnings("unchecked")
    public List<Post> findAllWithComments() {
//        Session session = getSession(); // Hibernate 세션 가져오기
        
//        JPAQueryFactory queryFactory = new JPAQueryFactory(getSession());

        QPost post = QPost.post;
        QComment comment = QComment.comment;
        return queryFactory.selectFrom(post)
                           .leftJoin(post.commentList, comment).fetchJoin() // commentList를 left join으로 가져옴
                           .fetch(); // 결과를 리스트로 반환

        // JPQL 쿼리에서 fetch join을 사용하여 Post와 연결된 Comment 목록을 함께 가져옴
//        Query<Post> query = session.createQuery(
//            "SELECT p FROM Post p LEFT JOIN FETCH p.commentList", Post.class
//        );
//
//        return query.getResultList();
    }
    
    @Override
    public List<Post> findPostsByPage(int page, int pageSize) {
//        Session session = getSession();
//        Query<Post> query = session.createQuery("FROM Post", Post.class);
//        query.setFirstResult(offset);
//        query.setMaxResults(pageSize);
//        return query.getResultList();
        
//        JPAQueryFactory queryFactory = new JPAQueryFactory(getSession());

    	if (page < 1) {
    		page = 1;
    	}
    	
        QPost post = QPost.post;
        return queryFactory
                .selectFrom(post)
                .orderBy(post.creationDate.desc()) // 원하는 정렬 방식 적용
                .offset((page - 1) * pageSize) // 페이지 오프셋 계산
                .limit(pageSize) // 한 페이지당 몇 개의 포스트를 가져올지 제한
                .fetch();
    }

    @Override
    public long countPosts() {
//        Session session = getSession();
//        Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Post p", Long.class);
//        return query.uniqueResult().intValue();
    	
    	QPost post = QPost.post;

        return queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();
    }
    
    // 검색 쿼리 추가
//    @Override
//    public List<Post> searchPostsByName(String name, int page, int pageSize) {
//        QPost post = QPost.post;
//        JPAQuery<Post> query = new JPAQuery<>(getSession());
//        
//        query.from(post)
//             .where(post.name.containsIgnoreCase(name))
//             .offset((page - 1) * pageSize)
//             .limit(pageSize);
//
//        return query.fetch();
//    }
//
//    @Override
//    public long countPostsByName(String name) {
//        QPost post = QPost.post;
//        JPAQuery<Long> query = new JPAQuery<>(getSession());
//
//        return query.select(post.count())
//                    .from(post)
//                    .where(post.name.containsIgnoreCase(name))
//                    .fetchOne();
//    }
    
//    @SuppressWarnings("unchecked")
    public List<Post> searchPostsByName(String search, int page, int pageSize) {
    	QPost post = QPost.post;
    	
        JPAQueryFactory queryFactory = new JPAQueryFactory(getSession());

        return queryFactory
                .selectFrom(post)
                .where(post.name.containsIgnoreCase(search)) // 검색어가 포함된 name
                .offset((page - 1) * pageSize) // 페이지네이션: 시작 위치 설정
                .limit(pageSize) // 페이지 크기
                .fetch(); // 결과 리스트 반환
    }
    
    @SuppressWarnings("deprecation")
	public long countPostsByName(String search) {
    	QPost post = QPost.post;
        JPAQueryFactory queryFactory = new JPAQueryFactory(getSession());

        return queryFactory
                .selectFrom(post)
                .where(post.name.containsIgnoreCase(search)) // 검색어가 포함된 name
                .fetchCount(); // 개수 반환
    }

}
