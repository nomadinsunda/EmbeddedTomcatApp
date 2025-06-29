<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Post Details</title>
</head>
<body>
    <h1>${post.title}</h1>
    <p>${post.content}</p>

    <h2>Files</h2>
    <c:if test="${not empty post.files}">
        <ul>
            <c:forEach var="file" items="${post.files}">
                <li>
                    <a href="${pageContext.request.contextPath}/posts/${post.id}/files/download/${file.id}">${file.fileName}</a>
                </li>
            </c:forEach>
        </ul>
    </c:if>

    <a href="${pageContext.request.contextPath}/posts/${post.id}/files/upload">Upload a File</a>
</body>
</html>