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

    def init = { servletContext ->
		
		if (Environment.current != Environment.PRODUCTION) {
			println "Bootstrap: ${Environment.current.name}"
		}
		
		//make sure nlppier user exists
		User.findByUsername("nlppier")?:new User(username:"nlppier",password:"${configService.generatePassword()}",enabled:false).save(failOnError:true)
		def superadmin = Role.findByAuthority("ROLE_SUPERADMIN")?:new Role(authority:"ROLE_SUPERADMIN").save(flush:true)
		def admin = Role.findByAuthority("ROLE_ADMIN")?:new Role(authority:"ROLE_ADMIN").save(flush:true)
		def user = Role.findByAuthority("ROLE_USER")?:new Role(authority:"ROLE_USER").save(flush:true)
		def analyst = Role.findByAuthority("ROLE_ANALYST")?:new Role(authority:"ROLE_ANALYST").save(flush:true)
		def cardio = Role.findByAuthority("ROLE_CARDIOLOGY")?:new Role(authority:"ROLE_CARDIOLOGY").save(flush:true)
		def cancer = Role.findByAuthority("ROLE_CANCER")?:new Role(authority:"ROLE_CANCER").save(flush:true)

		configUser("rmcewan", [user,analyst,superadmin])
		//configUser("rmcewan",  [user,cardio])
		configUser("rmcewan1", [user,analyst,superadmin])
		configUser("hultm041", [user,analyst,superadmin])
		configUser("ghultma1", [user,analyst,superadmin])
		configUser("alber475", [user,analyst])
		configUser("linde527", [user,analyst])
		
		configUser("gmelton",[user,analyst])
		configUser("pakh0002",[user,analyst])
		
		//Init FV users
		configUser("jessler1",[user,cancer])
		configUser("mleonar1",[user,cancer,cardio])
		configUser("ehoule1", [user,cancer,cardio])
		configUser("jmarkow1",[user,cardio])
		configUser("jlibor1", [user,cardio])
		configUser("pvonide1",[user,cardio])
		configUser("klondgr1",[user,cardio])
		configUser("esamuel1",[user,cardio])
		
		
		
		
		//populate elastic data
		Cluster.withSession { session ->
			def app = configUser("nlppier", [])
			
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
			def patientId = Field.findByFieldNameAndType("patient_id", null)?:new Field(fieldName:"patient_id", dataTypeName:"LONG", description:"CDR Patient ID", aggregatable:true, exportable:true)
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

			def clinicalCorpus = Corpus.findByName("Clinical Notes")?: new Corpus(name:"Clinical Notes", description:"notes from Epic", enabled:true, glyph:"fa-file-text-o", minimumRole:analyst).save(flush:true, failOnError:true)
			
			def noteType = Type.findByTypeName("note")?:new Type(typeName:"note", description:"CDR note", environment:Environment.current.name)
			noteType.addToFields(noteId)
			noteType.addToFields(mrn)
			noteType.addToFields(patientId)
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
					fp.aggregate = true
				}
				if ( f.fieldName=="mrn" || f.fieldName=="patient_id") {
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

			
			//PREFS SANITY CHECK
			def users = User.list()
			users.each { u ->
				if ( u.username!='nlppier' ) {
					configService.initalizeUserPreferences( u )
					println "preferences set for ${u.username}"
				}
			}
			
		}
		
    }
	
	User configUser( username, roles ) {
		def pwd = RandomStringUtils.randomAlphanumeric(20)
		def user = User.findByUsername(username)?:new User(username:username,password:pwd,enabled:true).save(failOnError:true)
		roles.each { role ->
			UserRole.create(user, role)
		}
		println "USER CONFIGURED ${username}:${pwd}"
		user
	}
	
	
    def destroy = {
    }
	
}
	