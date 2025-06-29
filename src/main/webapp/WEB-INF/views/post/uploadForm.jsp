<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>File Upload</title>
</head>
<body>
    <h1>Upload File for Post: ${post.title}</h1>

    <form action="${pageContext.request.contextPath}/posts/${post.id}/files/upload" method="post" enctype="multipart/form-data">
        <input type="file" name="file" />
        <button type="submit">Upload</button>
    </form>

    <c:if test="${not empty message}">
        <p>${message}</p>
    </c:if>

    <c:if test="${not empty error}">
        <p>${error}</p>
    </c:if>
</body>
</html>