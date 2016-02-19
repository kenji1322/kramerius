/*
 * Copyright (C) 2013-2016 Alberto Hernandez
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/* global K5 */

K5.eventsHandler.addHandler(function(type, configuration) {
    if (type === "i18n/dictionary") {
        $(document).prop('title', K5.i18n.ctx.dictionary['application.title'] +
                ". " + K5.i18n.ctx.dictionary['common.results.for'] +
                " \"" + $("#q").val() + "\"");
        if (!K5.gui["results"])
            K5.gui["results"] = new Results();
    }
});

var Results = function(elem) {
    
    this.container = $("#app-results-container");
    this.displayStyle = "display";
    this._init();
};

Results.prototype = {
    _init: function() {
        if (this.resultsLoaded)
            return;
        
        K5.eventsHandler.addHandler(_.bind(function(type, data) {
            if (type === "window/resized") {
                
            }
        }, this));
        
        this.itemTemplate = $(".app-result-item").clone();
        this.numFoundLabelContainer = $("#numFoundLabel");
        
        this.paginationContainer = $("#pagination");
        this.pageTemplate = this.paginationContainer.find(".page").clone();
        this.pagePrev = this.paginationContainer.find(".prev").clone();
        this.pageNext = this.paginationContainer.find(".next").clone();
        this.paginationContainer.empty();
        
        this.getDocs();
        
        

        window.onpopstate = _.bind(function(e) {
            //console.log(e.state);
            var s = (e.state ? e.state.start : 0);
            $("#start").val(s);
            this.getDocs();
        }, this);


    },
    getDocs: function() {
        $('.opacityloading').show();
        this.container.empty();
        $.get("raw_results.vm?" + $("#search_form").serialize(), _.bind(function(data) {
            //console.log(data);
            $('#search_results_docs .more_docs').remove();
            var json = jQuery.parseJSON(data);
            this.loadDocs(json);
            K5.eventsHandler.trigger("results/loaded", json);
            $('.opacityloading').hide();
            this.resultsLoaded = true;
        }, this));
    },
    loadDocs: function(res) {
        var numFound = 0;
        var addMore = false;
        var docs;
        if (res.grouped) {
            numFound = res.grouped.root_pid.ngroups;
            var groups = res.grouped.root_pid.groups;

            for (var i = 0; i < groups.length; i++) {
                var doc = groups[i].doclist.docs[0];
                var pid = doc.PID;
                
                var elem = this.itemTemplate.clone();
                var r = new Result(elem,
                        {"json": doc,
                            "collapsed": groups[i].doclist.numFound,
                            "hl": res.highlighting[pid]});
                this.container.append(elem);
            }
            if (numFound > parseInt(res.responseHeader.params.rows) + parseInt(res.responseHeader.params.start)) {
                addMore = true;
            }
        } else {
            numFound = res.response.numFound;
            docs = res.response.docs;
            for (var i = 0; i < docs.length; i++) {
                var elem = this.itemTemplate.clone();
                var pid = docs[i].PID;
                var r = new Result(elem, {
                    "json": docs[i],
                    "hl": res.highlighting[pid]
                });
                this.container.append(elem);
            }
        }
            
        $("div.collections").mouseenter(function(){
            $(this).children("div.cols").show();
        });
        $("div.collections").mouseleave(function(){
            $(this).children("div.cols").hide();
        });
        
        var start = 0;
        if(res.responseHeader.params.start){
            start = parseInt(res.responseHeader.params.start);
        }
        
        var rows = parseInt($("#rows").val());
        if(res.responseHeader.params.rows){
            rows = parseInt(res.responseHeader.params.rows);
        }

        this.setHeader(numFound, start, rows);
    },
    setHeader: function(numFound, start, rows) {
        var key = 'common.title.plural_2';
        if (numFound > 4) {
            key = 'common.title.plural_2';
        } else if (numFound > 1) {
            key = 'common.title.plural_1';
        } else {
            key = 'common.title.singural';
        }
        
        this.numFoundLabelContainer.find(".number").text(numFound);
            
        this.numFoundLabelContainer.find(".text").attr('data-key', key);
        this.numFoundLabelContainer.find(".text").data('key', key);
        this.numFoundLabelContainer.find(".text").text(K5.i18n.ctx.dictionary[key]);
        
        //Pagination
        this.paginationContainer.empty();
        
        if(numFound > rows){
            var pages = Math.min(Math.floor(numFound / rows) + 1, 5);
            if(start > 0){
                this.paginationContainer.append(this.pagePrev);
            }
            
            
            var curr = Math.floor(start/rows) + 1;
            var s = Math.max(curr - 2, 1);
            if(pages < 5){
                s = 1;
            }
            
            for(var i = 0; i<pages; i++ ){
                var p = this.pageTemplate.clone();
                if(curr === i+s){
                    p.addClass("active");
                }
                p.find(".page-label").text(i+s);
                p.data("start", i*rows);
                p.click(_.partial(function(res){
                    window.event.preventDefault();
                    var p = $(this).data("start");
                    $("#start").val(p);
                    
                    var j = searchToJson();
                    j.start = p;
                    var searchStr = location.origin + location.pathname + "?" + jsonToSearch(j);
                    window.history.pushState({start: p}, null, searchStr);
                    res.getDocs();
                }, this));
                this.paginationContainer.append(p);
            }
            if(start < numFound - rows){
                this.paginationContainer.append(this.pageNext);
            }
            this.paginationContainer.show();
        }else{
            this.paginationContainer.hide();
        }
        
    }
};



