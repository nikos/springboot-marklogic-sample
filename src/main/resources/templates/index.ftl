<!DOCTYPE html>
<html ng-app="MarkLogicRESTClientApp">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sample web application: MarkLogic with SpringBoot</title>
    <link href="/bower/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bower/angular-toastr/dist/angular-toastr.min.css" rel="stylesheet">
    <link href="/bower/ng-tags-input/ng-tags-input.min.css" rel="stylesheet">
    <link href="/css/app.css" rel="stylesheet">
</head>

<body>
<div class="container">
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
                <a class="navbar-brand" href="#">MarkLogic SpringBoot Sample</a>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#">All Products</a></li>
                    <!-- li><a href="#">Link</a></li -->
                </ul>
                <form class="navbar-form navbar-right" role="search" ng-controller="ProductSearchController">
                    <div class="form-group">
                        <input type="text" ng-model="asyncProductMatches" placeholder="Search Product"
                               typeahead="productMatch for productMatch in findMatchingProducts($viewValue)"
                               typeahead-loading="loadingProductMatches" class="form-control">
                        <i ng-show="loadingProductMatches" class="glyphicon glyphicon-refresh"></i>
                    </div>
                    <!-- button type="submit" class="btn btn-default">Submit</button -->
                </form>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>

    <!-- this is where content will be injected by Angular -->
    <div ng-view></div>
</div>

<script src="/bower/angular/angular.min.js"></script>
<script src="/bower/angular-route/angular-route.min.js"></script>
<script src="/bower/angular-resource/angular-resource.min.js"></script>
<script src="/bower/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
<script src="/bower/angular-toastr/dist/angular-toastr.min.js"></script>
<script src="/bower/ng-tags-input/ng-tags-input.min.js"></script>
<script src="/js/app.js"></script>
</body>
</html>