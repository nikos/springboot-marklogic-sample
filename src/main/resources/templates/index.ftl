<!doctype html>
<html ng-app>
<head>
    <meta charset="utf-8">
    <title>Sample web application: MarkLogic with SpringBoot</title>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.21/angular.min.js"></script>
    <script src="/js/app.js"></script>
</head>

<body>
<p>
    Huhu
</p>

<div ng-controller="Hello">
    <p>The ID is {{greeting.id}}</p>
    <p>The content is {{greeting.content}}</p>
</div>

Date: ${time?date}
<br>
Time: ${time?time}
<br>
Message: ${message}

</body>
</html>