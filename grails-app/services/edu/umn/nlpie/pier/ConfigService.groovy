package edu.umn.nlpie.pier

import org.apache.commons.lang.RandomStringUtils

import edu.umn.nlpie.pier.api.AvailableAggregations
import edu.umn.nlpie.pier.context.AuthorizedContext
import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

@Transactional
class ConfigService {
	
	static scope = "prototype"

    //keep
	def getAuthorizedContexts() {
		def list = AuthorizedContext.list(sort:'label')
		//TODO get user from spring security service
		//TODO check if user has ROLE_ADMIN, if yes, add each enabled corpus for the current env for corpus-wide searching
		def corpora = Corpus.availableCorpora
		corpora.each { ct ->
			list.add(0,new AuthorizedContext( label:ct.name, filterValue:0 ));
		}
		list
    }
	
	def authorizedContextByLabel( label ) { 
		//TODO get user from spring security service
		println label
		def ac = AuthorizedContext.findByLabel( label );
		if ( !ac ) ac = new AuthorizedContext( label:label, filterValue:0 )
		ac
	}
	
	
	def getCorpusAggregations(corpusId) {
		new AvailableAggregations(corpusId).aggregations
	}
	
	def getDefaultPreferences() {
		FieldPreference.findAllByApplicationDefault(true)
	}
	
	//keep - creates set of initial preferences for new users
	def initalizeUserPreferences(User user) {
		def prefs = this.defaultPreferences
		prefs.each {
			println "cloning ${it.field.type}:${it.label}"
			def field = it.field
			def fp = new FieldPreference(it.properties)
			fp.setUser(user)
			fp.setId(null)
			fp.setDateCreated(null)
			fp.setDateCreated(null)
			fp.setApplicationDefault(false)//this is not strictly necessary for new, non-cloned instance, it's the default value
			field.addToPreferences(fp)
			field.save(failOnError:true)
		}
	}
	
	//TODO check if this is used - think not
	def generatePassword() {
		RandomStringUtils.randomAlphanumeric(20)
	}
	
	def prefs() {
		def note = Type.findByTypeName("note")
		def app = User.findByUsername("nlppier")	//should have been created in bootstrap
		def epicOntology = Ontology.findByName('Epic Categories')
	}
	
	def reverseEngineerDomainInstancesFromIndexMapping(Index index) {
		//method assumes cluster and associated index already exists
		//put in check for not overwriting domain classes based on elastic index mapping? Or is there value in being able to overwrite - certainly in case of non-strict elastic schema there is definite value.
		
		/*Cluster.withSession { session ->
			this.data()
			session.flush()
		}*/
		
		/*Index.withSession { session ->
			Index.findByIndexName(index.indexName)?.delete()
			session.flush()
		}*/
		def user = User.findByUsername("nlppier")	//all "reverse engineered" domain instances are owned by the app itself, not one of the users
		//def epicOntology = Ontology.findByName('Epic Categories')
		
		
		def rest = new RestBuilder()
		//def cluster = Cluster.findByClusterName('nlp05')
		def resp = rest.get("${index.cluster.uri}/${index.indexName}")
		
		def indexDefinition = resp.json[index.indexName]
		
		/*indexDefinition.keys().each { key ->
			println key
		}*/
		
		//as of ES 2.3, indexDefinition should have 4 nodes: settings, aliases, warmers, mappings (contains type in the index)
		//only interested in mappings and settings
		def mappings = indexDefinition.get("mappings")
		println mappings
		def settings = indexDefinition.get("settings")
		println settings
		
		
		mappings.keys().each { mappingKey ->
			def type = new Type(typeName:mappingKey)
			index.addToTypes(type)
			
			def props = mappings.get(mappingKey).get("properties")
			props.keys().each { propKey ->
				def propertyDef = props[propKey]
				def dataTypeName = PierUtils.dataTypeNameFromElasticPropertyMapping(propertyDef)
				println "${propKey} ${dataTypeName}"
				def field = new Field(dataTypeName:dataTypeName, fieldName:propKey)
				field.addToPreferences(new FieldPreference(user:user, label:PierUtils.labelFromUnderscore(field.fieldName), applicationDefault:true))
				type.addToFields(field)
				index.addToTypes(type)
				//println "${props[propKey].getClass().getName()} ${props[propKey]}"
				//println "${props[propKey].getClass().getName()} ${props[propKey]} ${PierUtils.dataTypeFromElasticPropertyMapping(props[propKey]).getClass().getName()}"
			}
		}
		
		index.numberOfShards = new Integer(settings.index.number_of_shards)
		index.numberOfReplicas = new Integer(settings.index.number_of_replicas)
		index.save(failOnError:true)
	}
}
