package com.intheeast.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.intheeast.dao.PostDao;
import com.intheeast.entity.Post;
import com.intheeast.service.DefaultTextFilter;
import com.intheeast.service.EntityCallback;
import com.intheeast.service.PostService;

@Service
@Transactional //boolean readOnly() default false;
public class PostServiceImpl implements PostService {
    @Inject 
    private DefaultTextFilter textFilter;
    
    @Autowired
    private PostDao postDao;


    public DefaultTextFilter getTextFilter() { return textFilter; }

    public void setTextFilter(DefaultTextFilter filter) { this.textFilter = filter; }
    
//    @Override
//    public Post findById(Long id) {
//        return postDao.findById(id);
//    }

    
    @Override
    public void post(final Post post, final EntityCallback<Post> callback) {
        preparePost(post);
        callback.post(post);
    }

    private void preparePost(final Post post) {
        post.setWeb(cleanupWebUrl(post.getWeb()));
        post.setText(textFilter.filter(post.getText()));
    }

    private String cleanupWebUrl(String webUrl) {
        try {
            return UriComponentsBuilder.fromUriString(webUrl)
                .build().toUriString();
        } catch (Exception e) {
            // URL이 잘못된 경우 로그를 기록하거나 기본값을 반환
            return webUrl;  // 기본적으로 원래 URL을 반환하거나 에러 처리
        }
    }

    @Transactional(readOnly=true)
    @Override
    public Post findById(Long id, final EntityCallback<Post> callback) {
        return callback.findById(id);
    }

    @Transactional(readOnly=true)
    @Override
    public List<Post> findAll(final EntityCallback<Post> callback) {
        return postDao.findAllWithComments();//callback.findAll();
    }

    @Override
    public void delete(Post post, final EntityCallback<Post> callback) {
        callback.delete(post);
    }
    
    // 페이지별 게시글 조회
    @Transactional(readOnly=true)
    @Override
    public List<Post> findPostsByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return postDao.findPostsByPage(offset, pageSize); // 페이징 처리
    }

    // 전체 게시글 개수 조회
    @Transactional(readOnly=true)
    @Override
    public long countPosts() {
        return postDao.countPosts();
    }
    
    @Transactional(readOnly=true)
    @Override
    public List<Post> searchPostsByName(String name, int page, int pageSize) {
        return postDao.searchPostsByName(name, page, pageSize);
    }

    @Transactional(readOnly=true)
    @Override
    public long countPostsByName(String name) {
        return postDao.countPostsByName(name);
    }

    
	@Override
	public void update(Post entity, EntityCallback<Post> callback) {
		callback.update(entity);
	}
    
    
}