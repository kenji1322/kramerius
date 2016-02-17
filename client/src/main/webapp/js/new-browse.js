/* global _, K5 */

K5.eventsHandler.addHandler(function(type, data) {
    if (type === "i18n/dictionary") {
        if (!K5.gui.browse) {
            K5.gui.browse = new Browse(K5, $("#browse"));
        }
    }
    if (type === "widow/url/hash") {
        K5.gui.browse.processHash(true);
    }
});


function Browse(application, elem) {
    this.application = application;
    this.elem = elem;
    this._init();
}

Browse.prototype = {
    
    letters: ["0","A","B","C","Č","D","E","F","G","H","CH","I","J","K","L","M","N","O","P","Q","R","Ř","S","Š","T","U","V","W","X","Y","Z","Ž"],
    
    ctx: {},
    _init: function() {
        
        this.lettersList = $("#letters");
        this.letterTemplate = this.lettersList.find('.letter').clone();
        
        this.titlesList = $("#titles-list");
        this.titleItemTemplate = this.titlesList.find('.title-item').clone();
        this.authorsList = $("#authors-list");
        this.authorItemTemplate = this.authorsList.find('.author-item').clone();
        
        this.rowsPerRequest = 200;
        this.browseField = "browse_title";
        this.showField = "dc.title";
        this.input = $("#browse-input");
        
        this.renderLetters();
        this.processHash();
        this.input.keyup(_.bind(function(ev){
            this.typing =  true;
            this.doSuggest();
        }, this));
//        
//        setTimeout(function() {
//            this.checkSuggest()
//        }.bind(this), 1000);
        
    },
    checkSuggest: function(){
        if(!this.typing){
            this.doSuggest();
        }
        this.typing = false;
        setTimeout(function() {
            this.checkSuggest();
        }.bind(this), 500);
    },
    doSuggest: function(){
        var val = this.input.val();
        var hash = window.location.hash;
        if(val.length >= 1){
            //if(hash.length > 1 && val !== hash.substring(1)){
                window.location.hash = val.toUpperCase();
            //}
        }
    },
    processHash: function() {
        var hash = window.location.hash;
        var letter = "A";
        this.hash = "A";
        if (hash.length > 1) {
            hash = hash.substring(1);
            this.hash = hash;
            letter = hash.substring(0,1);
            if(hash.startsWith('CH')){
                letter = hash.substring(0,2);
            }
            
        }
        this.hash = this.replaceChars(this.hash);
        
        this.selectedLetter = letter;

        this.loadLetter();
        $(".letter").removeClass("active");
        $(".letter." + letter).addClass("active");
    },
    loadLetter: function(){
        this.getTitles(0);
        this.getAuthors(this.hash, true);
    },
    getAuthors: function(start, include){
        var q = "terms.fl=browse_autor&terms.limit="+this.rowsPerRequest+"&terms.lower.incl="+include+"&terms.sort=index&terms.lower=" + start;
        $('.authors .loading').show();
        this.authorsList.empty();
        K5.api.askForTerms(q, _.bind(function(data) {
            this.authorsList.find('.more_docs').remove();
            var arr = data.terms.browse_autor;
            for (var i = 0; i < arr.length; i++) {
                var br = arr[i++];
                var text = br.split("##")[1];
                var div = this.authorItemTemplate.clone();
                div.data("author", text);
                div.find(".name").text(text);
                div.click(function() {
                    var q = "&author=\"" + $(this).data("author") + "\""; 
                    K5.api.gotoResultsPage(q);
                });
                
                this.authorsList.append(div);
            }
            $('.authors .loading').hide();
            if (arr.length === this.rowsPerRequest * 2) {
                var nextStart = arr[arr.length-2];
                var more = $('<div class="more_docs" data-start="' + nextStart + '">more...</div>');
                this.authorsList.append(more);
                more.click(function() {
                    var q = "&author=\"" + $(this).parent().data("author") + "\""; 
                    K5.gui.browse.getAuthors($(this).data("start"), false);
                });
            }
        }, this), "application/json");
    },
    replaceChars: function(s){
        var ret = s;
        $.ajax({
            type: 'GET',
            url: "utfsort.vm?term=" + s,
            async:false
          }).success(function(data){
              ret = data;
          });
        return ret;
    },
    getTitles: function(start){
        
        var q = "sort=" + this.browseField + " asc&rows=" + this.rowsPerRequest + "&start=" + start +
                "&q=" + this.browseField + ":[\"" + this.hash + " *\" TO *]" +
                "&fl=PID,dc.title,dc.creator,datum_str,dostupnost";
        if(K5.indexConfig){
            q += "&fq=";
            var models = K5.indexConfig.browse.models;
            var modelField = K5.indexConfig.mappings.fedora_model;
            for(var i=0; i<models.length; i++){
                q += modelField + ':"' + models[i] + '"';
                if(i<models.length-1){
                    q += " OR ";
                }
            }
        }
        $('.titles .loading').show();
        this.titlesList.empty();
        K5.api.askForSolr(q, _.bind(function(data) {
            this.titlesList.find('.more_docs').remove();
            var arr = data.response.docs;
            for (var i = 0; i < arr.length; i++) {
                var doc = arr[i];
                var div = this.titleItemTemplate.clone();
                div.data("pid", doc.PID);
                var dcTitle = doc['dc.title'];
                if(dcTitle.length === 0){
                    dcTitle = K5.i18n.translatable("dctitle.none");
                }
                var title = div.find('.title');
                title.text(dcTitle);
                div.click(function(e) {
                    e.stopPropagation();
                    e.preventDefault();
                    K5.api.gotoDisplayingItemPage($(this).data("pid"));
                });
                var other = div.find(".other");
                if (doc['dc.creator']) {
                    other.append('<div>' + doc['dc.creator'] + '</div>');
                }
                if (doc['datum_str']) {
                    other.append('<div>' + doc['datum_str'] + '</div>');
                }
                
                if (doc['dostupnost']) {
                    div.addClass("app-item-" + doc['dostupnost']);
                    div.attr("title", doc['dostupnost']);
                }
                
                this.titlesList.append(div);
            }
            //this.resizeResults();
            $('.titles .loading').hide();
            var nextStart = start + this.rowsPerRequest;
            if (data.response.numFound > nextStart) {
                var more = $('<div class="more_docs" data-start="' + nextStart + '">more...</div>');
                this.titlesList.append(more);
                more.click(function() {
                    var q = "&author=\"" + $(this).parent().data("author") + "\""; 
                    K5.gui.browse.getTitles($(this).data("start"));
                });
            }

        }, this), "application/json");
    },
    letterClick: function(letter){
        this.input.val(letter);
        window.location.hash = letter;
    },
    renderLetters: function() {
        this.lettersList.empty();
            $.each(this.letters, _.bind(function(idx, letterDisp) {
                var letter = letterDisp;
                if(letter === "0"){
                    letter = "!";
                }
                var l = this.letterTemplate.clone();
                
                l.data("data-key", letter);
                l.addClass(letter);
                var a = l.find('.link');
                a.text(letter);
                a.attr("href", "#"+letter);
                
                l.click(_.bind(function() {
                    this.letterClick(letter);
                }, this));
                
                this.lettersList.append(l);
            }, this));
        
    }
};
