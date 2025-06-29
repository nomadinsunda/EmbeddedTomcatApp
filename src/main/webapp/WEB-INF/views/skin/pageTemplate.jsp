<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><decorator:title default="My Web Application"/></title>
    
    <!-- 공통 CSS 파일을 불러오는 부분 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" />
</head>
<body>
    <header>
        <!-- 공통 헤더 부분 -->
        <h1>My Web Application</h1>
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/posts">Post</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <!-- 동적으로 변경되는 콘텐츠 부분 -->
        <decorator:body />
    </main>

    <footer>
        <!-- 공통 푸터 부분 -->
        <p>&copy; 2024 My Web Application. All rights reserved.</p>
    </footer>
</body>
</html>