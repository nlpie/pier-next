package edu.umn.nlpie.pier.elastic.search

import edu.umn.nlpie.pier.api.HistorySummaryDTO
import edu.umn.nlpie.pier.audit.Query
import edu.umn.nlpie.pier.audit.SearchRegistration


//@Transactional
class SearchService {

    def saveSearch( ) {
		//get user from spring security
    }
	
	def searchHistory( excludeMostRecent ) {
		def sql = new StringBuffer()
		sql << "select distinct max(q.registration.id), q.label, q.registration.authorizedContext from Query q where q.registration.username=? and q.httpStatus=? and q.type=? and q.registration.id not in ( ? ) group by q.label, q.registration.authorizedContext, q.query order by max(q.registration.id) desc "

		def registrationId = 0.toLong()
		
		if ( excludeMostRecent ) {
			registrationId = SearchRegistration.createCriteria().get {
				eq ("username", "nouserservice.user")	//TODO change to authenticated user from userService
				projections {
					max "id"
				}
			} as Long
		}
		
		def results = Query.executeQuery(sql.toString(), ["nouserservice.user", 200, "document", registrationId] )
		def summaries = []
		results.each {
			summaries << new HistorySummaryDTO(it)
		}
		summaries
		//lookup user from userService
	}
	
	def registeredSearch(id) {
		SearchRegistration.get(id.toLong())
	}
	
}