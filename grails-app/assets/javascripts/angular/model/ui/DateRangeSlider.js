//import Aggregation from './Aggregation';

class DateRangeSlider { //extends Aggregation {
    constructor( min, max ) {
    	//super( {} );
    	//this.initialSlider = undefined;
    	//this.initialSlider = initialSlider;
    	//min and max are either JSON objects OR primitive values
    	//either as min and max agg from ES OR gte/lte properties of a RangeFilter (of a recent/save query), examples follow
    	/*
    	agg example
    	{
    		"value_as_string": "2005-02-19 00:00",
    		"value": 1108771200000
    	}
    	RangeFilter details example; in this case, min and max are primitives
    	min value of 1266956980000,
		max value of 1435301490000
    	*/
		
    	this.minValue = ( min instanceof Object ) ? min.value : min;
		this.maxValue = ( max instanceof Object ) ? max.value : max;
		
		//let flr = min.value;
		//let cel = max.value;
		
		let flr = this.minValue;
		let cel = this.maxValue;

		this.options = {
				floor: flr,
				ceil: cel,
				step: 10000,
				noSwitching: true,
				translate: function(millis) {
					if (millis != null) {
						let date = new Date(millis);
						return date.toDateString();
					}
					return '';
				},
				hideLimitLabels: false
		}
		this.filtered = false;
    }
    
    updateAggregationFilter( aggregation ) {
    	//TODO move this method to client side Aggregation.js object?
    	aggregation.filters.min = this.minValue;
    	aggregation.filters.max = this.maxValue;
    	aggregation.initialSlider.filtered = true;
//alert(JSON.stringify(aggregation,null,'\t'));
    }
    
    maxUp( aggregation ) {
    	this.maxValue = this.maxValue + 86400*1000;
        this.updateAggregationFilter( aggregation );
    }
    maxDown( aggregation ) {
    	this.maxValue = this.maxValue - 86400*1000;
        this.updateAggregationFilter( aggregation );
    }
    minUp( aggregation ) {
    	this.minValue = this.minValue + 86400*1000;
        this.updateAggregationFilter( aggregation );
    }
    minDown( aggregation ) {
    	this.minValue = this.minValue - 86400*1000;
        this.updateAggregationFilter( aggregation );
    }
    
    fineAdjust( event, aggregation ) {
    	//alert(event.keyCode);
    	switch (event.keyCode) {
        case 37:
            //left arrow;
            this.minDown( aggregation );
            break;
        case 38:
            //up arrow
            this.minUp( aggregation );
            break;
        case 39:
            //right arrow
        	this.maxUp( aggregation );
            break;
        case 40:
            //down arrow
            this.maxDown( aggregation );
            break;
    	}
    }

}

export default DateRangeSlider;