<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sample web application: MarkLogic (Test of Visualization Widget)</title>
    <link href="/bower/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/app.css" rel="stylesheet">
    <link media="screen, print" href="/visualization-widgets/lib/viz/chart/chart.css" rel="stylesheet" type="text/css">

    <style type="text/css">
        .widget {
            width: 500px;
            display: block;
            float: left;
        }
    </style>
</head>

<body>
    <div id="yearDistributionContainer" class="widget"></div>

    <!-- external library dependencies -->
    <script src="/visualization-widgets/lib/external/jquery-1.7.1.min.js" type="text/javascript"></script>
    <script src="/visualization-widgets/lib/external/highcharts.js" type="text/javascript"></script>
    <!-- internal widget framework library -->
    <script src="/visualization-widgets/lib/controller.js" type="text/javascript"></script>
    <script src="/visualization-widgets/lib/widget.js" type="text/javascript"></script>
    <script src="/visualization-widgets/lib/viz/chart/chart.js" type="text/javascript"></script>
    <script type="text/javascript">
        ML.controller.init({proxy: "/proxy/search"});
        var yearConfig = {
            constraint: 'Type',
            constraintType: 'range-unbucketed',
            dataType: 'xs:string',
            title: 'Type',
            dataLabel: 'Type'
        };
        ML.chartWidget('yearDistributionContainer', 'bar', yearConfig);
        ML.controller.loadData();
    </script>
</body>
</html>
