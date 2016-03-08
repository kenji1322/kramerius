
/* 
 * Copyright (C) 2016 alberto
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/* global K5, _ */


var TimelineRow = function(config) {
    this.year = config.year;
    this.container = config.container;
    this.scroll = this.container.find(".app-timeline-scroller");
    this.countContainer = this.container.find(".app-year-count");
    this.titleContainer = this.container.find(".app-year-title");
    this.itemTempl = config.itemTempl;
    this.titleContainer.text(this.year);
    
    
    this.btnRight = this.container.find(".app-timeline-btn-group-right>.app-cursor-pointer");
    this.btnRight.click(_.bind(function(){
        this.slide(1);
    }, this));

    this.btnLeft = this.container.find(".app-timeline-btn-group-left>.app-cursor-pointer");
    this.btnLeft.click(_.bind(function(){
        this.slide(-1);
    }, this));
        
    this.titleContainer.click(_.bind(function(){
        this.addRokFilter();
    }, this));
    
    this.getDocs({"offset":0});
};

TimelineRow.prototype = {
    rowsPerRequest: 50,
    getDocs: function(params) {
        var query = "fl=root_pid,dc.creator,root_title,dc.title,PID,dostupnost,model_path,fedora.model"+
                "&group=true&group.ngroups=true&group.field=root_pid&group.format=simple&group.sort=level asc"+
                "&q=rok:" + this.year + "&start=" + params.offset + "&rows=" + this.rowsPerRequest;
        K5.api.askForSolr(query, _.bind(function(data) {
            this.docs = {"docs": data.grouped.root_pid.doclist.docs, "count": data.grouped.root_pid.ngroups};
            this.countContainer.text(data.grouped.root_pid.ngroups);
            this.render();
        }, this), "application/json");

    },
    addRokFilter: function () {
        var min = this.year;
        var max = this.year;
        if (parseInt(min) && parseInt(max) && min <= max) {
            var val = '[' + min + ' TO ' + max + ']';

            var input = $("<input>", {type: "hidden", value: this.year, name: 'rok', class: "facet"});
            $("#search_form").append(input);
            $("#start").val("0");
            $("#search_form").submit();
        } else {
            alert("Invalid values");
        }

    },
    render: function() {
        this.scroll.empty();
        var docs = this.docs.docs;
        for (var i = 0; i < docs.length; i++) {
            var item = this.itemTempl.clone();
            var doc = docs[i];

            var pid = doc["PID"];
            
            item.find("img").attr("src", "api/item/" + pid + "/thumb");
            
            item.find("[data-field]").each(function(){
                var f = $(this).data("field");
                if(doc[f]){
                    $(this).text(doc[f]);
                }
            });
            
            this.scroll.append(item);
            
            //this.addThumb(docs[i]);
        }

    },
    addThumb: function(doc) {
        var pid = doc["PID"];
        var root_pid = doc["root_pid"];
        var model = doc["fedora.model"];
        var imgsrc = "api/item/" + pid + "/thumb";
        var thumb = $('<li/>', {class: 'thumb'});
        thumb.data("metadata", doc["root_title"]);
        var title = doc["root_title"];
        var dctitle = doc["dc.title"];
        var typtitulu = doc["model_path"][0].split("/")[0];
        var shortTitle = title;
        var creator = "";
        var maxLength = 90;
        var showToolTip = false;
        if (shortTitle.length > maxLength) {
            shortTitle = shortTitle.substring(0, maxLength) + "...";
            showToolTip = true;
        }
        shortTitle = '<div class="title">' + shortTitle + '</div>';
        if (doc["dc.creator"]) {
            creator = '<div class="autor">' + doc["dc.creator"] + '</div>';
        }
        var titletag = '<div class="title">' + title + '</div>';
        if(title !== dctitle){
            titletag = titletag + '<div class="dctitle">' + dctitle + '</div>';
            shortTitle = shortTitle + '<div class="dctitle">' + dctitle.substring(Math.min(30, dctitle.length)) + '</div>';
        }
        var modeltag = '<div class="title">' + K5.i18n.translatable('fedora.model.' + model) + '</div>';
        thumb.data("pid", pid);
        thumb.data("root_pid", root_pid);
        this.container.append(thumb);
        var policy = $('<div/>', {class: 'policy'});
        if (doc['dostupnost']) {
            policy.addClass(doc['dostupnost']);
        }
        thumb.append(policy);
        if (showToolTip) {
            thumb.attr("title", titletag + creator + modeltag);
            thumb.tooltip({
                content: titletag + creator,
                position: {my: "left bottom-1", at: "right-1 bottom"}
            });
        }
        thumb.click(function() {
            K5.api.gotoDisplayingItemPage($(this).data('pid'));
        });

        var divimg = $('<div/>', {class: 'img'});
        var img = $('<img/>', {src: imgsrc});
        $(thumb).append(divimg);
        $(divimg).append(img);

        var ithumb = $('<div/>', {class: 'info'});
        ithumb.html(shortTitle + creator + modeltag);
        thumb.append(ithumb);
    },
    slide: function(dx){
        var speed = 500;
        var w = this.container.width();
        var finalPos = this.scroll.scrollLeft() + w * 0.7 * dx;
        this.scrolling = true;
        this.scroll.animate({scrollLeft:finalPos}, speed, _.bind(function() {
                this.scrolling = false;
        },this));
        
        if(finalPos <=0){
            this.btnLeft.hide();
        }else{
            this.btnLeft.show();
        }
        
        
        if(finalPos >= w){
            this.btnRight.hide();
        }else{
            this.btnRight.show();
        }
    }
};
