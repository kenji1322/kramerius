/* global K5, _ */

K5.eventsHandler.addHandler(function (type, data) {
    if (type === "results/loaded") {
        if (!K5.gui["facets"]) {
            K5.gui["facets"] = new Facets(data);
        }
    }
});


var Facets = function (data) {
    this.data = data;
    this._init();
};

Facets.prototype = {
    _init: function () {
        
        var facets = this.data.facet_counts.facet_fields;
        
        
        this.facetsContainer = $("#app-sidebar-wrapper>ul.app-sidebar-nav");
        this.facetTemplate = $("#app-sidebar-wrapper>ul.app-sidebar-nav>li.facet").clone();
        this.facetValueTemplate = this.facetTemplate.find('.facet-value').clone();
        
        this.facetsContainer.empty();
        this.hasUsed = false;
        this.renderUsed();
        this.render(facets);
        
        $("#facets div.used").click(_.partial(function (facets, event) {
            event.preventDefault();
            var val = $(this).data("key");
            var facet = $(this).data("facet");
            facets.removeFilter(facet, val);
        }, this));

    },
    
    addFilter: function (facet, val) {        
        if (val === "") {
            val = "none";
        }
        var input = $("<input>", {type: "hidden", value: val, name: facet, class: "facet"});
        $("#search_form").append(input);
        $('#search_form input[name="page"]').val("search");
        $("#start").val("0");
        $("#search_form").submit();
    },
    addRokFilter: function () {
        var selid = "#sel_rok";
        var min = $(selid).data("from");
        var max = $(selid).data("to");
        if (parseInt(min) && parseInt(max) && min <= max) {
            var val = '[' + min + ' TO ' + max + ']';

            var input = $("<input>", {type: "hidden", value: val, name: 'rok', class: "facet"});
            $("#search_form").append(input);
            $("#start").val("0");
            $("#search_form").submit();
        } else {
            alert("Invalid values");
        }

    },
    isUsed: function(facet, val){
        var ret = false;
        $("input[name='" + facet + "'][type='hidden']").each(function () {
            if ($(this).val() === val) {
                ret = true;
                return;
            }
        });
        
        return ret;
    },
    removeFilter: function (facet, val) {
        $("input[name='" + facet + "']").each(function () {
            if ($(this).val() === val) {
                $(this).remove();
            }
        });
        $('#search_form input[name="page"]').val("search");
        $("#search_form").submit();
    },
    removeAllFilters: function () {
        $("#search_form input.facet").remove();
        $('#search_form input[name="page"]').val("search");
        $("#search_form").submit();
    },
    
    
    rokFacet: function (div, minv, maxv) {
        var facetName = 'rok';
        var sel = $("<div/>", {class: 'sel'});
        var selid = "sel_" + facetName;
        sel.attr('id', selid);
        div.append(sel);

        var span = $("<span/>", {class: 'label'});
        span.text('od ' + minv + ' do ' + maxv);
        sel.append(span);

        var spango = $("<span/>", {class: 'go'});
        spango.text('go');
        sel.append(spango);

        var id = facetName + "_range";
        var range = $("<div/>", {class: "slid"});
        range.attr('id', id);
        range.data("min", minv);
        range.data("max", maxv);
        div.append(range);

        $(range).slider({
            range: true,
            min: minv,
            max: maxv,
            values: [minv, maxv],
            slide: function (event, ui) {
                $(sel).find("span.label").html("od " + ui.values[ 0 ] + " - do " + ui.values[ 1 ]);
                $(sel).data("from", ui.values[ 0 ]);
                $(sel).data("to", ui.values[ 1 ]);
            }
        });
        $(sel).find("span.go").button({
            icons: {
                primary: "ui-icon-arrowthick-1-e"
            },
            text: false
        });
        $(sel).find("span.go").click(_.bind(function () {
            this.addRokFilter();
        }, this));
    },
    
    
    renderUsed: function(){
        if($("#search_form input.facet").length === 0) return;
            this.hasUsed = true;
        var facetLi = this.facetTemplate.clone();
        facetLi.attr("id", "used-filters");
        var facetList = facetLi.find('.facet-list');
        facetList.attr("id", "app-collapse-used");
        facetList.empty();
        facetLi.find(".facet-name").html(K5.i18n.translatable('facets.used'));
        facetLi.find(".app-cursor-pointer").data("target", "#app-collapse-used");
        facetLi.find(".app-cursor-pointer").attr("data-target", "#app-collapse-used");
        facetList.addClass("in");
        this.facetsContainer.append(facetLi);
        
        if($("#search_form input[name='dostupnost']").is(":visible")){
            
        }
        
        $("#search_form input.facet").each(_.partial(function(facets){
            
            var val = $(this).data("key");
            var facet = $(this).data("facet");
            
            var facetValue = facets.facetValueTemplate.clone();
            var text = "";
            if (facet === "collection") {
                if(!K5.i18n.hasKey(val)){
                    // ignorujeme sbirky, pro ktere nemame nazev
                    return;
                }
                text = (K5.i18n.translatable(val));
            } else if (facet === "typ_titulu" || facet === "fedora.model" || facet === "model_path") {
                text = (K5.i18n.translatable("fedora.model." + val));
            } else if (facet === "dostupnost") {
                text = (K5.i18n.translatable("dostupnost." + val));
            } else {
                text = (val);
            }

            facetValue.data('facet', facet);
            facetValue.data('val', val);
            facetValue.click(_.partial(function (facets, event) {
                event.preventDefault();
                facets.removeFilter($(this).data('facet'), $(this).data('val'));
            }, facets));
            
            facetValue.find(".facet-value-text").html(text);
            facetValue.find(".facet-value-count").remove();
            facetList.append(facetValue);
            
                            
        }, this));
    },
    render: function (json) {
        var moreCount = 10;
        var root_models = {};
        $.each(json, _.bind(function (key, arr) {
            if (arr.length > 2) {
                
                var facetLi = this.facetTemplate.clone();
                var facetList = facetLi.find('.facet-list');
                facetList.attr("id", "app-collapse-"+key);
                facetList.empty();
                facetLi.find(".facet-name").html(K5.i18n.translatable('facet.' + key));
                facetLi.find(".app-cursor-pointer").data("target", "#app-collapse-"+key);
                facetLi.find(".app-cursor-pointer").attr("data-target", "#app-collapse-"+key);
                
                
                for (var i = 0; i < arr.length; i++) {
                    var val = arr[i];
                    var count = parseInt(arr[++i]);
                    
                    if(count > 0){
                
                        var facetValue = this.facetValueTemplate.clone();

                        if (key === "model_path") {
                            val = val.split("/")[0];
                            if (root_models[val]) {
                                root_models[val] = count + root_models[val];
                            } else {
                                root_models[val] = count;
                            }
                        } else {
                            var text = "";
                            if (key === "collection") {
                                if(!K5.i18n.hasKey(val)){
                                    // ignorujeme sbirky, pro ktere nemame nazev
                                    continue;
                                }
                                text = (K5.i18n.translatable(val));
                            } else if (key === "typ_titulu" || key === "fedora.model" || key === "model_path") {
                                text = (K5.i18n.translatable("fedora.model." + val));
                            } else if (key === "dostupnost") {
                                text = (K5.i18n.translatable("dostupnost." + val));
                            } else {
                                text = (val);
                            }
                            
                            facetValue.data('facet', key);
                            facetValue.data('val', val);
                            facetValue.click(_.partial(function (facets, event) {
                                event.preventDefault();
                                facets.addFilter($(this).data('facet'), $(this).data('val'));
                            }, this));

                            facetValue.find(".facet-value-text").html(text);
                            facetValue.find(".facet-value-count").text(count);
                            facetList.append(facetValue);
                        }
                    }
                }
                
                if (key !== "model_path") {
                    this.facetsContainer.append(facetLi);
                }
                
            }
        }, this));
        if (!jQuery.isEmptyObject(root_models) && Object.keys(root_models).length > 1) {
            
            var facetLi = this.facetTemplate.clone();
            var facetList = facetLi.find('.facet-list');
            facetList.attr("id", "app-collapse-type");
            facetList.empty();
            facetLi.find(".facet-name").html(K5.i18n.translatable('facet.model_path'));
            facetLi.find(".app-cursor-pointer").data("target", "#app-collapse-type");
            facetLi.find(".app-cursor-pointer").attr("data-target", "#app-collapse-type");
            $.each(root_models, _.bind(function (val, count) {
                
                if(count > 0){
                
                    var facetValue = this.facetValueTemplate.clone();
                    var text = (K5.i18n.translatable("fedora.model." + val));
                    facetValue.data('facet', 'typ_titulu');
                    facetValue.data('key', val);
                    facetValue.click(_.partial(function (facets, event) {
                        event.preventDefault();
                        facets.addFilter($(this).data('facet'), $(this).data('key'));
                    }, this));
                    
                    facetValue.find(".facet-value-text").html(text);
                    facetValue.find(".facet-value-count").text(count);
                    facetList.append(facetValue);
                    
                }
            }, this));

            if(!this.hasUsed){
                
                facetLi.find(".app-cursor-pointer").removeClass("collapsed");
                facetList.addClass("in");
                this.facetsContainer.prepend(facetLi);
            }else{
                $("#used-filters").after(facetLi);
            }
            
            
                            
        }
    }
};