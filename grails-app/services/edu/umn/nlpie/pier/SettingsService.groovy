package edu.umn.nlpie.pier

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
		def m = [:]
		indexes.each { index ->
			m.put( index.commonName, preferencesByOntology(index,'ALL') )
		}
		m
    }
	
	private preferencesByOntology( index,scope ) {
		//TODO lookup user
		//verify user has access to these settings
		def user = User.findByUsername("rmcewan")
		def ontologies = Ontology.list()
		def o = [:]
		ontologies.each { ontology ->
			//o.put( ontology.name, null )
			def fieldPreferences 
			switch(scope) {
				case "ALL": fieldPreferences = FieldPreference.executeQuery('from FieldPreference fp where user=? and fp.field.type.index=? and fp.ontology=? and (field.aggregatable=? or field.exportable=?) order by fp.label',[ user,index,ontology,true,true ], [ readOnly:true ]); break;
				case "AGGREGATES": fieldPreferences = FieldPreference.executeQuery('from FieldPreference fp where user=? and fp.field.type.index=? and fp.ontology=? and fp.aggregate=? and field.aggregatable=? order by fp.label',[ user,index,ontology,true, true ], [ readOnly:true ]); break;
				//case "EXPORTS": fieldPreferences = FieldPreference.executeQuery('from FieldPreference fp where user=? and fp.field.type.index=? and fp.ontology=? and fp.export=? and field.exportable=? order by fp.label',[ user,index,ontology,true,true ], [ readOnly:true ]); break;
			}
			if ( fieldPreferences.size()>0 ) {	//put ontology and prefs in o only if there are preferences
				def p = [:]
				fieldPreferences.each { fp ->
					p.put( fp.label, fp)
				}
				o[ontology.name] = p
			}
		}
		o		
	}
	
	def corpusAggregations( corpusId ) {
		def corpus = Corpus.get(corpusId.toLong())
		def index = corpus.index
		preferencesByOntology(index,'AGGREGATES')
	}
	
	def updatePreference( properties ) {
		def fp = FieldPreference.get(properties.id.toLong())
		fp.properties = properties
		fp.save()
    }
}
