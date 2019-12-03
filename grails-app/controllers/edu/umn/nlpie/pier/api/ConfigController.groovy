package edu.umn.nlpie.pier.api

import org.apache.commons.lang.RandomStringUtils

import edu.umn.nlpie.pier.ConfigService
import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.api.exception.*
import edu.umn.nlpie.pier.context.AuthorizedUser
import edu.umn.nlpie.pier.elastic.Index
import edu.umn.nlpie.pier.springsecurity.Role
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.springsecurity.UserRole
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured


class ConfigController {
    
	ConfigService configService
	def apiService
	def dataSource_notes
	
	static responseFormats = ['json']
	//static allowedMethods = [get: "GET", "find": "POST"]
		
	//keep
	def authorizedContexts() {
		def contexts = configService.authorizedContexts
		JSON.use ('authorized.context') {
			respond contexts
		}
	}
	
	def authorizedContextByLabel() {
		JSON.use ('authorized.context') {
			//println request.JSON
			respond configService.authorizedContextByLabel(request.JSON.label)
		}
	}
	
	//deprecated
	def authorizedContextByQueryId() {
		JSON.use ('authorized.context') {
			//println request.JSON
			respond configService.authorizedContextByQueryId(request.JSON.queryId)
		}
	}
	
	//TODO refactor to superclass
	private renderException(Exception e) {
		render(status: e.status, text: '{"message":"'+ e.message +'"}', contentType: "application/json") as JSON
	}

	def mapping() {
		JSON.use ('index.mapping') {
			def json = Index.get(1) as JSON
			json.prettyPrint = true
			json.render response
		}
	}
	
	@Secured(["ROLE_SUPERADMIN"])
	def reveng() {
		def index = Index.findByIndexName('notes_v2')
		println index.cluster
		configService.reverseEngineerDomainInstancesFromIndexMapping(index)
	}
	
	def under() {
		PierUtils.toSnakeCase("CamelCaseToUnderscore")
		PierUtils.underscoreToCamelCase("underscore_to_camel_case")
		PierUtils.labelFromUnderscore("label_from_underscore")
	}
	
	@Secured(["ROLE_SUPERADMIN"])
	def users() {
		def role = Role.findByAuthority("ROLE_USER")
		AuthorizedUser.list().each {  u->
			def user = User.findByUsername( u.username )
			if ( !user ) {
				user = new User( username:u.username, enabled:true, password:configService.generatePassword() ).save( failOnError:true,flush:true )
				println "CREATED USER [${u.username}] with ID:[${user.id}]"
				configService.initalizeUserPreferences( user )
			}
			if ( !UserRole.exists(user.id,role.id) ) {
				UserRole.create( user,role ).save(flush:true)
				println "CREATED ROLE_USER [${user.username}]"
			} else {
				//nothing to do
			}
		}
	}
	
	@Secured(["ROLE_SUPERADMIN"])
	def preferences() {
		/*
		SELECT * FROM field_preference where user_id=1; #nlppier
		SELECT * FROM field_preference where user_id=2; #rmcewan
		#delete from field_preference where user_id=3
		SELECT * FROM field_preference where user_id=3; #rmcewan1
		
		#delete from field_preference where user_id not in (1)
		
		select * from field where field_name='service_date'
		
		
		#reset application defaults
		update field_preference fp
		inner join field f on f.id=fp.field_id and fp.user_id=1
		inner join `type` t on t.id=f.type_id
		inner join `index` i on i.id=t.index_id and i.id=2
		set fp.aggregate=0, fp.export=0;
		
		#update default fields to export
		update field_preference fp
		inner join field f on f.id=fp.field_id and fp.user_id=1
		inner join `type` t on t.id=f.type_id
		inner join `index` i on i.id=t.index_id and i.id=2
		set fp.export=1
		where f.field_name in ( 'service_id', 'patient_id' );
		
		#update default field to aggregate
		update field_preference fp
		inner join field f on f.id=fp.field_id and fp.user_id=1
		inner join `type` t on t.id=f.type_id
		inner join `index` i on i.id=t.index_id and i.id=2
		set fp.aggregate=1
		where f.field_name in ( 'service_id', 'patient_id', 'kod', 'smd', 'tos', 'role', 'setting', 'prov_type', 'service_date', 'encounter_center', 'encounter_department');
		
		
		#check results
		select * from field_preference fp
		inner join field f on f.id=fp.field_id
		inner join `type` t on t.id=f.type_id
		inner join `index` i on i.id=t.index_id
		where i.id=2
		and user_id=1 order by fp.ontology_id, fp.label
		*/
		
		def users = User.findAllByEnabled( true )
		users.each { u ->
			configService.initalizeUserPreferences( u )			
		}
	}
	
}