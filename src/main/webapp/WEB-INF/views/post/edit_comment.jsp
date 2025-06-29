<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Comment</title>
</head>
<body>
    <h1>Edit Comment</h1>
    <form:form method="post" modelAttribute="comment" action="${pageContext.request.contextPath}/posts/${post.id}/comments/${comment.id}/edit">
        <label for="name">Name:</label>
        <form:input path="name" />
        <br/>
        
        <label for="email">Email:</label>
        <form:input path="email" />
        <br/>
        
        <label for="text">Comment:</label>
        <form:textarea path="text" />
        <br/>
        
        <button type="submit">Update Comment</button>
    </form:form>
    
    <a href="${pageContext.request.contextPath}/posts/${post.id}">Cancel</a>
</body>
</html>