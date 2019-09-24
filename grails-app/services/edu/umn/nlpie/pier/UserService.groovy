package edu.umn.nlpie.pier

import grails.transaction.Transactional
//import grails.plugin.springsecurity.SpringSecurityService

@Transactional
class UserService {
	
	
	
	def springSecurityService

    def getCurrentUser() {
		springSecurityService.currentUser
    }
	
	def getCurrentUserId() {
		springSecurityService.currentUser.id
	}
	
	def getCurrentUserUsername() {
		springSecurityService.currentUser?.username
	}
	
	def loggedIn() {
		springSecurityService.isLoggedIn()
	}
	
	/*def getCurrentUserId() {
		//requires Message: Object of class [org.springframework.security.ldap.userdetails.LdapUserDetailsImpl] must be an instance of class grails.plugin.springsecurity.userdetails.GrailsUser
		println springSecurityService.loadCurrentUser().id
	}*/
}
