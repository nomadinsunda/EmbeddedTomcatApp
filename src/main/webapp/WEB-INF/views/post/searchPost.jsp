<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Post Search</title>
</head>
<body>
    <h1>Search Posts by Name</h1>
    
    <!-- 검색 폼 -->
    <form action="searchPost" method="get">
        <input type="text" name="name" placeholder="Enter post name" />
        <button type="submit">Search</button>
    </form>
    
    <hr/>

    <!-- 검색 결과 출력 -->
    <c:if test="${not empty posts}">
        <h2>Search Results:</h2>
        <ul>
            <c:forEach var="post" items="${posts}">
                <li>${post.name}: ${post.content}</li>
            </c:forEach>
        </ul>
    </c:if>
    
    <c:if test="${empty posts}">
        <p>No posts found.</p>
    </c:if>

</body>
</html>