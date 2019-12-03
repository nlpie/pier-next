
package edu.umn.nlpie.pier.indexing

import java.util.concurrent.atomic.AtomicInteger

class Metrics {
	
	AtomicInteger processed = new AtomicInteger()
	AtomicInteger exists = new AtomicInteger()
	AtomicInteger notExists = new AtomicInteger()
	AtomicInteger updated = new AtomicInteger()
	AtomicInteger errored = new AtomicInteger()
	def unit = "unspecified"
	def start = new Date().time
	def batch = -1
	
	def incrementProcessed() {
		processed.addAndGet(1)
	}
	
	def incrementExists() {
		exists.addAndGet(1)
	}
	
	def incrementNotExists() {
		notExists.addAndGet(1)
	}
	
	def incrementUpdated( int n ) {
		updated.addAndGet( n )
	}
	
	def incrementErrored() {
		errored.addAndGet(1)
	}
	
	def elapsedTimeInMillis() {
		def now = new Date().time
		now-start
	}
	
	def rate() {
		def rate = (processed.get()/elapsedTimeInMillis())*1000
		def formattedRate = String.format("%.1f", rate)
		"${formattedRate} ${unit}/s"
	}
	
	def stats() {
		"BATCH ${batch} -- processed: ${processed.get()}, exists: ${exists.get()}, updated: ${updated.get()}, notExists: ${notExists.get()}, errored: ${errored.get()};  ${rate()};  elapsedTime: ${elapsedTimeInMillis()/1000} s"
	}
	
	def log() {
		def stamp = new Date()
		def log = new File( "../logs/Batch_${batch}_${stamp}.log" )
		log << stats()
		//WARN if updated!=updated
	}
}
