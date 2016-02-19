/* global _, K5 */

K5.eventsHandler.addHandler(function(type, data) {
    

    if (type === "application/init/end") {
        /* if (K5.authentication.ctx["profile"] && K5.authentication.ctx.profile["favorites"]) {
                var mapped = _.map(K5.authentication.ctx.profile.favorites, function(pid) { 
                        var obj = {};
                        obj["pid"] = pid;                       
                        obj["title"] = 'title';                       
                        return  obj; 
                });
                var data = {'data':mapped};
                K5.gui.currasels["profilefavorites"] = new Carousel("#yearRows>div.profilefavorites", {"json": data}, true);
                K5.gui.currasels["profilefavorites"].setName("profilefavorites");
        }  
        */
        K5.gui.home = new Doc(K5);
    }
});



function Doc(application) {
    this.application = application;
    this._init();
}

Doc.prototype = {
    ctx: {},
    _init: function() {
    }
    
};
