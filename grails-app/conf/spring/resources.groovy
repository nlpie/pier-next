import edu.umn.nlpie.pier.SettingsService
import edu.umn.nlpie.pier.elastic.search.ElasticService
import edu.umn.nlpie.pier.marshaller.JsonMarshallerRegistrar
import grails.util.Environment

// Place your Spring DSL code here
beans = {
	jsonMarshallerRegistrar(JsonMarshallerRegistrar)
	
	elasticService(ElasticService) {
		switch(Environment.current) {
	
			case Environment.current.name.startsWith("fv"):
				umlsIndexSearchUrl = "http://nlp02.fairview.org:9200/umls_2013/entry/_search"				
				break
			case Environment.DEVELOPMENT:
				umlsIndexSearchUrl = "http://nlp05.ahc.umn.edu:9200/umls_2013/entry/_search"
				break
			default:
				umlsIndexSearchUrl = "http://nlp05.ahc.umn.edu:9200/umls_2013/entry/_search"
				break
		}
	}
}