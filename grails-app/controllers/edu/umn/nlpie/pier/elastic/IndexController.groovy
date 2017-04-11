package edu.umn.nlpie.pier.elastic

//import org.elasticsearch.client.Client

//import org.elasticsearch.groovy.client.GClient
//import org.elasticsearch.groovy.node.GNode
//import static org.elasticsearch.groovy.node.nodeBuilder

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress

import edu.umn.nlpie.pier.PierUtils
import edu.umn.nlpie.pier.springsecurity.User
import edu.umn.nlpie.pier.ui.FieldPreference
import edu.umn.nlpie.pier.ui.Ontology


class IndexController {

	def indexService 
	
	def p() {
		indexService.createAdminConfigurationFromIndexMapping("nlp05.ahc.umn.edu","notes_v2")
	}
	
    static scaffold = true
	//def index() { }
	
	def check() {
		println Field.findAllByTypeInList(Index.types).collect{it.id}
	}
	
	def e() {
		Settings settings = Settings.settingsBuilder().put("client.transport.sniff", true).build()
		Client client = nodeBuilder().client(true).settings {
			cluster {
			  name = "green"
			}
			arbitrary {
			  setting = "arbitraryValue"
			}
		  }.node().client
	}
	
	def transport() {
		Settings settings = Settings.settingsBuilder()
			.put("client.transport.sniff", true)
			.put("cluster.name", "green").build()
			
		Client client = TransportClient.builder().settings(settings).build() 	//https://www.elastic.co/guide/en/elasticsearch/reference/2.0/breaking_20_java_api_changes.html
		
		/*TransportClient client = new TransportClient(ImmutableSettings.settingsBuilder {
			cluster {
				name = "green"
			}
			client {
				transport {
					sniff = true
				}
			}
		})*/

		// add transport addresses
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("nlp05.ahc.umn.edu"), 9300))
		
		SearchResponse response = client.search {
		  indices "pier.next.gen"
		  types "sample"
		  source {
			query {
			  match {
				name = 'drill'
			  }
			}
		  }
		}.actionGet()
		
		println "found ${response.hits.totalHits()}, ${response.tookInMillis}"
		
		client.close()
	}
	
}
