package edu.umn.nlpie.pier

import org.codehaus.groovy.grails.web.json.JSONObject

import edu.umn.nlpie.pier.elastic.datatype.DataType
import edu.umn.nlpie.pier.elastic.datatype.DateDataType
import edu.umn.nlpie.pier.elastic.datatype.DatetimeDataType
import edu.umn.nlpie.pier.elastic.datatype.IntegerDataType
import edu.umn.nlpie.pier.elastic.datatype.LongDataType
import edu.umn.nlpie.pier.elastic.datatype.NotAnalyzedStringDataType
import edu.umn.nlpie.pier.elastic.datatype.SnowballAnalyzedStringDataType

class PierUtils {
	
	static String toSnakeCase( String text ) {
		text = text.replaceAll( /([A-Z])/, /_$1/ ).toLowerCase().replaceAll( /^_/, '' )
		println text
		text
	}
	static propertyNameToColumnName ( s ) {
		return PierUtils.toSnakeCase(s)
	}//alias
	
	static underscoreToCamelCase(s) {
		s = s.replaceAll(/_\w/) { it[1].toUpperCase() }
		println s
		s
	}
	static columnNameToPropertyName( s ) {
		return PierUtils.underscoreToCamelCase(s)
	}//alias
	
	static labelFromUnderscore( s ) {
		s = s.toLowerCase().tokenize("_").collect { it.capitalize() }.join(' ')
		println s
		s
	}
	
	static DataType dataTypeFromElasticPropertyMapping(JSONObject obj) {
		def type = obj.get("type") //all should have type
		def format = obj.containsKey("format") ? obj.get("format"):null
		def index = obj.containsKey("index") ? obj.get("index"):null
		def analyzer = obj.containsKey("analyzer") ? obj.get("analyzer"):null
		def dataType
		switch (type) {
			case { it=="string" && index=="not_analyzed" } 		: dataType = new NotAnalyzedStringDataType(); break;
			case { it=="string" && analyzer=="snowball" } 		: dataType = new SnowballAnalyzedStringDataType(); break;
			case { it=="integer" } 								: dataType = new IntegerDataType(); break;
			case { it=="long" } 								: dataType = new LongDataType(); break;	
			case { it=="date" && format=="YYYY-MM-dd" } 		: dataType = new DateDataType(); break;
			case { it=="date" && format=="YYYY-MM-dd HH:mm" } 	: dataType = new DatetimeDataType(); break;
			default												: throw new Exception("no suitable DataType found for ${obj}")
		}
		dataType		
	}
	
	static String dataTypeNameFromElasticPropertyMapping(JSONObject obj) {
		def type = obj.get("type") //all should have type
		def format = obj.containsKey("format") ? obj.get("format"):null
		def index = obj.containsKey("index") ? obj.get("index"):null
		def analyzer = obj.containsKey("analyzer") ? obj.get("analyzer"):null
		def dataTypeName
		switch (type) {
			case { it=="string" && index=="not_analyzed" } 		: dataTypeName = "NOT_ANALYZED_STRING"; break;
			case { it=="string" && analyzer=="snowball" } 		: dataTypeName = "SNOWBALL_ANALYZED_STRING"; break;
			case { it=="integer" } 								: dataTypeName = "INTEGER"; break;
			case { it=="long" } 								: dataTypeName = "LONG"; break;
			case { it=="date" && format=="YYYY-MM-dd" } 		: dataTypeName = "DATE"; break;
			case { it=="date" && format=="YYYY-MM-dd HH:mm" } 	: dataTypeName = "DATETIME"; break;
			default												: throw new Exception("no suitable dataTypeName found for ${obj}")
		}
		dataTypeName
	}
	
	static DataType dataTypeFromName(String dataTypeName) {
		def dataType
		switch (dataTypeName) {
			case "INTEGER"						: dataType = new IntegerDataType(); break;
			case "LONG"							: dataType = new LongDataType(); break;
			case "DATE" 						: dataType = new DateDataType(); break;
			case "DATETIME" 					: dataType = new DatetimeDataType(); break;
			case "NOT_ANALYZED_STRING"	 		: dataType = new NotAnalyzedStringDataType(); break;
			case "SNOWBALL_ANALYZED_STRING"	 	: dataType = new SnowballAnalyzedStringDataType(); break;
			default 							: throw new Exception("no suitable DataType found for ${[dataTypeName]}")
		}
		dataType
	}

}
