package edu.umn.nlpie.pier

import org.apache.commons.lang.RandomStringUtils

import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.context.AuthorizedContext
import edu.umn.nlpie.pier.context.PierApplicationConfiguredCorpusContext
import edu.umn.nlpie.pier.context.PierCorpusContextByUser
import edu.umn.nlpie.pier.context.PierNoteSetContext
import edu.umn.nlpie.pier.context.PierNoteSetContextByUser
import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

@Transactional
class ConfigService {
	
	static scope = "prototype"
	def userService
	
	/*def getAuthorizedContexts() {
		def list = new ArrayList()
		def username = userService.currentUserUsername
		def corpora = Corpus.searchableCorpora
		corpora.each { c ->
			if ( SpringSecurityUtils.ifAnyGranted( c.minimumRole.authority ) ) {  
				list.add(0,new AuthorizedContext( label:c.name, contextFilterValue:0, description:c.description, corpusName:c.name, filteredContext:false ));
			}
		}
		if ( SpringSecurityUtils.ifAnyGranted( 'ROLE_ANALYST' ) ) {
			println "ANALYSTS"
			list.addAll( ActiveContext.list( [sort:'label']) )
		} else {
			list.addAll( AuthorizedContext.findAllByUsername(username,[sort:'filteredContext']) )
		}
		println "${username}, ${list.size()} contexts available"
		list
	}*/
	
	def getAuthorizedContexts() {
		def list = new ArrayList()
		def username = userService.currentUserUsername
		def corpora = Corpus.searchableCorpora
		
		//load application-configured corpus contexts, if user has privs
		corpora.each { c ->
			if ( SpringSecurityUtils.ifAnyGranted( c.minimumRole.authority ) ) {
				list.add(0,new PierApplicationConfiguredCorpusContext( label:c.name, contextFilterValue:0, description:c.description, corpusName:c.name, filteredContext:false ));
			}
		}
		
		//load admin console-configured corpus contexts
		list.addAll( PierCorpusContextByUser.findAllByUsername(username) )
		
		//load note set contexts based on user roles
		if ( SpringSecurityUtils.ifAnyGranted( 'ROLE_ANALYST' ) ) {
			println "ANALYST"
			list.addAll( PierNoteSetContext.list( [sort:'label']) )
		} else {
			list.addAll( PierNoteSetContextByUser.findAllByUsername(username,[sort:'contextFilterValue']) )
		}
		println "${username}, ${list.size()} contexts available"
		list
	}
	
	//deprecated 8/5/19
	/*def authorizedContextByLabel( label ) { 
		def username = userService.currentUserUsername
		def ac = AuthorizedContext.findByLabelAndUsername( label,username );
		if ( !ac ) {
			println "Created AuthContext from configured corpora (Corpus domain object)"
			def c = Corpus.findByName( label )
			ac = new AuthorizedContext( label:c.name, contextFilterValue:0, description:c.description, corpusName:c.name, filteredContext:false )
		} else {
			println "AuthContext found in authorized_context db object"
		}	
		ac
	}*/
	
	def authorizedContextByLabel( label ) {
		//first try to get context by username and label
		def ac
		def username = userService.currentUserUsername
println "ACBL: ${username} ${label}"
println "ACBL: looking for PierNoteSetContextByUser"
		ac = PierNoteSetContextByUser.findByLabelAndUsername( label, username )
		if ( !ac ) {
println "ACBL: looking for PierNoteSetContext"
			ac = PierNoteSetContext.findByLabel( label )
		}
		if ( !ac ) {
println "ACBL: looking for PierCorpusContextByUser"
			ac = PierCorpusContextByUser.findByLabelAndUsername( label, username )
		}
		if ( !ac ) {
println "ACBL: looking for Corpus to use with PierApplicationConfiguredCorpusContext"
			def c = Corpus.findByName( label )
println "ACBL: whole corpus [${c.name}] found for input [${label}]"
			ac = new PierApplicationConfiguredCorpusContext( label:c.name, contextFilterValue:0, description:c.description, corpusName:c.name, filteredContext:false )
		}
println "ACBL: AuthorizedPierContext impl found: ${ac.getClass().getName()}"
		ac
	}
	
	def authorizedContextByQueryId( queryId ) {
		def query = Query.get( queryId.toLong() )
		def label = query.authorizedContext
		def username = userService.currentUserUsername
		def ac = AuthorizedContext.findByLabelAndUsername( label,username );
		if ( !ac ) ac = new AuthorizedContext( label:label, filterValue:0 )
		ac
	}
	
	def getDefaultPreferences() {
		FieldPreference.findAllByApplicationDefault(true)
	}
	
	//keep - creates set of initial preferences for new users
	def initalizeUserPreferences( User user ) {
		def prefs = this.defaultPreferences
		prefs.each { defaultPreference ->
			//println "cloning ${it.field.type}:${it.label}"
			def pref = FieldPreference.findByUserAndField( user, defaultPreference.field )
			if ( !pref ) {
				def field = defaultPreference.field
				def fp = new FieldPreference( defaultPreference.properties )
				fp.setUser(user)
				fp.setId(null)
				fp.setDateCreated(null)
				fp.setDateCreated(null)
				fp.setApplicationDefault(false)//this is not strictly necessary for new, non-cloned instance, it's the default value
				field.addToPreferences(fp)
				field.save(failOnError:true)
				println "\tfield [${defaultPreference.field.fieldName}] pref added for [${user.username}]"
			}
		}
		println "PREFERENCES SET FOR ${user.username}"
	}
	
	def generatePassword() {
		RandomStringUtils.randomAlphanumeric(20)
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
