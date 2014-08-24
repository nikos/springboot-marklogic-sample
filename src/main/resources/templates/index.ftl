<!DOCTYPE html>
<html ng-app="MarkLogicRESTClientApp">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sample web application: MarkLogic with SpringBoot</title>
    <link href="/bower/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bower/angular-toastr/dist/angular-toastr.min.css" rel="stylesheet">
    <style>
        body {
            padding-top: 60px;
        }
    </style>
</head>

<body>
<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">MarkLogic Demo</a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Link</a></li>
                <li><a href="#">Link</a></li>
            </ul>
            <form class="navbar-form navbar-left" role="search">
                <div class="form-group">
                    <input type="text" class="form-control" placeholder="Search">
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
            </form>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>

<div class="container">
    <!-- this is where content will be injected by Angular -->
    <div ng-view></div>
</div>

<script src="/bower/angular/angular.min.js"></script>
<script src="/bower/angular-route/angular-route.min.js"></script>
<script src="/bower/angular-resource/angular-resource.min.js"></script>
<script src="/bower/angular-toastr/dist/angular-toastr.min.js"></script>
<script src="/js/app.js"></script>
</body>
</html>