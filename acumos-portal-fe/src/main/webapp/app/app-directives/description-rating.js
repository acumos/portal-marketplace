app.directive(
    'descriptionRating',
    function() {
          return {
                restrict : 'EA',
                scope : {
                	ratedescriptioncheck : '='
                },
                 template  : '<div class="rating clear_both" ng-show="ratedescriptioncheck > 0">'
                    + '<span class="font600"> Description Rating: &nbsp; &nbsp; </span>' 
                    + '<span><i class="fa fa-star " ng-class="{\'fa-star1\': !(ratedescriptioncheck > 100) }"></i></span>'
                    + '<span><i class="fa fa-star " ng-class="{\'fa-star1\': !(ratedescriptioncheck > 200) }"></i></span>'
                    + '<span><i class="fa fa-star " ng-class="{\'fa-star1\': !(ratedescriptioncheck > 300) }"></i></span>'
                    + '<span><i class="fa fa-star " ng-class="{\'fa-star1\': !(ratedescriptioncheck > 400) }"></i></span>'
                    + '<span><i class="fa fa-star " ng-class="{\'fa-star1\': !(ratedescriptioncheck > 500) }"></i></span>'
                    + '</div>'

          }
    });
