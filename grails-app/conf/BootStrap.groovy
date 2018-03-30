import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.elastic.*
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.util.Environment


class BootStrap {
	
	def indexService
	def configService

    def init = { servletContext ->
		
		if (Environment.current != Environment.PRODUCTION) {
			//indexService.createAdminConfigurationFromIndexMapping("nlp05.ahc.umn.edu","notes_v0")
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
			def biomedicus = Ontology.findByName('NLP Annotations')?:new Ontology(name:'NLP Annotations', description:"NLP annotations").save(flush:true, failOnError:true)
			
			
			def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://nlp05.ahc.umn.edu:9200",environment:Environment.current.toString(),description:"test cluster (to be prod)", commonName:"test cluster")
			def epicNotesIdx = Index.findByCommonName("Epic Notes")?:		new Index(commonName:"Epic Notes", 		  indexName:"notes_v3",  status:"Available", description:"clinical epic notes", 	numberOfShards:6, numberOfReplicas:0,environment:Environment.current.toString())//.save(flush:true, failOnError:true)
			
			def noteId = Field.findByFieldName("note_id")?:new Field(fieldName:"note_id",dataTypeName:"LONG", description:"Epic note id", aggregatable:false)
			def mrn = Field.findByFieldName("mrn")?:new Field(fieldName:"mrn",dataTypeName:"NOT_ANALYZED_STRING", description:"Epic patient identifier", aggregatable:true)
			def encounterId = Field.findByFieldName("encounter_id")?:new Field(fieldName:"encounter_id",dataTypeName:"LONG", description:"Epic visit number", aggregatable:true)
			def serviceDate = Field.findByFieldName("service_date")?:new Field(fieldName:"service_date",dataTypeName:"DATE", description:"Date of Service", aggregatable:true)
			def filingDatetime = Field.findByFieldName("filing_datetime")?:new Field(fieldName:"filing_datetime",dataTypeName:"DATETIME", description:"When note was filed", aggregatable:true, exportable:true)
			def eds = Field.findByFieldName("encounter_department_specialty")?:new Field(fieldName:"encounter_department_specialty",dataTypeName:"NOT_ANALYZED_STRING", description:"Specialty name in Epic", aggregatable:true, exportable:true)
			def pt = Field.findByFieldName("prov_type")?:new Field(fieldName:"prov_type",dataTypeName:"NOT_ANALYZED_STRING", description:"Provider type in Epic name", aggregatable:true, exportable:true)
			
			def role = Field.findByFieldName("role")?:new Field(fieldName:"role",dataTypeName:"NOT_ANALYZED_STRING", description:"Provider role axis in HL7-LOINC DO", aggregatable:true, exportable:true)
			def smd = Field.findByFieldName("smd")?:new Field(fieldName:"smd",dataTypeName:"NOT_ANALYZED_STRING", description:"subject matter domain", aggregatable:true, exportable:true)
			def text = Field.findByFieldName("text")?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"document text", defaultSearchField:true, aggregatable:false)
			def contextFilter = Field.findByFieldName("authorized_context_filter_value")?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:true, aggregatable:false )
			def cui = Field.findByFieldName("cuis")?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline", aggregatable:true)

			def clinicalCorpus = Corpus.findByName("Clinical Notes")?: new Corpus(name:"Clinical Notes", description:"notes from Epic", enabled:true, glyph:"fa-file-text-o").save(flush:true, failOnError:true)
			
			def noteType = Type.findByTypeName("note")?:new Type(typeName:"note", description:"CDR note", environment:Environment.current.toString())//, Corpus:clinicalCorpus)
			noteType.addToFields(noteId)
			noteType.addToFields(mrn)
			noteType.addToFields(encounterId)
			noteType.addToFields(serviceDate)
			noteType.addToFields(filingDatetime)
			noteType.addToFields(role)
			noteType.addToFields(smd)
			noteType.addToFields(text)
			noteType.addToFields(contextFilter)
			noteType.addToFields(cui)
			noteType.addToFields(eds)
			noteType.addToFields(pt)
			noteType.fields.each { f ->
				println "adding pref for ${f.fieldName}"
				def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
				if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
				if ( f.fieldName=="text" || f.fieldName=="filing_datetime" || f.fieldName=="service_date" || f.fieldName=="encounter_id" ) fp.aggregate=false
				if ( f.fieldName=="role" || f.fieldName=="smd" ) fp.ontology=epicHL7LoincOntology
				if ( f.fieldName=="cuis" ) {
					fp.ontology=biomedicus
					fp.label = "Medical Concepts"
					fp.numberOfFilterOptions = 25
					fp.aggregate = true
				}
				if ( f.fieldName=="mrn" || f.fieldName=="note_id") {
					fp.computeDistinct = true
					if ( f.fieldName=="note_id" || f.fieldName=="mrn" ) fp.aggregate = false
				}
				f.addToPreferences(fp)
			}
			epicNotesIdx.type = noteType
			nlp05.addToIndexes(epicNotesIdx)
			//println nlp05.toString()
			nlp05.save(failOnError:true, flush:true)
			//done with clinical notes config
			clinicalCorpus.addToIndexes(epicNotesIdx)
			clinicalCorpus.save(flush:true, failOnError:true)
			
			//SURG PATH REPORTS INDEX
			def surgPathCorpus = Corpus.findByName("Surgical Pathology Reports")?: new Corpus(name:"Surgical Pathology Reports", description:"surgical path reports from CDR", enabled:true, glyph:"icon-i-pathology").save(flush:true, failOnError:true)
			
			def surgPathIdx = Index.findByCommonName("Surgical Pathology Reports")?:new Index(commonName:"Surgical Pathology Reports", indexName:"surgical-path_v1", status:"Available", description:"surgical pathology reports", numberOfShards:6, numberOfReplicas:0,environment:Environment.current.toString())
			def surgPathType = Type.findByTypeName("report")?:new Type(typeName:"report", description:"CDR surgical path report", environment:Environment.current.toString())//, Corpus:surgPathCorpus)
			def report = Field.findByFieldName("report")?:new Field(fieldName:"report",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"surgical pathology report text", defaultSearchField:true,, aggregatable:false)
			def surgPathContextFilterField = Field.findByFieldNameAndType("authorized_context_filter_value", null)?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:true, aggregatable:false )
			def pathCui = Field.findByFieldNameAndType("cuis", null)?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline", aggregatable:true, exportable:true)
			surgPathType.addToFields(report)
			surgPathType.addToFields(surgPathContextFilterField)
			surgPathType.addToFields(pathCui)
			surgPathType.fields.each { f ->
				println "adding surg path pref for ${f.fieldName}"
				def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
				if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
				if ( f.fieldName=="report") fp.aggregate=false
				if ( f.fieldName=="cuis" ) {
					fp.ontology=biomedicus
					fp.label = "Medical Concepts"
					fp.numberOfFilterOptions = 50
				}
				f.addToPreferences(fp)
			}
			surgPathIdx.type = surgPathType
			nlp05.addToIndexes(surgPathIdx)
			println nlp05.toString()
			nlp05.save(failOnError:true, flush:true)
			
			surgPathCorpus.addToIndexes(surgPathIdx)
			surgPathCorpus.save(flush:true, failOnError:true)
			
			//PREFS SANITY CHECK
			configService.initalizeUserPreferences(User.findByUsername("rmcewan"))
			println "preferences set for rmcewan"
		}
		
    }
    def destroy = {
    }
	
}
	