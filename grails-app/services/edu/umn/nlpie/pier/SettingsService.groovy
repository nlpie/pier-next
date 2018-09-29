package edu.umn.nlpie.pier

import org.codehaus.groovy.grails.web.json.JSONArray

import edu.umn.nlpie.pier.api.exception.InsufficientPrivilegesException
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.transaction.Transactional

@Transactional
class SettingsService {
	
	def userService

    def corpusPreferences( corpusId ) {	//all preferences
		def corpus = Corpus.get(corpusId.toLong())
    	def index = corpus.index
		preferencesByOntology( index,'ALL' )	//returns JSONArray
		/*
		def indexes = Index.getSearchableIndexes()	//findAllByStatus("Searchable")
		def map = [:]
		indexes.each { index ->
			println "SettingsService.preferences(): ${index.commonName}"
			map.put( index.commonName, preferencesByOntology( index,'ALL' ) )
		}
		map
		*/
    }
	
    def corpusAggregations( corpusId ) {
    	//should return array of ontologies, each of which has an array of aggregations
    	def corpus = Corpus.get(corpusId.toLong())
		println "settingsService.corpusAggregations ${corpus.name} ${corpusId}"
    	def index = corpus.index
    	preferencesByOntology( index,'AGGREGATES' )	//returns JSONArray
    }

	private preferencesByOntology( index,scope ) {
		def user = User.findByUsername(userService.currentUserUsername)
		def ontologies = Ontology.list()
		//def o = [:]
		def relevantOntologies = new JSONArray()
		ontologies.each { ontology ->
			//println "${index} ${ontology}"
			def prefs 
			switch(scope) {
				case "ALL": 
					prefs = FieldPreference.executeQuery('from FieldPreference fp where fp.user=? and fp.field.type.index=? and fp.ontology=? and (field.aggregatable=? or field.exportable=?) order by fp.label', [ user,index,ontology,true,true ], [ readOnly:true ]) 
					break
				case "AGGREGATES": 
					prefs = FieldPreference.executeQuery('from FieldPreference fp where fp.user=? and fp.field.type.index=? and fp.ontology=? and fp.aggregate=? and field.aggregatable=? order by fp.label', [ user,index,ontology,true,true ], [ readOnly:true ])
					break
			}
			if ( prefs.size()>0 ) {	//put ontology and prefs in o only if there are preferences
				ontology.fieldPreferences = prefs
				relevantOntologies.add( ontology )
			}
			println "\t${index}: ${relevantOntologies.toString()}" 
		}
		relevantOntologies	
	}
	
	
	def updatePreference( properties ) {
		def fp = FieldPreference.get(properties.id.toLong())
		if ( fp.user.username!=userService.currentUserUsername ) {
			throw new InsufficientPrivilegesException( "User does not have sufficient privileges to change this setting ")
		}
		fp.properties = properties
		fp.save()
    }
	
	def saveQuery( uuid ) {
		Query.executeUpdate("update Query q set q.saved=true where q.uuid=?", [uuid] )
		Query.findAllByUuid( uuid )
	}
	
}