var Result = function(elem, options) {
    
    this.json = options.json;
    this.elem = elem;
    
    this.hl = options.hl;
    this.collapsed = options.collapsed;
    this.init();

    return this.elem;
};

Result.prototype = {
    background: "silver",
    thumbHeight: 128,
    maxInfoLength: 50,
    init: function() {

        this.render();

    },
    setSizes: function() {
        this.panelHeight = this.$elem.height();
        this.panelWidth = this.$elem.width();
    },
    
    render: function() {
        
        var doc = this.json;
        var pid = doc.PID;
        var imgsrc = "api/item/" + pid + "/thumb";
        
        var fedora_model = doc[fieldMappings.fedora_model];
        var typtitulu = doc["model_path"][0].split("/")[0];

        var title = doc[fieldMappings.title];
        
        if (title.length > this.maxInfoLength) {
            title = title.substring(0, this.maxInfoLength) + "...";
        }
            
        var rootTitle = doc["root_title"];
        
        
        var info = {short: "", full: ""};
        info.full = '<div class="title">' + rootTitle + '</div>';
        info.short = "";


        if (rootTitle.length > this.maxInfoLength) {
            rootTitle = rootTitle.substring(0, this.maxInfoLength) + '...';
        }
        this.getDetails(info);
        
        
        this.elem.find(".app-result-item-title").html(rootTitle);
        this.elem.find("img").attr("src", imgsrc);
        this.elem.find(".app-result-item-title").data("pid", pid);
        this.elem.find(".app-result-item-title").click(function(){
            K5.api.gotoDisplayingItemPage($(this).data('pid'));
        });
        
        if (doc[fieldMappings.autor]) {
            var cre = doc[fieldMappings.autor].toString();
        
            info.full += '<div class="author">' + cre + '</div>';
            if (cre.length > 40) {
                cre = cre.substring(0, 40) + "...";
            }
            
            this.elem.find(".app-result-item-author").html(cre);
        }

        if (doc["datum_str"]) {
            info.full += '<div class="datum">' + doc["datum_str"] + '</div>';
            this.elem.find(".app-result-item-rok").html(doc["datum_str"]);
        }
        
        if (doc['dostupnost']) {
            this.elem.find(".policy").addClass(doc['dostupnost']);
        }
        
        
        if (this.hl && this.hl["text_ocr"]) {
            var tx = "";
            for (var j = 0; j < this.hl.text_ocr.length; j++) {
                tx += '<div class="hl">' + this.hl.text_ocr[j] + '</div>';
            }
            //this.elem.find(".app-result-item-teaser").html(tx).show();
            info.full += tx;
        }
        
        var linkpid;
        if ((this.collapsed && this.collapsed > 1)) {
            linkpid = doc['root_pid'];
            var key = 'common.hits.plural_1';
            if (this.collapsed > 4) {
                key = 'common.hits.plural_2';
            }
            var tx = K5.i18n.translatable(key);
            this.elem.find(".app-result-item-found").html(this.collapsed + ' ' + tx + ' ' + K5.i18n.translatable('model.locativ.' + typtitulu));
        } else if (fedora_model === typtitulu) {
            this.elem.find(".app-result-item-found").html(K5.i18n.translatable('fedora.model.' + typtitulu));
        } else {
            this.elem.find(".app-result-item-found").html(K5.i18n.translatable('fedora.model.' + fedora_model) + ' ' +
                    K5.i18n.translatable('model.locativ.' + typtitulu));
        }
        
        
        if(doc.hasOwnProperty("collection") && doc.collection.length>0){
            info.full += "<div>" + K5.i18n.translatable('harvested.from') + ":</div>";
            for(var i=0; i< doc.collection.length; i++){
                info.full += '<div class="collection">' + K5.i18n.translatable(doc.collection[i]) + '</div>';
            }
        }
        

        this.elem.find(".app-result-item-info").attr("content", info.full)
                .popover({html:true, 
                    trigger: 'focus',
                    placement: "auto top",
                    content: info.full
                })
                .click(function(e){
                    e.preventDefault();
                    e.stopPropagation(); 
                });
        
    },
    getDetails: function(info) {
        //var title = this.json["dc.title"];
        var model = this.json["fedora.model"];
        var details = this.json["details"];
        var root_title = this.json["root_title"];
        var detFull = "";
        var detShort = "";
        if (details && details.length > 0) {
            var dArr = details[0].split("##");
            if (model === "periodicalvolume") {
                detShort = "<div>" + root_title.substring(0, this.maxInfoLength) + "</div>" +
                        K5.i18n.translatable('field.datum') + ": " + dArr[0] + " " +
                        K5.i18n.translatable('mods.periodicalvolumenumber') + " " + dArr[1];
                detFull = "<div>" + root_title + "</div>" +
                        K5.i18n.translatable('field.datum') + ": " + dArr[0] + " " +
                        K5.i18n.translatable('mods.periodicalvolumenumber') + " " + dArr[1];
            } else if (model === "internalpart") {
                detFull = dArr[0] + " " + dArr[1] + " " + dArr[2] + " " + dArr[3];
                detShort = dArr[0] + " " + dArr[1] + " " + dArr[2] + " " + dArr[3];
            } else if (model === "periodicalitem") {
                if (dArr[0] !== root_title) {
                    detFull = dArr[0] + " " + dArr[1] + " " + dArr[2];
                    detShort = dArr[0] + " " + dArr[1] + " " + dArr[2];
                } else {
                    detFull = dArr[1] + " " + dArr[2];
                    detShort = dArr[1] + " " + dArr[2];
                }
            } else if (model === "monographunit") {
                detFull = dArr[0] + " " + dArr[1];
                detShort = dArr[0] + " " + dArr[1];
            } else if (model === "page") {
                detFull = dArr[0] + " " + K5.i18n.translatable('mods.page.partType.' + dArr[1]);
                detShort = dArr[0] + " " + K5.i18n.translatable('mods.page.partType.' + dArr[1]);

            } else {
                detFull = details;
                detShort = details;
            }
        } else {
            return "";
        }

        info.short += '<div class="details">' + detShort + '</div>';
        info.full += '<div class="details">' + detFull + '</div>';

    }
};
