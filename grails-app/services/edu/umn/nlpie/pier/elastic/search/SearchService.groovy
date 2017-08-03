package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration


//@Transactional
class SearchService {

    def saveSearch( ) {
		//get user from spring security
    }
	
	def searchHistory( excludeMostRecent ) {
		def sql = new StringBuffer()
		def registrationId = 0.toLong()
		sql << "from Query q where q.registration.username=? and q.httpStatus=? and q.type=? and q.registration.id not in ( ? ) order by q.id desc "
		if ( excludeMostRecent ) {
			registrationId = SearchRegistration.createCriteria().get {
				eq ("username", "nouserservice.user")	//TODO change to authenticated user from userService
				projections {
					max "id"
				}
			} as Long
		}
		Query.findAll(sql.toString(), ["nouserservice.user", 200, "document", registrationId] )
		//lookup user from userService
	}
	
}