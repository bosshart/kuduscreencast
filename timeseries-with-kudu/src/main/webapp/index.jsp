<%@ page language="java" contentType="text/html; charset=US-ASCII"
pageEncoding="US-ASCII"%>
<html>


<head>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
</head>

<body>
<div id="myChart" style="width: 480px; height: 400px;"><!-- Chart will be drawn inside this DIV --></div>
<script>

    function makeplot() {
      Plotly.d3.csv("http://<%=request.getServerName() %>:8080/stockdata/", function(data){ processData(data, true) } );
    };

    var layout = {
      yaxis: {range: [9500, 10000]},
      title: 'Plotting Largest Orders from Kudu!'
    };

    function processData(allRows, firstRun) {
      /** to do: use Plotly.restyle('myChart', traces); to update instead of refresh chart **/
      var plotCreated = false;
      var x = [], y = [], currentSymbol = '';
      for (var i=0; i<allRows.length; i++) {
        row = allRows[i];
        if(row['symbol'] != currentSymbol) {
            if(currentSymbol != '') {
                var traces = [{
                    x: x,
                    y: y,
                    name: currentSymbol
                  }];
                if(!plotCreated) {
                    Plotly.newPlot('myChart', traces, layout);
                    plotCreated=true;
                } else {
                    Plotly.addTraces('myChart', traces);

                }
            }
            currentSymbol = row['symbol'];
            x = [];
            y = [];
        }
        x.push( row['timestamp'] );
        y.push( row['orderqty'] );
      }
    };

    makeplot();

    window.setInterval(function(){
        makeplot();
    }, 5000);

  </script>
</body>
</html>