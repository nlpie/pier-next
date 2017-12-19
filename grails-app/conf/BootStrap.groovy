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
			def biomedicus = Ontology.findByName('BioMedICUS NLP Annotations')?:new Ontology(name:'BioMedICUS NLP Annotations', description:"NLP annotations").save(flush:true, failOnError:true)
			
			def clinicalCorpusType = CorpusType.findByName("Clinical Notes")?: new CorpusType(name:"Clinical Notes", description:"notes from Epic", enabled:true, glyph:"fa-file-text-o").save(flush:true, failOnError:true)
			
			def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://nlp05.ahc.umn.edu:9200",environment:"TEST",description:"test cluster (to be prod)", commonName:"test cluster")
			def epicNotesIdx = Index.findByCommonName("Epic Notes")?:		new Index(commonName:"Epic Notes", 		  indexName:"notes_v3",  status:"Available", description:"clinical epic notes", 	numberOfShards:6, numberOfReplicas:0)//.save(flush:true, failOnError:true)
			
			def noteId = Field.findByFieldName("note_id")?:new Field(fieldName:"note_id",dataTypeName:"LONG", description:"Epic note id")
			def mrn = Field.findByFieldName("mrn")?:new Field(fieldName:"mrn",dataTypeName:"NOT_ANALYZED_STRING", description:"Epic patient identifier")
			def encounterId = Field.findByFieldName("encounter_id")?:new Field(fieldName:"encounter_id",dataTypeName:"LONG", description:"Epic visit number")
			def serviceDate = Field.findByFieldName("service_date")?:new Field(fieldName:"service_date",dataTypeName:"DATE", description:"Date of Service")
			def filingDatetime = Field.findByFieldName("filing_datetime")?:new Field(fieldName:"filing_datetime",dataTypeName:"DATETIME", description:"When note was filed")
			
			def kod = Field.findByFieldName("kod")?:new Field(fieldName:"kod",dataTypeName:"NOT_ANALYZED_STRING", description:"kind of document")
			def smd = Field.findByFieldName("smd")?:new Field(fieldName:"smd",dataTypeName:"NOT_ANALYZED_STRING", description:"subject matter domain")
			def text = Field.findByFieldName("text")?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"document text", defaultSearchField:true)
			def contextFilter = Field.findByFieldName("authorized_context_filter_value")?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:true )
			def cui = Field.findByFieldName("cuis")?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline")
			
			def noteType = Type.findByTypeName("note")?:new Type(typeName:"note", description:"CDR note", environment:Environment.current.name, corpusType:clinicalCorpusType)
			noteType.addToFields(noteId)
			noteType.addToFields(mrn)
			noteType.addToFields(encounterId)
			noteType.addToFields(serviceDate)
			noteType.addToFields(filingDatetime)
			noteType.addToFields(kod)
			noteType.addToFields(smd)
			noteType.addToFields(text)
			noteType.addToFields(contextFilter)
			noteType.addToFields(cui)
			noteType.fields.each { f ->
				println "adding pref for ${f.fieldName}"
				def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
				if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
				if ( f.fieldName=="text" || f.fieldName=="service_date" || f.fieldName=="filing_datetime" ) fp.aggregate=false
				if ( f.fieldName=="kod" || f.fieldName=="smd") fp.ontology=epicHL7LoincOntology
				if ( f.fieldName=="cuis" ) {
					fp.ontology=biomedicus
					fp.label = "UMLS CUI"
					fp.numberOfFilterOptions = 100
					fp.aggregate = false
				}
				if ( f.fieldName=="mrn" || f.fieldName=="note_id") {
					fp.computeDistinct = true
					if ( f.fieldName=="note_id" ) fp.aggregate = false
				}
				f.addToPreferences(fp)
			}
			epicNotesIdx.addToTypes(noteType)
			nlp05.addToIndexes(epicNotesIdx)
			//println nlp05.toString()
			nlp05.save(failOnError:true, flush:true)
			//done with clinical notes config
			
			//SURG PATH REPORTS INDEX
			def surgPathCorpusType = CorpusType.findByName("Surgical Pathology Reports")?: new CorpusType(name:"Surgical Pathology Reports", description:"surgical path reports from CDR", enabled:true, glyph:"icon-i-pathology").save(flush:true, failOnError:true)
			def surgPathIdx = Index.findByCommonName("Surgical Pathology Reports")?:new Index(commonName:"Surgical Pathology Reports", indexName:"surgical-path_v1", status:"Available", description:"surgical pathology reports", numberOfShards:6, numberOfReplicas:0)
			def surgPathType = Type.findByTypeName("report")?:new Type(typeName:"report", description:"CDR surgical path report", environment:Environment.current.name, corpusType:surgPathCorpusType)
			def report = Field.findByFieldName("report")?:new Field(fieldName:"report",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"surgical pathology report text", defaultSearchField:true)
			def surgPathContextFilterField = Field.findByFieldNameAndType("authorized_context_filter_value", null)?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:true )
			def pathCui = Field.findByFieldNameAndType("cuis", null)?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline")
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
					fp.label = "UMLS CUI"
					fp.numberOfFilterOptions = 50
				}
				f.addToPreferences(fp)
			}
			surgPathIdx.addToTypes(surgPathType)
			nlp05.addToIndexes(surgPathIdx)
			println nlp05.toString()
			nlp05.save(failOnError:true, flush:true)
			
			//PREFS SANITY CHECK
			configService.clonePreferences(User.findByUsername("rmcewan"))
			println "preferences set for rmcewan"
		}
		
    }
    def destroy = {
    }
	
}
	