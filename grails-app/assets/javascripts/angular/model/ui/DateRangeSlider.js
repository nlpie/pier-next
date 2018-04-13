
class DateRangeSlider {
    constructor( min, max, initialSlider ) {
    	//min and max are JSON objects returned as either a min or max agg from ES, example follows
    	/*
    	{
    		"value_as_string": "2005-02-19 00:00",
    		"value": 1108771200000
    	}
    	*/
    	this.minValue = min.value;
		this.maxValue = max.value;
		let flr =  initialSlider ? initialSlider.options.floor : min.value;
		let cel =  initialSlider ? initialSlider.options.ceil : max.value;
		//this.filterOffMinValue = min.value;
		//this.filterOffMaxValue = max.value;
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
    	//TODO move this method to client side Aggregation.js object
    	aggregation.filters.min = this.minValue;
    	aggregation.filters.max = this.maxValue;
    	aggregation.initialSlider.filtered = true;
//alert(JSON.stringify(aggregation,null,'\t'));
    }
    
    up( aggregation ) {
    	this.maxValue = this.maxValue = this.maxValue + 86400*1000;
    }
    
    reset( aggregation ) {
    	this.minValue = this.options.floor;
    	this.maxValue = this.options.ceil;
    	aggregation.filters.min = this.options.floor;
    	aggregation.filters.max = this.options.ceil;
    	aggregation.initialSlider.filtered = false;
    }

}

export default DateRangeSlider;