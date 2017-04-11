
class Filter {
    
	constructor(usersettings,aggs) {
    	//constructed from user elastic aggs returned from search AND user settings 
		//usersettings needed to get es field name to use in constructing search filters
		//b/c aggs come back with label (which could be ui label, or es field name)
		//either way, need both sets of objects to lookup either ui label from field name or other way around
    }

}

export default Filter;