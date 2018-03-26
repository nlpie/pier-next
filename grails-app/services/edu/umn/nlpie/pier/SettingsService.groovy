package edu.umn.nlpie.pier

import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.transaction.Transactional

@Transactional
class SettingsService {

    def preferences() {
		//lookup user
		//verify user has access to these settings
		def user = User.findByUsername("rmcewan")
		def indexes = Index.findAllByStatus("Available")
		def m = [:]
		indexes.each { index ->
			def ontologies = Ontology.list()
			def o = [:]
			ontologies.each { ontology ->
				//o.put( ontology.name, null )
				def fieldPreferences = FieldPreference.executeQuery(
					'from FieldPreference fp where user=? and fp.field.type.index=? and fp.ontology=? and (field.aggregatable=? or field.exportable=?) order by fp.label',
					[ user,index,ontology,true,true ], [ readOnly:true ]
				)
				if ( fieldPreferences.size()>0 ) {	//put ontology and prefs in o only if there are preferences
					def p = [:]
					fieldPreferences.each { fp ->
						p.put( fp.label, fp)
					}
					o[ontology.name] = p
				}
			}
			m.put( index.commonName, o)
		}
		m
    }
	
	def updatePreference( properties ) {
		def fp = FieldPreference.get(properties.id.toLong())
		fp.properties = properties
		fp.save()
    }
}
