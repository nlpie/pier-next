/**
 * 
 */
package edu.umn.nlpie.pier.api

/**
 * @author ${name:git_config(user.name)} and ${email:git_config(user.email)} (rmcewan) 
 *
 */
class ScrollPayload {
	String scroll_id
	String scroll	//time expression, see https://www.elastic.co/guide/en/elasticsearch/reference/2.3/common-options.html#time-units for valid values
}