import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.elastic.*
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.CorpusType
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.util.Environment


class BootStrap {
	
	def indexService
	def configService

    def init = { servletContext ->
		
		if (Environment.current != Environment.PRODUCTION) {
			indexService.createAdminConfigurationFromIndexMapping("nlp05.ahc.umn.edu","notes_v2")
			/*
			{
				"notes_v2": {
				  "mappings": {
					"note": {
					  "dynamic": "strict",
					  "_timestamp": {
						"enabled": true
					  },
				      "properties": {
						  ....
					  }
					}
				  }
				}
			}
			*/
		}
		
		//make sure nlppier user exists
		User.findByUsername("nlppier")?:new User(username:"nlppier",password:"${configService.generatePassword()}",enabled:false).save(failOnError:true)
		
		//populate elastic data
		Cluster.withSession { session ->
			def user = User.findByUsername("rmcewan")?:new User(username:"rmcewan",password:"umn").save(failOnError:true)
			def app = User.findByUsername("nlppier")?:new User(username:"nlppier",password:"${this.generatePassword()}",enabled:false).save(failOnError:true)
			
			def epicOntology = Ontology.findByName('Epic Categories')?:new Ontology(name:'Epic Categories', description:"what shows in Epic").save(flush:true, failOnError:true)
			def epicHL7LoincOntology = Ontology.findByName('HL7 LOINC')?:new Ontology(name:'HL7 LOINC', description:"DO Axis values").save(flush:true, failOnError:true)
			def clinicalCorpusType = CorpusType.findByName("Clinical Notes")?: new CorpusType(name:"Clinical Notes", description:"notes from Epic", enabled:true).save(flush:true, failOnError:true)
			def microbioCorpusType = CorpusType.findByName("Microbiology Notes")?: new CorpusType(name:"Microbioloy Notes", description:"microbio results from CDR", enabled:true).save(flush:true, failOnError:true)
			
			def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://nlp05.ahc.umn.edu:9200",environment:"TEST",description:"test cluster (to be prod)", commonName:"test cluster")
			def epic = Index.findByCommonName("Epic Notes")?:		new Index(commonName:"Epic Notes", 		  indexName:"notes_v2",  status:"Available", description:"clinical epic notes", 	numberOfShards:6, numberOfReplicas:0)//.save(flush:true, failOnError:true)
			
			
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
			def micro = Index.findByCommonName("Microbiology Reports")?:new Index(commonName:"Microbiology Reports", indexName:"microbio_v1", status:"Available", description:"microbiology result reports", numberOfShards:6, numberOfReplicas:0)//.save(flush:true, failOnError:true)
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
			
			println "done populating elastic info"
		}
		
    }
    def destroy = {
    }
	
}
	