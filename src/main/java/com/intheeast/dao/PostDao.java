package com.intheeast.dao;

import java.util.List;

import com.intheeast.entity.Post;

public interface PostDao extends Dao<Post> {
    // 추가적인 Post 관련 메서드가 필요하다면 여기에 선언합니다.
	
	List<Post> findAllWithComments();
	
	List<Post> findPostsByPage(int offset, int pageSize); // 페이징 처리된 게시글 조회
    long countPosts(); // 전체 게시글 개수 조회
    
    // 검색 기능 추가
    List<Post> searchPostsByName(String name, int page, int pageSize);

    long countPostsByName(String name);
}
