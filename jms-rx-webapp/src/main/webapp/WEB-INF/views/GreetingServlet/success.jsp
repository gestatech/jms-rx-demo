<html>
  <head>
    <title>Greeting</title>
  </head>
  <body>
    <p><strong>${greeting}</strong></p>
    <p><a href="${request.servletContext.contextPath}?name=${name}">again</a>
       <a href="${request.servletContext.contextPath}?">back</a>
    </p>
  </body>
</html>