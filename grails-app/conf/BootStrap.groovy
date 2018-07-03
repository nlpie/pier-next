import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.elastic.*
import edu.umn.nlpie.pier.springsecurity.Role
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.springsecurity.UserRole
import edu.umn.nlpie.pier.ui.Corpus
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology
import grails.util.Environment


class BootStrap {
	
	def indexService
	def configService

    def init = { servletContext ->
		
		if (Environment.current != Environment.PRODUCTION) {
			println "Bootstrap: ${Environment.current.name}"
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
		def superadmin = Role.findByAuthority("ROLE_SUPERADMIN")?:new Role(authority:"ROLE_SUPERADMIN").save(flush:true)
		def admin = Role.findByAuthority("ROLE_ADMIN")?:new Role(authority:"ROLE_ADMIN").save(flush:true)
		def u = Role.findByAuthority("ROLE_USER")?:new Role(authority:"ROLE_USER").save(flush:true)
		def beta = Role.findByAuthority("ROLE_BETA_USER")?:new Role(authority:"ROLE_BETA_USER").save(flush:true)
		def analyst = Role.findByAuthority("ROLE_ANALYST")?:new Role(authority:"ROLE_ANALYST").save(flush:true)
		//UserRole.create(rmcewan, superadmin)
		def rmcewan = User.findByUsername("rmcewan")?:new User(username:"rmcewan",password:"umn").save(failOnError:true)
		UserRole.create(rmcewan, u)
		UserRole.create(rmcewan, beta)
		UserRole.create(rmcewan, analyst)
		UserRole.create(rmcewan, superadmin)
		def rmcewan1 = User.findByUsername("rmcewan1")?:new User(username:"rmcewan1",password:"umn").save(failOnError:true)
		UserRole.create(rmcewan1, u)
		UserRole.create(rmcewan1, beta)
		UserRole.create(rmcewan1, analyst)
		UserRole.create(rmcewan1, superadmin)
		
		
		//populate elastic data
		Cluster.withSession { session ->
			def user = User.findByUsername("rmcewan")?:new User(username:"rmcewan",password:"umn").save(failOnError:true)
			def app = User.findByUsername("nlppier")?:new User(username:"nlppier",password:"${this.generatePassword()}",enabled:false).save(failOnError:true)
			
			def epicOntology = Ontology.findByName('Epic Categories')?:new Ontology(name:'Epic Categories', description:"what shows in Epic").save(flush:true, failOnError:true)
			def epicHL7LoincOntology = Ontology.findByName('HL7 LOINC')?:new Ontology(name:'HL7 LOINC', description:"DO Axis values").save(flush:true, failOnError:true)
			def biomedicus = Ontology.findByName('NLP Annotations')?:new Ontology(name:'NLP Annotations', description:"NLP annotations").save(flush:true, failOnError:true)
			
			
			def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://nlp05.ahc.umn.edu:9200",environment:Environment.current.name,description:"test cluster (to be prod)", commonName:"test cluster")
			//def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://localhost:9200",environment:Environment.current.name,description:"test cluster (to be prod)", commonName:"test cluster")
			if ( Environment.current.name=="fvdev" ) {
				nlp05.uri = "http://localhost:9200"
			}
			if ( Environment.current.name=="fvtest" ) {
				nlp05.uri = "http://nlp02.fairview.org:9200"
			}
			def epicNotesIdx = Index.findByCommonName("Epic Notes")?:new Index(commonName:"Epic Notes", indexName:"notes_v3",  status:"Searchable", description:"clinical epic notes", numberOfShards:6, numberOfReplicas:0, environment:Environment.current.name)//.save(flush:true, failOnError:true)
			
			def termExpansionIdx = Index.findByCommonName("word2vec nearest terms")?:new Index(commonName:"word2vec nearest terms", indexName:"expansion_v1",  status:"Functional", description:"semantially related terms and misspellings for each term in corpus", numberOfShards:1, numberOfReplicas:0, environment:Environment.current.name, isTermExpansionIndex:true)//.save(flush:true, failOnError:true)
			def termExpansionType = Type.findByTypeName("word")?:new Type(typeName:"word", description:"w2v type", environment:Environment.current.name)
			termExpansionIdx.type = termExpansionType
			nlp05.addToIndexes(termExpansionIdx)
			
			def noteId = Field.findByFieldName("note_id")?:new Field(fieldName:"note_id",dataTypeName:"LONG", description:"Epic note id", aggregatable:false)
			def mrn = Field.findByFieldName("mrn")?:new Field(fieldName:"mrn",dataTypeName:"NOT_ANALYZED_STRING", description:"Epic patient identifier", aggregatable:true)
			def encounterId = Field.findByFieldName("encounter_id")?:new Field(fieldName:"encounter_id",dataTypeName:"LONG", description:"Epic visit number", aggregatable:true)
			def serviceDate = Field.findByFieldName("service_date")?:new Field(fieldName:"service_date",dataTypeName:"DATE", description:"Date of Service", aggregatable:true, exportable:true)
			def filingDatetime = Field.findByFieldName("filing_datetime")?:new Field(fieldName:"filing_datetime",dataTypeName:"DATETIME", description:"When note was filed", aggregatable:true, exportable:true)
			def eds = Field.findByFieldName("encounter_department_specialty")?:new Field(fieldName:"encounter_department_specialty",dataTypeName:"NOT_ANALYZED_STRING", description:"Specialty name in Epic", aggregatable:true, exportable:true)
			def ec = Field.findByFieldName("encounter_center")?:new Field(fieldName:"encounter_center",dataTypeName:"NOT_ANALYZED_STRING", description:"Encounter center name in Epic", aggregatable:true, exportable:true)
			def ect = Field.findByFieldName("encounter_clinic_type")?:new Field(fieldName:"encounter_clinic_type",dataTypeName:"NOT_ANALYZED_STRING", description:"Clinic type in Epic", aggregatable:true, exportable:true)			
			
			def pt = Field.findByFieldName("prov_type")?:new Field(fieldName:"prov_type",dataTypeName:"NOT_ANALYZED_STRING", description:"Provider type in Epic name", aggregatable:true, exportable:true)
			
			def role = Field.findByFieldName("role")?:new Field(fieldName:"role",dataTypeName:"NOT_ANALYZED_STRING", description:"Provider role axis in HL7-LOINC DO", aggregatable:true, exportable:true)
			def smd = Field.findByFieldName("smd")?:new Field(fieldName:"smd",dataTypeName:"NOT_ANALYZED_STRING", description:"subject matter domain", aggregatable:true, exportable:true)
			def text = Field.findByFieldName("text")?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"document text", defaultSearchField:true, aggregatable:false)
			def contextFilter = Field.findByFieldName("authorized_context_filter_value")?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:true, aggregatable:false )
			def cui = Field.findByFieldName("cuis")?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline", aggregatable:true, significantTermsAggregatable:true)

			def clinicalCorpus = Corpus.findByName("Clinical Notes")?: new Corpus(name:"Clinical Notes", description:"notes from Epic", enabled:true, glyph:"fa-file-text-o").save(flush:true, failOnError:true)
			
			def noteType = Type.findByTypeName("note")?:new Type(typeName:"note", description:"CDR note", environment:Environment.current.name)
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
			noteType.addToFields(ec)
			noteType.addToFields(ect)
			noteType.addToFields(pt)
			noteType.fields.each { f ->
				println "adding pref for ${f.fieldName}"
				def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
				if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
				if ( f.fieldName=="text" || f.fieldName=="filing_datetime" || f.fieldName=="encounter_id" ) fp.aggregate=false
				if ( f.fieldName=="role" || f.fieldName=="smd" ) fp.ontology=epicHL7LoincOntology
				if ( f.fieldName=="cuis" ) {
					fp.ontology=biomedicus
					fp.label = "Medical Concepts"
					fp.numberOfFilterOptions = 25
					fp.aggregate = false
				}
				if ( f.fieldName=="mrn" || f.fieldName=="note_id") {
					fp.computeDistinct = true
					//if ( f.fieldName=="note_id" || f.fieldName=="mrn" ) fp.aggregate = false
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
			
			def surgPathIdx = Index.findByCommonName("Surgical Pathology Reports")?:new Index(commonName:"Surgical Pathology Reports", indexName:"surgical-path_v1", status:"Available", description:"surgical pathology reports", numberOfShards:6, numberOfReplicas:0,environment:Environment.current.name)
			def surgPathType = Type.findByTypeName("report")?:new Type(typeName:"report", description:"CDR surgical path report", environment:Environment.current.name)//, Corpus:surgPathCorpus)
			def report = Field.findByFieldName("report")?:new Field(fieldName:"report",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"surgical pathology report text", defaultSearchField:true,, aggregatable:false)
			def surgPathContextFilterField = Field.findByFieldNameAndType("authorized_context_filter_value", null)?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:true, aggregatable:false )
			def pathCui = Field.findByFieldNameAndType("cuis", null)?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline", aggregatable:true, exportable:true, significantTermsAggregatable:true)
			
			def pathCollDate = Field.findByFieldNameAndType("collection_datetime", null)?:new Field(fieldName:"collection_datetime", dataTypeName:"DATETIME", description:"Specimen collection time", aggregatable:true, exportable:true)
			def procCode = Field.findByFieldNameAndType("proc_code", null)?:new Field(fieldName:"proc_code", dataTypeName:"NOT_ANALYZED_STRING", description:"Specimen collection procedure code", aggregatable:true, exportable:true)
			def procName = Field.findByFieldNameAndType("proc_name", null)?:new Field(fieldName:"proc_name", dataTypeName:"NOT_ANALYZED_STRING", description:"Specimen collection procedure name", aggregatable:true, exportable:true)
			def authProv = Field.findByFieldNameAndType("authorizing_prov_name", null)?:new Field(fieldName:"authorizing_prov_name", dataTypeName:"NOT_ANALYZED_STRING", description:"Authorizing provider", aggregatable:true, exportable:true)
			def resultsInterp = Field.findByFieldNameAndType("rslts_interpreter", null)?:new Field(fieldName:"rslts_interpreter", dataTypeName:"NOT_ANALYZED_STRING", description:"Name of provider interpreting results ", aggregatable:true, exportable:true)
			
			surgPathType.addToFields(report)
			surgPathType.addToFields(surgPathContextFilterField)
			surgPathType.addToFields(pathCollDate)
			surgPathType.addToFields(procCode)
			surgPathType.addToFields(procName)
			surgPathType.addToFields(authProv)
			surgPathType.addToFields(resultsInterp)
			//surgPathType.addToFields(pathCui)
			surgPathType.fields.each { f ->
				println "adding surg path pref for ${f.fieldName}"
				def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
				if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
				if ( f.fieldName=="report") fp.aggregate=false
				if ( f.fieldName=="cuis" ) {
					fp.ontology=biomedicus
					fp.label = "Medical Concepts"
					fp.numberOfFilterOptions = 10
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
	