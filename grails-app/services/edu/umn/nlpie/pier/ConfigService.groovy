package edu.umn.nlpie.pier

import edu.umn.nlpie.pier.elastic.Cluster
import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.request.AuthorizedContext
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

@Transactional
class ConfigService {
	
	static scope = "prototype"

    def getAuthorizedContexts() {
		//get user from spring security service
		def username = "wein0153"
		//Request.findAllByStatus( "Completed", [sort: "icsRequest"] )
		AuthorizedContext.findAllByUsername(username)
    }
	
	def getDefaultPreferences() {
		FieldPreference.findAllByApplicationDefault(true)
	}
	
	def clonePreferences(User user) {
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
	
	def generatePassword() {
		def rest = new RestBuilder()
		def resp = rest.get("https://www.random.org/strings/?num=1&len=20&digits=on&upperalpha=off&loweralpha=on&unique=on&format=plain&rnd=new")
		resp.text
	}
	
	def data() {
		Cluster.withSession { session ->
			def user = User.findByUsername("rmcewan")?:new User(username:"rmcewan",password:"umn").save(failOnError:true)
			def app = User.findByUsername("nlppier")?:new User(username:"nlppier",password:"${this.generatePassword()}",enabled:false).save(failOnError:true)
			
			def epicOntology = Ontology.findByName('Epic Categories')?:new Ontology(name:'Epic Categories', description:"what shows in Epic").save(flush:true, failOnError:true)
			def epicHL7LoincOntology = Ontology.findByName('HL7 LOINC')?:new Ontology(name:'HL7 LOINC', description:"DO Axis values").save(flush:true, failOnError:true)
			
			def cluster = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://nlp05.ahc.umn.edu:9200",environment:"DEV",description:"test cluster (to be prod)", commonName:"me too")//.save(flush:true, failOnError:true)
			
			def epic = Index.findByCommonName("Epic Notes")?:new Index(commonName:"Epic Notes", indexName:"notes_v2", status:"In Progress", description:"clinical epic notes", numberOfShards:6, numberOfReplicas:0)//.save(flush:true, failOnError:true)
			def path = Index.findByCommonName("Pathology Reports")?:new Index(commonName:"Pathology Reports", indexName:"pathology", status:"In Progress", description:"path reports", numberOfShards:6, numberOfReplicas:0)//.save(flush:true, failOnError:true)
			
			def noteType = Type.findByTypeName("note_data")?:new Type(typeName:"note_data", description:"CDR note")//.save(flush:true, failOnError:true)			
			
			def mrn = Field.findByFieldName("mrn")?:new Field(fieldName:"mrn",dataTypeName:"NOT_ANALYZED_STRING", description:"Epic patient identifier")//.save(flush:true, failOnError:true)
			def encounterId = Field.findByFieldName("encounter_id")?:new Field(fieldName:"encounter_id",dataTypeName:"LONG", description:"Epic visit number")//.save(flush:true, failOnError:true)
			
			//session.flush()
			
			noteType.addToFields(mrn)//.save(flush:true, failOnError:true)
			noteType.addToFields(encounterId)//.save(flush:true, failOnError:true)
			
			noteType.fields.each { f ->
				println "adding pref for ${f.fieldName}"
				f.addToPreferences(new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true))
			}
		
			epic.addToTypes(noteType)
			cluster.addToIndexes(epic)
			cluster.addToIndexes(path)
			cluster.save(failOnError:true)
			
			println "done"
		}
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
