package edu.umn.nlpie.pier.cdr

class Umls {

    static constraints = {
    }
	
	static mapping = {
		datasource 'notes'
		table name: "MRCONSO", schema: "UMLS_2017AA"	//view in notes schema
		version false
		id name: 'aui'
	}
	
	/*static hibernateFilters = {
		sabFilter(condition: " SAB in ( 'SNOWMEDCT_US' ) ", default: true)
		tsFilter(condition: " TS='P' ", default: true)
	}*/
	
	String aui
	String cui
	String sab
	String ts
	String str
	String ispref
}
