<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Post List</title>
</head>
<body>
    <h1>Posts</h1>

    <!-- 검색 폼 -->
    <form action="${pageContext.request.contextPath}/posts/search" method="get">
        <input type="text" name="search" value="${param.search}" placeholder="Search by name">
        <button type="submit">Search</button>
    </form>

    <!-- 전체 게시글 보기 버튼 (검색 결과 후에만 나타남) -->
    <c:if test="${not empty param.search}">
        <a href="${pageContext.request.contextPath}/posts" class="btn">View All Posts</a>
    </c:if>

    <!-- 새 게시글 생성 버튼 -->
    <a href="${pageContext.request.contextPath}/posts/new" class="btn">Create New Post</a>

    <!-- 게시글 목록 -->
    <table border="1" cellpadding="10">
        <thead>
            <tr>
                <th>Title</th>
                <th>Author</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="post" items="${posts}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/posts/${post.id}">${post.title}</a></td>
                    <td>${post.name}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/posts/${post.id}" class="btn">View</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- 페이지네이션 -->
    <div class="pagination">
        <c:set var="totalPages" value="${totalPages < 1 ? 1 : totalPages}" /> <!-- totalPages가 1보다 작으면 1로 설정 -->
        <c:if test="${totalPages >= 1}">
            <c:if test="${currentPage > 1}">
                <a href="${pageContext.request.contextPath}/posts?page=${currentPage - 1}">&laquo; Previous</a>
            </c:if>

            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <span>${i}</span> <!-- 현재 페이지는 링크 없이 표시 -->
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/posts?page=${i}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <a href="${pageContext.request.contextPath}/posts?page=${currentPage + 1}">Next &raquo;</a>
            </c:if>
        </c:if>
    </div>

</body>
</html>