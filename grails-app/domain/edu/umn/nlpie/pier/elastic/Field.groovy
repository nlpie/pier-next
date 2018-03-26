package edu.umn.nlpie.pier.elastic

import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.elastic.datatype.DataType
import edu.umn.nlpie.pier.ui.FieldPreference


class Field {

	/*enum DataTypeReference {
		INTEGER, LONG, NOT_ANALYZED_STRING, SNOWBALL_ANALYZED_STRING, DATE, DATETIME
	}*/
	
	/*enum Rating {
		G("G"),PG("PG"),PG13("PG-13"),R("R"),NC17("NC-17"),NR("Not Rated")
	
		final String value
	
		Rating(String value) { this.value = value }
	
		String toString() { value }
		String getKey() { name() }
	}*/
    
	static mapping = {
		
    }
	
	static constraints = {
		fieldName( unique:'type')
		dataTypeName( inList: [ "INTEGER", "LONG", "NOT_ANALYZED_STRING", "SNOWBALL_ANALYZED_STRING", "DATE", "DATETIME" ] )
		description(nullable:true)	
		dataType display:false
		aggregatable()
		exportable()
		defaultPreference display:false
	}
	static belongsTo = [ type:Type ]
	//static hasOne = [ uiDataElement:UIDataElement]
	static hasMany = [ preferences:FieldPreference ]
	
	
	String fieldName
	Boolean defaultSearchField = false
	Boolean contextFilterField = false
	Boolean aggregatable = false
	Boolean exportable = false
	String dataTypeName
	String description
	List<FieldPreference> preferences
	
	static transients = [ 'defaultPreference', 'dataType', 'property' ]
	String property	//alias for fieldName
	DataType dataType
	FieldPreference defaultPreference
	
	Date dateCreated
	Date lastUpdated
	
	/*public List<FieldPreference> getPreferences() {
        //if (preferences == null) return null;
        FieldPreference.findAllByApplicationDefault(true)
    }
	public void setPreferences(List<FieldPreference> prefsList) {
		preferences = prefsList
	}*/
	
	/*public List<FieldPreference> getAllPreferences() {
		//if (preferences == null) return null;
		if (this.id==null) return null
		if ( preferences==null ) return null
		FieldPreference.findAllByField(this)
	}*/
	
	public FieldPreference getDefaultPreference() {
		if (this.id==null) return null
		FieldPreference.findByFieldAndApplicationDefault(this,true)
	}
	
	def afterLoad() {
		/*switch (dataTypeName) {
			case "INTEGER": dataType = new IntegerDataType(); break;
			case "LONG": dataType = new LongDataType(); break;
			case "DATE": dataType = new DateDataType(); break;
			case "DATETIME": dataType = new DatetimeDataType(); break;
			case "NOT_ANALYZED_STRING": dataType = new NotAnalyzedStringDataType(); break;
			case "SNOWBALL_ANALYZED_STRING": dataType = new SnowballAnalyzedStringDataType(); break;
			default: dataType = new NotAnalyzedStringDataType()
		}*/
		dataType = PierUtils.dataTypeFromName(dataTypeName)
		property = fieldName
	}
	
	@Override
	String toString() {
		(id!=null) ? "${type.index.indexName}.${type}.${fieldName}: ${dataTypeName}" : ""
	}
	
}