import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration

import edu.umn.nlpie.pier.elastic.*
import edu.umn.nlpie.pier.springsecurity.User
import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
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
		
		
    }
    def destroy = {
    }
	
}
	