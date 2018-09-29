	<script type="text/ng-template" id="expansionSelection.template">
        	<div class="modal-header">
            	<h3 class="modal-title" id="modal-title">Word vector-based suggestions</h3>
				<small> selected suggestions are added to the current search </small>
        	</div>
        	<div class="modal-body" id="modal-body">
            	
				
				<uib-tabset active="active">
    				<uib-tab ng-repeat="embedding in ctrl.embeddings track by $index" index="$index" heading="{{ctrl.currentSearch.inputExpansion.targetLabel(embedding.word)}}">

						<div class="row">
							<div class="col-md-12" ng-if="ctrl.currentSearch.inputExpansion.targetHasExpansions(embedding.word)">
								<br/>&nbsp;
								<b>Selected expansion terms:</b> {{ ctrl.currentSearch.inputExpansion.flatten(embedding.word) }}
								<br/>&nbsp;
							</div>
							<div class="col-md-12" ng-if="!ctrl.currentSearch.inputExpansion.targetHasExpansions(embedding.word)">
								<br/>&nbsp;
								No expansions selected
								<br>&nbsp;
							</div>
						</div>

						<div class="row">
							<div class="col-md-12">
								<b>Related misspellings</b>
								<small style="color:gray"><i>frequency | spellgen score | edit distance</i></small>
							</div>
            			</div>
						<div class="row">
							<div class="col-md-12">
								<span ng-click="ctrl.toggleAllRelatedMisspellings(embedding.word, ctrl.embeddings[$index].relatedMisspellings )">Select All <input type="checkbox" title="select/de-select all" ng-model="ctrl.allRelatedMisspellings"></input></span>
							</div>
						</div>
						<div class="row">
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:0" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }} 
										</input>
										<small style="color:gray"><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:8" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }} 
										</input>
										<small style="color:gray"><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
                					</li>
            					</ul>
							</div>

							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:16" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }} 
										</input>
										<small style="color:gray"><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:24" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }} 
										</input>
										<small style="color:gray"><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
                					</li>
            					</ul>
							</div>
						</div>

						<div class="row">
							<div class="col-md-12">
								<b>Semantically related terms</b>
								<small style="color:gray"><i>frequency | cosine similarity</i></small>
							</div>
            			</div>
						<div class="row">
							<div class="col-md-12">
								<span ng-click="ctrl.toggleAllSemanticallyRelatedTerms(embedding.word, ctrl.embeddings[$index].semanticallyRelatedTerms )">Select All <input type="checkbox" title="select/de-select all" ng-model="ctrl.allSemanticallyRelatedTerms"></input></span>
							</div>
						</div>
						<div class="row">
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:15:0" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }}
										</input>
										<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:15:15" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }}
										</input>
										<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:15:30" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }}
										</input>
										<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:15:45" 
										style="list-style-type:none" ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
                    					<input type="checkbox" ng-model="suggestion.on">
										{{ suggestion.term }}
										</input>
										<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
                					</li>
            					</ul>
							</div>
						</div>

					</uib-tab>
				</uib-tabset>
        	</div>
        	<div class="modal-footer">
            	<button class="btn btn-primary" type="button" ng-click="ctrl.modalService.expOk()">OK</button>
            	<button class="btn btn-warning" type="button" ng-click="ctrl.clear();ctrl.modalService.expClear()">Clear All</button>
        	</div>
    </script>