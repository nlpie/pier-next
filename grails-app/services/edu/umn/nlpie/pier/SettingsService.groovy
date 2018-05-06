package edu.umn.nlpie.pier

import org.codehaus.groovy.grails.web.json.JSONArray

import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.transaction.Transactional

@Transactional
class SettingsService {

    def preferences() {	//all preferences
		def aggreateable = true
		def exportable = true
		def indexes = Index.getAvailableIndexes()	//findAllByStatus("Available")
		def map = [:]
		indexes.each { index ->
			map.put( index.commonName, preferencesByOntology(index,'ALL') )
		}
		map
    }
	
    def corpusAggregations( corpusId ) {
    	//should return array of ontologies, each of which has an array of aggregations
    	def corpus = Corpus.get(corpusId.toLong())
    	def index = corpus.index
    	preferencesByOntology(index,'AGGREGATES')
    }

	private preferencesByOntology( index,scope ) {
		//TODO lookup user
		//verify user has access to these settings
		def user = User.findByUsername("rmcewan")
		def ontologies = Ontology.list()
		//def o = [:]
		def relevantOntologies = new JSONArray()
		ontologies.each { ontology ->
			def prefs 
			switch(scope) {
				case "ALL": 
					prefs = FieldPreference.executeQuery('from FieldPreference fp where user=? and fp.field.type.index=? and fp.ontology=? and (field.aggregatable=? or field.exportable=?) order by fp.label',[ user,index,ontology,true,true ], [ readOnly:true ]) 
					break
				case "AGGREGATES": 
					prefs = FieldPreference.executeQuery('from FieldPreference fp where user=? and fp.field.type.index=? and fp.ontology=? and fp.aggregate=? and field.aggregatable=? order by fp.label',[ user,index,ontology,true, true ], [ readOnly:true ])
					break
			}
			if ( prefs.size()>0 ) {	//put ontology and prefs in o only if there are preferences
				ontology.fieldPreferences = prefs
				//prefs.each { println "${scope} ${it}" }
				relevantOntologies.add( ontology )
				//def p = [:]
				/*def p = new JSONArray()
				fieldPreferences.each { fp ->
					//p.put( fp.label, fp)
					p.add(fp)
				}
				//o[ontology.name] = p*/
			}
		}
		relevantOntologies	
	}
	
	
	def updatePreference( properties ) {
		def fp = FieldPreference.get(properties.id.toLong())
		fp.properties = properties
		fp.save()
    }
	
	def saveQuery( registrationId ) {
		//TODO get user from spring security
		def reg = SearchRegistration.get(registrationId.toLong())
		def maxSeq = reg.maxSequence
		Query.executeUpdate("update Query q set q.saved=true where q.registration.id=? and q.sequence=?", [reg.id, maxSeq] )
		reg
	}
	
}
