package edu.umn.nlpie.pier

import edu.umn.nlpie.pier.context.AuthorizedContext
import edu.umn.nlpie.pier.elastic.Cluster
import edu.umn.nlpie.pier.elastic.Field
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.elastic.Type
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.CorpusType
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

@Transactional
class ConfigService {
	
	static scope = "prototype"

    //keep
	def getAuthorizedContexts() {
		//get user from spring security service
		def username = "gmelton"
		//Request.findAllByStatus( "Completed", [sort: "icsRequest"] )
		AuthorizedContext.findAllByUsername(username)
    }
	
	//keep
	def getAvailableCorpora(env) {
		Type.where { corpusType.id in ( CorpusType.list().collect{it.id} ) && environment==env }.list()
	}
	
	def getDefaultPreferences() {
		FieldPreference.findAllByApplicationDefault(true)
	}
	
	//keep
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
	
	//TODO check if this is used - think not
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
			def clinicalCorpusType = CorpusType.findByName("Clinical Notes")?: new CorpusType(name:"Clinical Notes", description:"notes from Epic").save(flush:true, failOnError:true)
			def microbioCorpusType = CorpusType.findByName("Microbiology Notes")?: new CorpusType(name:"Microbioloy Notes", description:"microbio results from CDR").save(flush:true, failOnError:true)
			
			def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://nlp05.ahc.umn.edu:9200",environment:"TEST",description:"test cluster (to be prod)", commonName:"test cluster")			
			def epic = Index.findByCommonName("Epic Notes")?:		new Index(commonName:"Epic Notes", 		  indexName:"notes_v2",  status:"Searchable", description:"clinical epic notes", 	numberOfShards:6, numberOfReplicas:0)//.save(flush:true, failOnError:true)
			
			
			def mrn = Field.findByFieldName("mrn")?:new Field(fieldName:"mrn",dataTypeName:"NOT_ANALYZED_STRING", description:"Epic patient identifier")//.save(flush:true, failOnError:true)
			def encounterId = Field.findByFieldName("encounter_id")?:new Field(fieldName:"encounter_id",dataTypeName:"LONG", description:"Epic visit number")//.save(flush:true, failOnError:true)
			def text = Field.findByFieldName("text")?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"document text", defaultSearchField:true)//.save(flush:true, failOnError:true)
			//session.flush()
			
			def noteType = Type.findByTypeName("note")?:new Type(typeName:"note", description:"CDR note", environment:"development", corpusType:clinicalCorpusType)//.save(flush:true, failOnError:true)
			noteType.addToFields(mrn)//.save(flush:true, failOnError:true)
			noteType.addToFields(encounterId)//.save(flush:true, failOnError:true)
			noteType.addToFields(text)
			noteType.fields.each { f ->
				println "adding pref for ${f.fieldName}"
				f.addToPreferences(new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true))
			}
		
			epic.addToTypes(noteType)
			nlp05.addToIndexes(epic)
			println nlp05.toString()
			nlp05.save(failOnError:true, flush:true)
			//done with nlp05 config
			
			
			
			def nlp02 = Cluster.findByClusterName("nlp02")?:new Cluster(clusterName:"nlp02",uri:"http://nlp02.ahc.umn.edu:9200",environment:"PROD",description:"prod cluster (to be test)", commonName:"prod cluster")
			def micro = Index.findByCommonName("Microbiology Reports")?:new Index(commonName:"Microbiology Reports", indexName:"microbio_v1", status:"Searchable", description:"microbiology result reports", numberOfShards:6, numberOfReplicas:0)//.save(flush:true, failOnError:true)
			def resultText = Field.findByFieldNameAndTypeIsNull("text")?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"microbiology result text", defaultSearchField:true)//.save(flush:true, failOnError:true)
			def resultType = Type.findByTypeName("result")?:new Type(typeName:"result", description:"CDR microbio results", environment:"development", corpusType:microbioCorpusType)//.save(flush:true, failOnError:true)
			resultType.addToFields(resultText)
			resultType.fields.each { f ->
				println "adding pref for ${f.fieldName}"
				f.addToPreferences(new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicHL7LoincOntology, applicationDefault:true))
			}
			micro.addToTypes(resultType)
			nlp02.addToIndexes(micro)
			println nlp02.toString()
			nlp02.save(failOnError:true)
			
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
