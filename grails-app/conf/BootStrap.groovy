import org.apache.commons.lang.RandomStringUtils

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

	//DO THIS BEFORE SHUTTING DOWN THE INSTANCE
	//app refresh - db exports: user, user_authentication_event, query
	
    def init = { servletContext ->
	
		println "--START BOOTSTRAP--"
		
		//make sure nlppier user and certain roles exist
		User.findByUsername("nlppier")?:new User(username:"nlppier",password:"${configService.generatePassword()}",enabled:false).save(failOnError:true)
		def superadmin = Role.findByAuthority("ROLE_SUPERADMIN")?:new Role(authority:"ROLE_SUPERADMIN").save(flush:true)
		def admin = Role.findByAuthority("ROLE_ADMIN")?:new Role(authority:"ROLE_ADMIN").save(flush:true)
		def user = Role.findByAuthority("ROLE_USER")?:new Role(authority:"ROLE_USER").save(flush:true)
		def analyst = Role.findByAuthority("ROLE_ANALYST")?:new Role(authority:"ROLE_ANALYST").save(flush:true)
		def cardio = Role.findByAuthority("ROLE_CARDIOLOGY")?:new Role(authority:"ROLE_CARDIOLOGY").save(flush:true)
		def cancer = Role.findByAuthority("ROLE_CANCER")?:new Role(authority:"ROLE_CANCER").save(flush:true)
		
		if ( Environment.current == Environment.DEVELOPMENT ||  Environment.current.name=="fvdev" || Environment.current.name=="fvtest" ) {
		//Environment.current == Environment.TEST ||
		//if (true==false) {
			println "${Environment.current.name} RECREATING INDEXES/CORPORA"
			//populate elastic data
			Cluster.withSession { session ->
				def app = configUser("nlppier", [])
				
				def epicOntology = Ontology.findByName('Epic Categories')?:new Ontology(name:'Epic Categories', description:"what shows in Epic").save(flush:true, failOnError:true)
				def epicHL7LoincOntology = Ontology.findByName('HL7 LOINC Document Ontology')?:new Ontology(name:'HL7 LOINC Document Ontology', description:"HL7 LOINC Document Ontology axis values").save(flush:true, failOnError:true)
				def biomedicus = Ontology.findByName('NLP Annotations')?:new Ontology(name:'NLP Annotations', description:"NLP annotations").save(flush:true, failOnError:true)
				def mimicOntology = Ontology.findByName('MIMIC Data Element Ontology')?:new Ontology(name:'MIMIC Data Element Ontology', description:"MIMIC data elements").save(flush:true, failOnError:true)
				
				
				def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://nlp05.ahc.umn.edu:9200",environment:Environment.current.name,description:"test cluster (to be prod)", commonName:"test cluster")
				//def nlp05 = Cluster.findByClusterName("nlp05")?:new Cluster(clusterName:"nlp05",uri:"http://localhost:9200",environment:Environment.current.name,description:"test cluster (to be prod)", commonName:"test cluster")
				if ( Environment.current.name=="fvdev" ) {
					nlp05.uri = "http://localhost:9200"
				}
				if ( Environment.current.name=="fvtest" ) {
					nlp05.uri = "http://nlp02.fairview.org:9200"
				}
				def epicNotesIdx = Index.findByCommonName("Epic Notes")?:new Index(commonName:"Epic Notes", indexName:"notes_v3",  status:"Searchable", description:"clinical epic notes", numberOfShards:6, numberOfReplicas:0, environment:Environment.current.name)
				
				def termExpansionIdx = Index.findByCommonName("word2vec nearest terms")?:new Index(commonName:"word2vec nearest terms", indexName:"expansion_v1",  status:"Functional", description:"semantially related terms and misspellings for each term in corpus", numberOfShards:1, numberOfReplicas:0, environment:Environment.current.name, isTermExpansionIndex:true)
				def termExpansionType = Type.findByTypeName("word")?:new Type(typeName:"word", description:"w2v type", environment:Environment.current.name)
				termExpansionIdx.type = termExpansionType
				nlp05.addToIndexes(termExpansionIdx)
				
				//note note-related
				def noteId = Field.findByFieldName("note_id")?:new Field(fieldName:"note_id",dataTypeName:"LONG", description:"Epic note id", aggregatable:false, exportable:true)
				def noteVersionId = Field.findByFieldName("note_version_id")?:new Field(fieldName:"note_version_id",dataTypeName:"LONG", description:"Epic note version id", aggregatable:false, exportable:true)
				def hl7NoteId = Field.findByFieldName("hl7_note_id")?:new Field(fieldName:"hl7_note_id",dataTypeName:"LONG", description:"Realtime HL7 interface primary key for this note", aggregatable:false, exportable:true)
				def authContext = Field.findByFieldName("authorized_context")?:new Field(fieldName:"authorized_context",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:false, aggregatable:false )
				def contextFilter = Field.findByFieldName("authorized_context_filter_value")?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"LONG", description:"Array of filter values that govern access to this note",contextFilterField:true, aggregatable:false )
				def filingDate = Field.findByFieldName("filing_date")?:new Field(fieldName:"filing_date",dataTypeName:"DATE", description:"Date note was filed", aggregatable:false, exportable:true)
				def filingDatetime = Field.findByFieldName("filing_datetime")?:new Field(fieldName:"filing_datetime",dataTypeName:"DATETIME", description:"Date/time note was filed", aggregatable:false, exportable:true)
				//def noteDatetime = Field.findByFieldName("note_datetime")?:new Field(fieldName:"note_datetime",dataTypeName:"DATETIME", description:"Clarity date/time of note - alternative for filing datetime?", aggregatable:true, exportable:true)
				def textLength = Field.findByFieldName("text_length")?:new Field(fieldName:"text_length",dataTypeName:"INTEGER", description:"length of analyzed note", aggregatable:false, exportable:true)
				def textSourceFormat = Field.findByFieldName("text_source_format")?:new Field(fieldName:"text_source_format",dataTypeName:"NOT_ANALYZED_STRING", description:"plain text, rich text, format of analyzed note",contextFilterField:false, aggregatable:true, exportable:true )
				def text = Field.findByFieldName("text")?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"document text", defaultSearchField:true, aggregatable:false, exportable:true)
				/*
				"note_id": 603201078,
				"note_version_id": 552990432,
				"hl7_note_id":
				"authorized_context":
				"authorized_context_filter_value"
				"filing_date": "2014-07-31",
				"filing_datetime": "2014-07-31 16:00",
				"note_datetime": "2014-07-31 16:00",
				"text_length": 58,
				"text_source_format": "clarity_plain_text",
				"text": "  This patient was a no show for this scheduled appointment.  ",
				*/
				
				//pt-related
				def mrn = Field.findByFieldName("mrn")?:new Field(fieldName:"mrn",dataTypeName:"NOT_ANALYZED_STRING", description:"Epic patient identifier", aggregatable:true, exportable:true)
				def patientId = Field.findByFieldNameAndType("patient_id", null)?:new Field(fieldName:"patient_id", dataTypeName:"LONG", description:"CDR Patient ID", aggregatable:true, exportable:true)
				/*"patient_id": 19813340237,
				"mrn": "0029583181",*/
				
				
				//encounter-related
				def encounterId = Field.findByFieldName("encounter_id")?:new Field(fieldName:"encounter_id",dataTypeName:"LONG", description:"Epic visit number", aggregatable:true, exportable:true)
				def serviceId = Field.findByFieldName("service_id")?:new Field(fieldName:"service_id",dataTypeName:"LONG", description:"CDR encounter identifier", aggregatable:true, exportable:true)
				def serviceDate = Field.findByFieldName("service_date")?:new Field(fieldName:"service_date",dataTypeName:"DATE", description:"Date of Service", aggregatable:true, exportable:true)
				def eds = Field.findByFieldName("encounter_department_specialty")?:new Field(fieldName:"encounter_department_specialty",dataTypeName:"NOT_ANALYZED_STRING", description:"Specialty name in Epic", aggregatable:true, exportable:true)
				def ec = Field.findByFieldName("encounter_center")?:new Field(fieldName:"encounter_center",dataTypeName:"NOT_ANALYZED_STRING", description:"Encounter center name in Epic", aggregatable:true, exportable:true)
				def ect = Field.findByFieldName("encounter_clinic_type")?:new Field(fieldName:"encounter_clinic_type",dataTypeName:"NOT_ANALYZED_STRING", description:"Encounter Clinic type in Epic", aggregatable:true, exportable:true)	
				def ed = Field.findByFieldName("encounter_department")?:new Field(fieldName:"encounter_department",dataTypeName:"NOT_ANALYZED_STRING", description:"Encounter department in Epic", aggregatable:true, exportable:true)
				def ecType = Field.findByFieldName("encounter_center_type")?:new Field(fieldName:"encounter_center_type",dataTypeName:"NOT_ANALYZED_STRING", description:"Encounter center type in Epic", aggregatable:true, exportable:true)
				def di = Field.findByFieldName("department_id")?:new Field(fieldName:"department_id",dataTypeName:"NOT_ANALYZED_STRING", description:"CDR department identifier", aggregatable:true, exportable:true)
				
				/*"encounter_id": 108211423,
				"service_id": 48855854232,
				"service_date": "2014-07-31",
				"encounter_department_specialty": "Family Practice",
				"encounter_center": "Fairview Clinics Prior Lake",
				"encounter_clinic_type": "Primary Care",
				"encounter_department": "RV FAMILY PRACTICE",
				"encounter_center_type": "CLINIC",
				"department_id": "FV:DEPARTMENT_ID:58504",*/
				
				
				//provider-related
				def pt = Field.findByFieldName("prov_type")?:new Field(fieldName:"prov_type",dataTypeName:"NOT_ANALYZED_STRING", description:"Provider type in Epic name", aggregatable:true, exportable:true)
				def providerId = Field.findByFieldName("provider_id")?:new Field(fieldName:"provider_id",dataTypeName:"LONG", description:"CDR department identifier", aggregatable:true, exportable:true)
				def provId = Field.findByFieldName("prov_id")?:new Field(fieldName:"prov_id",dataTypeName:"NOT_ANALYZED_STRING", description:"Provider ID in Epic", aggregatable:true, exportable:true)
				def provName = Field.findByFieldName("prov_name")?:new Field(fieldName:"prov_name",dataTypeName:"NOT_ANALYZED_STRING", description:"Provider name in Epic", aggregatable:true, exportable:true)
				
				/*
				"prov_type": "Medical Assistant",
				"provider_id": "33900385981",
				"prov_id": "CSTEINB2",
				"prov_name": "STEINBERG, CHERYL",*/
				
				//HL7-LOINC_DO
				def role = Field.findByFieldName("role")?:new Field(fieldName:"role",dataTypeName:"NOT_ANALYZED_STRING", description:"Role axis in HL7-LOINC DO", aggregatable:true, exportable:true)
				def smd = Field.findByFieldName("smd")?:new Field(fieldName:"smd",dataTypeName:"NOT_ANALYZED_STRING", description:"Subject Matter Domain axis in HL7-LOINC DO", aggregatable:true, exportable:true)
				def kod = Field.findByFieldName("kod")?:new Field(fieldName:"kod",dataTypeName:"NOT_ANALYZED_STRING", description:"Kind of Document axis in HL7-LOINC DO", aggregatable:true, exportable:true)
				def tos = Field.findByFieldName("tos")?:new Field(fieldName:"tos",dataTypeName:"NOT_ANALYZED_STRING", description:"Subject Matter Domain axis in HL7-LOINC DO", aggregatable:true, exportable:true)
				def setting = Field.findByFieldName("setting")?:new Field(fieldName:"setting",dataTypeName:"NOT_ANALYZED_STRING", description:"Setting axis in HL7-LOINC DO", aggregatable:true, exportable:true)
				
				/*
				"role": "9.g. Registered Nurse",
				"smd": "12. Family Medicine",
				"kod": "7. Note",
				"tos": "9.k.1. Progress Note",
				"setting": "null",*/
				
				
				
				
				
				//B9-related
				def cui = Field.findByFieldName("cuis")?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline", aggregatable:true, exportable:true, significantTermsAggregatable:true)
				//def lowCui = Field.findByFieldName("low_confidence_cuis")?:new Field(fieldName:"low_confidence_cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline, lower confidence detection", aggregatable:true, exportable:true, significantTermsAggregatable:true)
				/*"cui": [
				         "C0030705"
				         ],
				"low_confidence_cui": [
						"C1658449"
				]*/
	
				def clinicalCorpus = Corpus.findByName("Clinical Notes")?: new Corpus(name:"Clinical Notes", description:"Clinical notes from Epic EHR", enabled:true, glyph:"fa-file-text-o", minimumRole:analyst).save(flush:true, failOnError:true)
				
				def noteType = Type.findByTypeName("note")?:new Type(typeName:"note", description:"Clinical note in Epic", environment:Environment.current.name)
				noteType.addToFields(noteId)
				noteType.addToFields(noteVersionId)
				noteType.addToFields(hl7NoteId)
				noteType.addToFields(authContext)
				noteType.addToFields(contextFilter)
				noteType.addToFields(filingDate)
				noteType.addToFields(filingDatetime)
				noteType.addToFields(textLength)
				noteType.addToFields(textSourceFormat)
				noteType.addToFields(text)
				
				noteType.addToFields(mrn)
				noteType.addToFields(patientId)
				
				noteType.addToFields(encounterId)
				noteType.addToFields(serviceId)
				noteType.addToFields(serviceDate)
				noteType.addToFields(eds)
				noteType.addToFields(ec)
				noteType.addToFields(ect)
				noteType.addToFields(ed)
				noteType.addToFields(ecType)
				noteType.addToFields(di)
				
				noteType.addToFields(pt)
				noteType.addToFields(providerId)
				noteType.addToFields(provId)
				noteType.addToFields(provName)
				
				noteType.addToFields(role)
				noteType.addToFields(smd)
				noteType.addToFields(kod)
				noteType.addToFields(tos)
				noteType.addToFields(setting)

				noteType.addToFields(cui)
				//noteType.addToFields(lowCui)
				
				noteType.fields.each { f ->
					println "adding pref for ${f.fieldName}"
					def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
					
					if ( f.contextFilterField || f.defaultSearchField || f.fieldName=="text") fp.aggregate=false
					
					if ( f.fieldName=="cuis" ) {
						fp.ontology=biomedicus
						fp.label = "Medical Concepts"
						fp.numberOfFilterOptions = 20
						fp.aggregate = true
					}
					if ( f.fieldName=="low_confidence_cuis" ) {
						fp.ontology=biomedicus
						fp.label = "Low Confidence Medical Concepts"
						fp.numberOfFilterOptions = 10
						fp.aggregate = true
					}
					
					if ( f.fieldName=="mrn" || f.fieldName=="patient_id" || f.fieldName=="encounter_id" || f.fieldName=="provider_id" || f.fieldName=="service_id" || f.fieldName=="department_id" ) {
						fp.computeDistinct = true
					}

					if ( f.fieldName=="role" || f.fieldName=="smd" || f.fieldName=="setting" || f.fieldName=="kod" || f.fieldName=="tos" ) fp.ontology=epicHL7LoincOntology
					
					f.addToPreferences(fp)
				}
				epicNotesIdx.type = noteType
				nlp05.addToIndexes(epicNotesIdx)
		
				nlp05.save(failOnError:true, flush:true)
				//done with clinical notes config
				clinicalCorpus.addToIndexes(epicNotesIdx)
				clinicalCorpus.save(flush:true, failOnError:true)
				
				
				
				
				
				//SURG PATH REPORTS INDEX
				def surgPathCorpus = Corpus.findByName("Surgical Pathology Reports")?: new Corpus(name:"Surgical Pathology Reports", description:"surgical path reports from CDR", enabled:true, glyph:"icon-i-pathology", minimumRole:analyst).save(flush:true, failOnError:true)
				
				def surgPathIdx = Index.findByCommonName("Surgical Pathology Reports")?:new Index(commonName:"Surgical Pathology Reports", indexName:"surgical-path_v1", status:"Searchable", description:"surgical pathology reports", numberOfShards:6, numberOfReplicas:0,environment:Environment.current.name)
				def surgPathType = Type.findByTypeName("report")?:new Type(typeName:"report", description:"CDR surgical path report", environment:Environment.current.name)
				def report = Field.findByFieldName("report")?:new Field(fieldName:"report",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"surgical pathology report text", defaultSearchField:true,, aggregatable:false)
				def surgPathContextFilterField = Field.findByFieldNameAndType("authorized_context_filter_value", null)?:new Field(fieldName:"authorized_context_filter_value",dataTypeName:"NOT_ANALYZED_STRING", description:"Array of search contexts that include this note",contextFilterField:true, aggregatable:false )
				def pathCui = Field.findByFieldNameAndType("cuis", null)?:new Field(fieldName:"cuis", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline", aggregatable:true, exportable:true, significantTermsAggregatable:true)
				
				def pathCollDate = Field.findByFieldNameAndType("collection_datetime", null)?:new Field(fieldName:"collection_datetime", dataTypeName:"DATETIME", description:"Specimen collection date/time", aggregatable:true, exportable:false)
				def pathResultDate = Field.findByFieldNameAndType("result_datetime", null)?:new Field(fieldName:"result_datetime", dataTypeName:"DATETIME", description:"Result date/time", aggregatable:false, exportable:false)
				def procCode = Field.findByFieldNameAndType("proc_code", null)?:new Field(fieldName:"proc_code", dataTypeName:"NOT_ANALYZED_STRING", description:"Specimen collection procedure code", aggregatable:true, exportable:true)
				def procName = Field.findByFieldNameAndType("proc_name", null)?:new Field(fieldName:"proc_name", dataTypeName:"NOT_ANALYZED_STRING", description:"Specimen collection procedure name", aggregatable:true, exportable:true)
				def authProv = Field.findByFieldNameAndType("authorizing_prov_name", null)?:new Field(fieldName:"authorizing_prov_name", dataTypeName:"NOT_ANALYZED_STRING", description:"Authorizing provider", aggregatable:true, exportable:true)
				def resultsInterp = Field.findByFieldNameAndType("rslts_interpreter", null)?:new Field(fieldName:"rslts_interpreter", dataTypeName:"NOT_ANALYZED_STRING", description:"Name of provider interpreting results ", aggregatable:true, exportable:true)
				
				def specNumber = Field.findByFieldNameAndType("specimen_number", null)?:new Field(fieldName:"specimen_number", dataTypeName:"NOT_ANALYZED_STRING", description:"Specimen number", aggregatable:true, exportable:true)
				def pathOrderId = Field.findByFieldNameAndType("order_id", null)?:new Field(fieldName:"order_id", dataTypeName:"NOT_ANALYZED_STRING", description:"CDR Order ID", aggregatable:true, exportable:true)
				def pathServiceId = Field.findByFieldNameAndType("service_id", null)?:new Field(fieldName:"service_id", dataTypeName:"LONG", description:"CDR Service ID", aggregatable:true, exportable:true)
				def pathPatientId = Field.findByFieldNameAndType("patient_id", null)?:new Field(fieldName:"patient_id", dataTypeName:"LONG", description:"CDR Patient ID", aggregatable:true, exportable:true)
				def pathReportLen = Field.findByFieldNameAndType("report_length", null)?:new Field(fieldName:"report_length", dataTypeName:"INTEGER", description:"Character length of report", aggregatable:false, exportable:true)
				
				surgPathType.addToFields(pathCui)
				surgPathType.addToFields(report)
				surgPathType.addToFields(surgPathContextFilterField)
				surgPathType.addToFields(pathCollDate)
				surgPathType.addToFields(pathResultDate)
				surgPathType.addToFields(procCode)
				surgPathType.addToFields(procName)
				surgPathType.addToFields(authProv)
				surgPathType.addToFields(resultsInterp)
				surgPathType.addToFields(specNumber)
				surgPathType.addToFields(pathOrderId)
				surgPathType.addToFields(pathServiceId)
				surgPathType.addToFields(pathPatientId)
				surgPathType.addToFields(pathReportLen)
				
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
					if ( f.fieldName=="mrn" || f.fieldName=="patient_id") {
						fp.computeDistinct = true
						//if ( f.fieldName=="note_id" || f.fieldName=="mrn" ) fp.aggregate = false
					}
					f.addToPreferences(fp)
				}
				surgPathIdx.type = surgPathType
				nlp05.addToIndexes(surgPathIdx)
				println nlp05.toString()
				nlp05.save(failOnError:true, flush:true)
				
				surgPathCorpus.addToIndexes(surgPathIdx)
				surgPathCorpus.save(flush:true, failOnError:true)
				
				//EF
				def efCorpus = Corpus.findByName("Echo Reports")?: new Corpus(name:"Echo Reports", description:"Non-scanned Echo reports", enabled:true, glyph:"fa-heart-o", minimumRole:cardio).save(flush:true, failOnError:true)
				def efIdx = Index.findByCommonName("Echo Reports")?:new Index(commonName:"Echo Reports", indexName:"ef_v1", status:"Searchable", description:"Non-scanned Echo reports", numberOfShards:1, numberOfReplicas:0,environment:Environment.current.name)
				def efType = Type.findByTypeName("summary")?:new Type(typeName:"summary", description:"Narrative, Impression, or Result Comment", environment:Environment.current.name)
				
				def efText = Field.findByFieldNameAndType("text", null)?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"content of Narrative, Impression, or Result Comment", defaultSearchField:true,, aggregatable:false)
				def efTextSrc = Field.findByFieldNameAndType("text_source", null)?:new Field(fieldName:"text_source", dataTypeName:"NOT_ANALYZED_STRING", description:"column(s) in which text data was found", aggregatable:true, exportable:true)
				def efTextFmt = Field.findByFieldNameAndType("text_format", null)?:new Field(fieldName:"text_format", dataTypeName:"NOT_ANALYZED_STRING", description:"whether analyzed text contains new line formatting", aggregatable:true, exportable:true)
				
				def efMeasures = Field.findByFieldNameAndType("ef_measures", null)?:new Field(fieldName:"ef_measures", dataTypeName:"NOT_ANALYZED_STRING", description:"measures", aggregatable:true, exportable:true)
				def efResults = Field.findByFieldNameAndType("ef_results", null)?:new Field(fieldName:"ef_results", dataTypeName:"NOT_ANALYZED_STRING", description:"measures and values", aggregatable:false, exportable:true)
				
				def efOrderDttm = Field.findByFieldNameAndType("order_datetime", null)?:new Field(fieldName:"order_datetime", dataTypeName:"DATETIME", description:"", aggregatable:true, exportable:true)
				def efProcStartDttm = Field.findByFieldNameAndType("procedure_start_datetime", null)?:new Field(fieldName:"procedure_start_datetime", dataTypeName:"DATETIME", description:"", aggregatable:true, exportable:true)
				def efOrdResDttm = Field.findByFieldNameAndType("order_result_datetime", null)?:new Field(fieldName:"order_result_datetime", dataTypeName:"DATETIME", description:"", aggregatable:true, exportable:true)
				def efParentOrdDttm = Field.findByFieldNameAndType("parent_order_datetime", null)?:new Field(fieldName:"parent_order_datetime", dataTypeName:"DATETIME", description:"", aggregatable:true, exportable:true)
				def efCollDttm = Field.findByFieldNameAndType("collection_datetime", null)?:new Field(fieldName:"collection_datetime", dataTypeName:"DATETIME", description:"", aggregatable:true, exportable:true)
				def efResResDttm = Field.findByFieldNameAndType("result_result_datetime", null)?:new Field(fieldName:"result_result_datetime", dataTypeName:"DATETIME", description:"", aggregatable:true, exportable:true)
				
				def efServiceId = Field.findByFieldNameAndType("service_id",null)?:new Field(fieldName:"service_id",dataTypeName:"LONG", description:"CDR service id", aggregatable:true)
				def efPatientId = Field.findByFieldNameAndType("patient_id",null)?:new Field(fieldName:"patient_id",dataTypeName:"LONG", description:"CDR patient id", aggregatable:true)
				def efMrn = Field.findByFieldNameAndType("mrn", null)?:new Field(fieldName:"mrn", dataTypeName:"NOT_ANALYZED_STRING", description:"Medical record number", aggregatable:true, exportable:true)
				def efOrdId = Field.findByFieldNameAndType("order_id", null)?:new Field(fieldName:"order_id", dataTypeName:"NOT_ANALYZED_STRING", description:"record identifier in CDR", aggregatable:false, exportable:true)
				def efOrdResId = Field.findByFieldNameAndType("order_result_id", null)?:new Field(fieldName:"order_result_id", dataTypeName:"NOT_ANALYZED_STRING", description:"record identifier in CDR", aggregatable:false, exportable:true)
				
				def efOrderTypeOrig = Field.findByFieldNameAndType("order_type_orig", null)?:new Field(fieldName:"order_type_orig", dataTypeName:"NOT_ANALYZED_STRING", description:"order type orig", aggregatable:true, exportable:true)
				def efOrderingModeOrig = Field.findByFieldNameAndType("ordering_mode_orig", null)?:new Field(fieldName:"ordering_mode_orig", dataTypeName:"NOT_ANALYZED_STRING", description:"orig order mode", aggregatable:true, exportable:true)
				def efTestNameOrig = Field.findByFieldNameAndType("test_name_orig", null)?:new Field(fieldName:"test_name_orig", dataTypeName:"NOT_ANALYZED_STRING", description:"test name orig", aggregatable:true, exportable:true)
				
				def efProcCode = Field.findByFieldNameAndType("procedure_code", null)?:new Field(fieldName:"procedure_code", dataTypeName:"NOT_ANALYZED_STRING", description:"procedure code", aggregatable:true, exportable:true)
				def efProcName = Field.findByFieldNameAndType("procedure_name", null)?:new Field(fieldName:"procedure_name", dataTypeName:"NOT_ANALYZED_STRING", description:"procedure name", aggregatable:true, exportable:true)
				
				def efProvId = Field.findByFieldNameAndType("provider_id",null)?:new Field(fieldName:"provider_id",dataTypeName:"LONG", description:"CDR provider id", aggregatable:false, exportable:true)
				def efAuthProvId = Field.findByFieldNameAndType("authorizing_provider_id",null)?:new Field(fieldName:"authorizing_provider_id",dataTypeName:"LONG", description:"CDR authorizing provider id", aggregatable:false, exportable:true)
				def efProv = Field.findByFieldNameAndType("provider", null)?:new Field(fieldName:"provider", dataTypeName:"NOT_ANALYZED_STRING", description:"provider name", aggregatable:true, exportable:true)
				def efAuthProv = Field.findByFieldNameAndType("authorizing_provider", null)?:new Field(fieldName:"authorizing_provider", dataTypeName:"NOT_ANALYZED_STRING", description:"authorizing provider name", aggregatable:true, exportable:true)
				
				efType.addToFields(efText)
				efType.addToFields(efTextSrc)
				efType.addToFields(efTextFmt)
				efType.addToFields(efMeasures)
				efType.addToFields(efResults)
				efType.addToFields(efOrderDttm)
				efType.addToFields(efProcStartDttm)
				efType.addToFields(efOrdResDttm)
				efType.addToFields(efParentOrdDttm)
				efType.addToFields(efCollDttm)
				efType.addToFields(efResResDttm)
				efType.addToFields(efOrdId)
				efType.addToFields(efPatientId)
				efType.addToFields(efMrn)
				efType.addToFields(efOrdId)
				efType.addToFields(efOrdResId)
				
				efType.addToFields(efOrderTypeOrig)
				efType.addToFields(efOrderingModeOrig)
				efType.addToFields(efTestNameOrig)
				
				efType.addToFields(efProcCode)
				efType.addToFields(efProcName)
				
				efType.addToFields(efProvId)
				efType.addToFields(efAuthProvId)
				efType.addToFields(efProv)
				efType.addToFields(efAuthProv)
				
				efType.fields.each { f ->
					println "adding EF pref for ${f.fieldName}"
					def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
					if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
					if ( f.fieldName=="text") fp.aggregate=false
					if ( f.fieldName=="procedure_name" || f.fieldName=="procedure_code" 
						|| f.fieldName=="ef_measures" || f.fieldName=="test_name_orig") fp.numberOfFilterOptions=100
					if ( f.fieldName=="mrn" || f.fieldName=="patient_id" ) { fp.computeDistinct = true
					}
				
					f.addToPreferences(fp)
				}
				efIdx.type = efType
				nlp05.addToIndexes(efIdx)
				
				
				efCorpus.addToIndexes(efIdx)
				efCorpus.save(flush:true, failOnError:true)
				
				println nlp05.toString()
				nlp05.save(failOnError:true, flush:true)
				
				//COPATH
				/*{
					"order_id": "FV:ORDER_ID:50948983",
					"order_result_id": "FV:ORDER_ID:COMPONENT_ID:LINE:ORD_DATE_REAL:50948983:2609:1:62018",
					"patient_id": 3823329754,
					"service_id": 10157966155,
					"result_datetime": "2010-03-29 00:00",
					"collection_datetime": null,
					"test_name_orig": "COPATH REPORT",
					"procedure_code": "LAB4613",
					"procedure_name": "SURGICAL PATHOLOGY EXAM",
					"text": null,
					"mrn": "005",
					"center": "Fairview Clinics Riverside Women's",
					"department_name": "ZWR OB/GYN"
				}*/
				
				def copathCorpus = Corpus.findByName("Copath Reports")?: new Corpus(name:"Copath Reports", description:"Copath reports via CDR", enabled:true, glyph:"icon-i-pathology", minimumRole:cancer).save(flush:true, failOnError:true)
				def copathIdx = Index.findByCommonName("Copath Reports")?:new Index(commonName:"Copath Reports", indexName:"copath_reports_v1", status:"Searchable", description:"Copath reports via CDR", numberOfShards:1, numberOfReplicas:0,environment:Environment.current.name)
				def copathType = Type.findByTypeNameAndIndex("report", null)?:new Type(typeName:"report", description:"Result Comment field in CDR for COPATH", environment:Environment.current.name)
				
				def copathText = Field.findByFieldNameAndType("text", null)?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"content result comment field", defaultSearchField:true, aggregatable:false)
				def copathTextFmt = Field.findByFieldNameAndType("text_format", null)?:new Field(fieldName:"text_format", dataTypeName:"NOT_ANALYZED_STRING", description:"whether analyzed text contains new line formatting", aggregatable:true, exportable:true)
				def copathCollDttm = Field.findByFieldNameAndType("collection_datetime", null)?:new Field(fieldName:"collection_datetime", dataTypeName:"DATETIME", description:"", aggregatable:false, exportable:true)
				def copathResDttm = Field.findByFieldNameAndType("result_datetime", null)?:new Field(fieldName:"result_datetime", dataTypeName:"DATETIME", description:"", aggregatable:false, exportable:true)
				def copathResDt = Field.findByFieldNameAndType("result_date", null)?:new Field(fieldName:"result_date", dataTypeName:"DATE", description:"", aggregatable:true, exportable:true)
				def copathOrdId = Field.findByFieldNameAndType("order_id", null)?:new Field(fieldName:"order_id", dataTypeName:"NOT_ANALYZED_STRING", description:"record identifier in CDR", aggregatable:false, exportable:true)
				def copathOrdResId = Field.findByFieldNameAndType("order_result_id", null)?:new Field(fieldName:"order_result_id", dataTypeName:"NOT_ANALYZED_STRING", description:"record identifier in CDR", aggregatable:false, exportable:true)
				def copathPatientId = Field.findByFieldNameAndType("patient_id",null)?:new Field(fieldName:"patient_id",dataTypeName:"LONG", description:"CDR patient id", aggregatable:true)
				def copathMrn = Field.findByFieldNameAndType("mrn", null)?:new Field(fieldName:"mrn", dataTypeName:"NOT_ANALYZED_STRING", description:"Medical record number", aggregatable:true, exportable:true)
				def copathServiceId = Field.findByFieldNameAndType("service_id",null)?:new Field(fieldName:"service_id",dataTypeName:"LONG", description:"CDR service id", aggregatable:true)
				def copathTestNameOrig = Field.findByFieldNameAndType("test_name_orig", null)?:new Field(fieldName:"test_name_orig", dataTypeName:"NOT_ANALYZED_STRING", description:"test name orig", aggregatable:true, exportable:true)
				def copathCenter = Field.findByFieldNameAndType("center", null)?:new Field(fieldName:"center", dataTypeName:"NOT_ANALYZED_STRING", description:"encounter center", aggregatable:true, exportable:true)
				def copathDeptName = Field.findByFieldNameAndType("department_name", null)?:new Field(fieldName:"department_name", dataTypeName:"NOT_ANALYZED_STRING", description:"department name", aggregatable:true, exportable:true)
				def copathProcCode = Field.findByFieldNameAndType("procedure_code", null)?:new Field(fieldName:"procedure_code", dataTypeName:"NOT_ANALYZED_STRING", description:"procedure code", aggregatable:true, exportable:true)
				def copathProcName = Field.findByFieldNameAndType("procedure_name", null)?:new Field(fieldName:"procedure_name", dataTypeName:"NOT_ANALYZED_STRING", description:"procedure name", aggregatable:true, exportable:true)
				
				copathType.addToFields(copathText)
				copathType.addToFields(copathTextFmt)
				copathType.addToFields(copathCollDttm)
				copathType.addToFields(copathResDttm)
				copathType.addToFields(copathResDt)
				copathType.addToFields(copathOrdId)
				copathType.addToFields(copathOrdResId)
				copathType.addToFields(copathPatientId)
				copathType.addToFields(copathMrn)
				copathType.addToFields(copathServiceId)
				copathType.addToFields(copathTestNameOrig)
				copathType.addToFields(copathCenter)
				copathType.addToFields(copathDeptName)
				copathType.addToFields(copathProcCode)
				copathType.addToFields(copathProcName)
				
				copathType.fields.each { f ->
					println "adding copath pref for ${f.fieldName}"
					def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:epicOntology, applicationDefault:true)
					if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
					if ( f.fieldName=="text") fp.aggregate=false
					if ( f.fieldName=="procedure_name" || f.fieldName=="procedure_code" || f.fieldName=="test_name_orig" || f.fieldName=="department_name") fp.numberOfFilterOptions=20
					if ( f.fieldName=="mrn" || f.fieldName=="patient_id" ) fp.computeDistinct = true
				
					f.addToPreferences(fp)
				}
				copathIdx.type = copathType
				nlp05.addToIndexes(copathIdx)
				
				println nlp05.toString()
				nlp05.save(failOnError:true, flush:true)
				
				copathCorpus.addToIndexes(copathIdx)
				copathCorpus.save(flush:true, failOnError:true)
				
				/*{
				 "text": "sometext",
				 "analyzed_text_length": 452,
				 "note_id": 1886132,
				 "patient_id": 14831,
				 "admission_id": 160882,
				 "analyzed_text_format": "FORMATTED PLAINTEXT",
				 "diagnosis": "NEWBORN",
				 "alive": "YES",
				 
				 "category": "Nursing/other",
				 "note_type": "Report",
				 "gender": "M",
				 "admission_type": "NEWBORN",
				 "insurance": "Private",
				 "religion": "CATHOLIC",
				 
				 "marital_status": "null",
				 "ethnicity": "WHITE",
				 "caregiver": "Read Only",
				 
				 "DOD": null,
				 "DOB": "2180-08-02",
				 "admit_date": "2180-08-02",
				 "discharge_date": "2180-10-28",
				 
				 "cui": [],
				 "acronym": []
			 	}*/
				
				def mimicCorpus = Corpus.findByName("MIMIC")?: new Corpus(name:"MIMIC", description:"MIMIC ICU notes", enabled:true, glyph:"fa-file-text-o", minimumRole:admin).save(flush:true, failOnError:true)
				def mimicIdx = Index.findByCommonName("MIMIC Notes")?:new Index(commonName:"MIMIC Notes", indexName:"mimic", status:"Searchable", description:"MIMIC ICU notesR", numberOfShards:6, numberOfReplicas:0,environment:Environment.current.name)
				def mimicType = Type.findByTypeNameAndIndex("note", null)?:new Type(typeName:"note", description:"NOTEEVENTS.TEXT field in the MIMIC distribution", environment:Environment.current.name)
				
				def mimicText = Field.findByFieldNameAndType("text", null)?:new Field(fieldName:"text",dataTypeName:"SNOWBALL_ANALYZED_STRING", description:"from NOTEEVENTS.TEXT in MIMIC data set", defaultSearchField:true, aggregatable:false)
				def mimicTextFmt = Field.findByFieldNameAndType("analyzed_text_format", null)?:new Field(fieldName:"analyzed_text_format", dataTypeName:"NOT_ANALYZED_STRING", description:"whether analyzed text contains new line formatting", aggregatable:false, exportable:false)
				def mimicNoteId = Field.findByFieldNameAndType("note_id",null)?:new Field(fieldName:"note_id",dataTypeName:"INTEGER", description:"NOTEEVENTS.ROW_ID in MIMIC data set", aggregatable:false)
				def mimicPatientId = Field.findByFieldNameAndType("patient_id",null)?:new Field(fieldName:"patient_id",dataTypeName:"INTEGER", description:"PATIENTS.ROW_ID in MIMIC data set", aggregatable:true)
				def mimicAdmissionId = Field.findByFieldNameAndType("admission_id",null)?:new Field(fieldName:"admission_id",dataTypeName:"INTEGER", description:"ADMISSIONS.ROW_ID in MIMIC data set", aggregatable:true)
				def mimicTextLength = Field.findByFieldNameAndType("analyzed_text_length",null)?:new Field(fieldName:"analyzed_text_length",dataTypeName:"INTEGER", description:"Length of text analyzed by BioMedICUS", aggregatable:false)
				def mimicDob = Field.findByFieldNameAndType("DOB", null)?:new Field(fieldName:"DOB", dataTypeName:"DATE", description:"", aggregatable:true, exportable:true)
				def mimicDod = Field.findByFieldNameAndType("DOD", null)?:new Field(fieldName:"DOD", dataTypeName:"DATE", description:"", aggregatable:true, exportable:true)
				def mimicAdmitDt = Field.findByFieldNameAndType("admit_date", null)?:new Field(fieldName:"admit_date", dataTypeName:"DATE", description:"", aggregatable:true, exportable:true)
				def mimicDischDt = Field.findByFieldNameAndType("discharge_date", null)?:new Field(fieldName:"discharge_date", dataTypeName:"DATE", description:"", aggregatable:true, exportable:true)
				
				def mimicDiagnosis = Field.findByFieldNameAndType("diagnosis", null)?:new Field(fieldName:"diagnosis", dataTypeName:"NOT_ANALYZED_STRING", description:"ADMISSIONS.DIAGNOSIS in MIMIC data set", aggregatable:true, exportable:true)
				def mimicAlive = Field.findByFieldNameAndType("alive", null)?:new Field(fieldName:"alive", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicCategory = Field.findByFieldNameAndType("category", null)?:new Field(fieldName:"category", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicNoteType = Field.findByFieldNameAndType("note_type", null)?:new Field(fieldName:"note_type", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicGender = Field.findByFieldNameAndType("gender", null)?:new Field(fieldName:"gender", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				
				def mimicAdmissionType = Field.findByFieldNameAndType("admission_type", null)?:new Field(fieldName:"admission_type", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicInsurance = Field.findByFieldNameAndType("insurance", null)?:new Field(fieldName:"insurance", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicReligion = Field.findByFieldNameAndType("religion", null)?:new Field(fieldName:"religion", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicMaritalStatus = Field.findByFieldNameAndType("marital_status", null)?:new Field(fieldName:"marital_status", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicEthnicity = Field.findByFieldNameAndType("ethnicity", null)?:new Field(fieldName:"ethnicity", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				def mimicCaregiver = Field.findByFieldNameAndType("caregiver", null)?:new Field(fieldName:"caregiver", dataTypeName:"NOT_ANALYZED_STRING", description:"PATIENTS.EXPIRE_FLAG in MIMIC data set, cast to YES/NO to reflect whether pt is alive", aggregatable:true, exportable:true)
				
				def mimicCui = Field.findByFieldNameAndType("cui", null)?:new Field(fieldName:"cui", dataTypeName:"NOT_ANALYZED_STRING", description:"UMLS CUIs identified by BioMedICUS NLP pipeline", aggregatable:true, exportable:true, significantTermsAggregatable:true)
				def mimicAcronym = Field.findByFieldNameAndType("acronym", null)?:new Field(fieldName:"acronym", dataTypeName:"NOT_ANALYZED_STRING", description:"Word sense disambiguated acronyms by BioMedICUS NLP pipeline", aggregatable:true, exportable:true, significantTermsAggregatable:true)
				
				
				mimicType.addToFields(mimicText)
				mimicType.addToFields(mimicTextFmt)
				mimicType.addToFields(mimicNoteId)
				mimicType.addToFields(mimicPatientId)
				mimicType.addToFields(mimicAdmissionId)
				mimicType.addToFields(mimicTextLength)
				mimicType.addToFields(mimicDob)
				mimicType.addToFields(mimicDod)
				mimicType.addToFields(mimicAdmitDt)
				mimicType.addToFields(mimicDischDt)
				mimicType.addToFields(mimicDiagnosis)
				mimicType.addToFields(mimicAlive)
				mimicType.addToFields(mimicCategory)
				mimicType.addToFields(mimicNoteType)
				mimicType.addToFields(mimicGender)
				
				mimicType.addToFields(mimicAdmissionType)
				mimicType.addToFields(mimicInsurance)
				mimicType.addToFields(mimicReligion)
				
				mimicType.addToFields(mimicMaritalStatus)
				mimicType.addToFields(mimicEthnicity)
				mimicType.addToFields(mimicCaregiver)
				mimicType.addToFields(mimicCui)
				mimicType.addToFields(mimicAcronym)
				
				
				mimicType.fields.each { f ->
					println "adding mimic prefs for ${f.fieldName}"
					def fp = new FieldPreference(user:app, label:PierUtils.labelFromUnderscore(f.fieldName), ontology:mimicOntology, applicationDefault:true)
					if ( f.contextFilterField || f.defaultSearchField ) fp.aggregate=false
					if ( f.fieldName=="text") fp.aggregate=false
					if ( f.fieldName=="patient_id" || f.fieldName=="admission_id" ) fp.computeDistinct = true
					if ( f.fieldName=="acronym" ) fp.ontology = biomedicus
					if ( f.fieldName=="cui" ) {
						fp.ontology=biomedicus
						fp.label = "Medical Concepts"
						fp.numberOfFilterOptions = 15
						fp.aggregate = true
					}
				
					f.addToPreferences(fp)
				}
				mimicIdx.type = mimicType
				nlp05.addToIndexes(mimicIdx)
				
				println nlp05.toString()
				nlp05.save(failOnError:true, flush:true)
				
				mimicCorpus.addToIndexes(mimicIdx)
				mimicCorpus.save(flush:true, failOnError:true)
				
				
				
			}
			
		}//end setup if TEST
		
		if  ( Environment.current != Environment.PRODUCTION ) {
			println "${Environment.current.name} UPDATING USERS AND PREFERENCES STARTING"
	
			configUser("rmcewan", [user,analyst,superadmin])
			configUser("rmcewan1", [user,analyst,superadmin])
			configUser("hultm041", [user,analyst,superadmin])
			configUser("ghultma1", [user,analyst,superadmin])
			configUser("alber475", [user])
			configUser("linde527", [user])
			configUser("yingzhu", [user,cancer,cardio])
			configUser("datar010", [user])
			configUser("gms", [user])
			
			configUser("gmelton",[user,analyst])
			configUser("pakh0002",[user,analyst])
			
			//BPIC
			configUser("tholk009",[user,analyst])
			configUser("akke0014",[user,analyst])
			configUser("andre725",[user,analyst])
			configUser("baker439",[user,analyst])
			configUser("rames007",[user,analyst])
			configUser("siege022",[user,analyst])
			
			if  ( Environment.current.name=="fvdev" || Environment.current.name=="fvtest" ) {
				//Init FV users
				configUser("jessler1",[user,cancer])
				configUser("mleonar1",[user,cancer,cardio])
				configUser("ehoule1", [user,cancer,cardio])
				configUser("jmarkow1",[user,cardio])
				configUser("jlibor1", [user,cardio])
				configUser("pvonide1",[user,cardio])
				configUser("klondgr1",[user,cardio])
				configUser("esamuel1",[user,cardio])
				configUser("cweber5",[user,analyst])
				configUser("sweldon2",[user,analyst])
			}
			
			//PREFS SANITY CHECK
			def users = User.list()
			users.each { u ->
				if ( u.username!='nlppier' ) {
					configService.initalizeUserPreferences( u )
				}
			}
			println "${Environment.current.name} UPDATING USERS AND PREFERENCES COMPLETE"
		}
		println "--END BOOTSTRAP--"
		
    }
	
	User configUser( username, roles ) {
		def pwd = RandomStringUtils.randomAlphanumeric(20)
		def user = User.findByUsername(username)?:new User(username:username,password:pwd,enabled:true).save(failOnError:true)
		roles.each { role ->
			if ( !UserRole.exists(user.id, role.id) ) {
				UserRole.create(user, role)
				println "USER CONFIGURED ${username}:${pwd}"
			}
		}
		user
	}
	
	
    def destroy = {
    }
	
}
	