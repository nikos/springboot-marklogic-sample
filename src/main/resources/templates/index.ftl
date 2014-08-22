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
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="#">MarkLogic Demo</a>

            <div class="nav-collapse collapse">
                <ul class="nav" role="navigation">
                    <li>
                        <a href="#/products">Products</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

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