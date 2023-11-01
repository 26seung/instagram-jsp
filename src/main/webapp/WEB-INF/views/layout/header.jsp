<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core"%> <%@ taglib prefix="sec"
uri="http://www.springframework.org/security/tags"%>

<sec:authorize access="isAuthenticated()">
  <sec:authentication property="principal" var="principal" />
</sec:authorize>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>PHOTOBUNDLE</title>

    <!-- 제이쿼리 -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <!-- Style -->
    <link rel="stylesheet" href="/css/style.css" />
    <link rel="stylesheet" href="/css/story.css" />
    <link rel="stylesheet" href="/css/popular.css" />
    <link rel="stylesheet" href="/css/profile.css" />
    <link rel="stylesheet" href="/css/upload.css" />
    <link rel="stylesheet" href="/css/update.css" />
    <link rel="shortcut icon" href="/images/icon.jpg" />

    <!-- Fontawesome -->
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.8.2/css/all.min.css"
    />
    <!-- Fonts -->
    <link
      href="https://fonts.googleapis.com/css2?family=Montserrat:wght@100;200;300;400;500;600;700&display=swap"
      rel="stylesheet"
    />
  </head>
  <link
    rel="stylesheet"
    href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0"
  />
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link
    href="https://fonts.googleapis.com/css2?family=Playpen+Sans:wght@600&display=swap"
    rel="stylesheet"
  />
  <body>
    <!-- principalId 담아두는 곳-->
    <input type="hidden" id="principalId" value="${principal.user.id}" />

    <header class="header">
      <div class="container">
        <a href="/" class="logo">
          <h1>PHOTOBUNDLE</h1>
          <!-- <img src="/images/instagram.png" alt="" /> -->
        </a>
        <nav class="navi">
          <ul class="navi-list">
            <li class="navi-item">
              <a href="/">
                <i class="fas fa-home"></i>
              </a>
            </li>
            <li class="navi-item">
              <a href="/image/popular">
                <i class="far fa-compass"></i>
              </a>
            </li>
            <li class="navi-item">
              <a href="/user/${principal.user.id}">
                <i class="far fa-user"></i>
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  </body>
</html>
