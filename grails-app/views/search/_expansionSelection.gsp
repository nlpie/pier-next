	<script type="text/ng-template" id="expansionSelection.template">
        	<div class="modal-header">
            	<h3 class="modal-title" id="modal-title">Word vector-based suggestions</h3>
				<small> selected suggestions are added to the current search </small>
        	</div>
        	<div class="modal-body" id="modal-body">

				<uib-tabset active="active">
    				<uib-tab ng-repeat="embedding in ctrl.embeddings track by $index" heading="{{ctrl.currentSearch.inputExpansion.targetLabel(embedding.word)}}">

						<div class="row">
							<div class="col-md-12" style="padding:20px" ng-if="ctrl.currentSearch.inputExpansion.targetHasExpansions(embedding.word)">
								<b>Selected expansion terms:</b> {{ ctrl.currentSearch.inputExpansion.flatten(embedding.word) }}
							</div>
							<div class="col-md-12" ng-if="!ctrl.currentSearch.inputExpansion.targetHasExpansions(embedding.word)">
								<br/>&nbsp;
								No expansions selected
								<br>&nbsp;
							</div>
						</div>

						<div class="row">
							<div class="col-md-12">
								<b style="font-size:1.3em">Related misspellings</b>
								<span style="padding-left:10px" ng-click="ctrl.toggleAllRelatedMisspellings(embedding.word, ctrl.embeddings[$index].relatedMisspellings)">
									Select All 
									<i class="fa fa-check-square-o" ng-if="ctrl.allRelatedMisspellings==true"></i>
									<i class="fa fa-square-o" ng-if="ctrl.allRelatedMisspellings==false"></i>
								</span>
								<br>
								<span style="padding-left:13px;color:gray">
									<small><i>frequency | spellgen score | edit distance</i></small>
								</span>
							</div>
            			</div>
						<div class="row">
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in embedding.relatedMisspellings | limitTo:8:0" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
										</span>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in embedding.relatedMisspellings | limitTo:8:8" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
										</span>
									</li>
            					</ul>
							</div>

							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in embedding.relatedMisspellings | limitTo:8:16" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
										</span>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in embedding.relatedMisspellings | limitTo:8:24" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small><i>{{suggestion.frequency | number }} | {{suggestion.score | number:2 }} | {{suggestion.edit_distance}}</i></small>
										</span>
                					</li>
            					</ul>
							</div>
						</div>


						<div class="row">
							<div class="col-md-12">
								<b style="font-size:1.3em">Semantically related terms</b>
								<span style="padding-left:10px" ng-click="ctrl.toggleAllSemanticallyRelatedTerms(embedding.word, ctrl.embeddings[$index].semanticallyRelatedTerms)">
									Select All 
									<i class="fa fa-check-square-o" ng-if="ctrl.allSemanticallyRelatedTerms==true"></i>
									<i class="fa fa-square-o" ng-if="ctrl.allSemanticallyRelatedTerms==false"></i>
								</span>
								<br>
								<span style="padding-left:13px;color:gray">
									<small><i>frequency | cosine similarity</i></small>
								</span>
							</div>
            			</div>
						<div class="row">
							<div class="col-md-3">
								<ul>
									<li ng-repeat="suggestion in embedding.semanticallyRelatedTerms | limitTo:15:0" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
										</span>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
									<li ng-repeat="suggestion in embedding.semanticallyRelatedTerms | limitTo:15:15" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
										</span>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
									<li ng-repeat="suggestion in embedding.semanticallyRelatedTerms | limitTo:15:30" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
										</span>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
									<li ng-repeat="suggestion in embedding.semanticallyRelatedTerms | limitTo:15:45" style="list-style-type:none">
										<span ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion)">
											<i class="fa fa-check-square-o" ng-if="suggestion.on==true"></i>
											<i class="fa fa-square-o" ng-if="suggestion.on==false"></i>
											{{ suggestion.term }}
										</span>
										<br>
										<span style="color:gray;padding-left:13px">
											<small style="color:gray"><i>{{ suggestion.frequency | number }} | {{ suggestion.cosine_distance | number:2 }}</i></small>
										</span>
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