(function( $ ) {

module( "lazyLoader: core" );

test( "defaults", function() {
	expect( 5 );
	var element = $( "#lazyLoader" ).lazyLoader();
	ok( typeof element.data("lazyLoader")!="undefined", "data is stored in element" );
	ok( element.data("lazyLoader").jsUrls.length==0, "no js in store" );
	ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
	ok( element.data("lazyLoader").cssUrls.length==0, "no css in store" );
	ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );
});

test( "initialize with initial loaded libs", function() {
	expect( 5 );
	var options={
			jsUrls:["test.js"],
			cssUrls:["test.css"]
	};
	var element = $( "#lazyLoader" ).lazyLoader(options);
	ok( typeof element.data("lazyLoader")!="undefined", "data is stored in element" );
	ok( element.data("lazyLoader").jsUrls.length==1, "one js in store" );
	ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
	ok( element.data("lazyLoader").cssUrls.length==1, "one css in store" );
	ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );
});

test( "initialize with initial loading libs", function() {
	expect( 9 );
	var options={
			jsUrlQueue:["test.js"],
			cssUrlQueue:["test.css"]
	};
	var element = $( "#lazyLoader" );
	element.lazyLoader(options);
	
	ok( typeof element.data("lazyLoader")!="undefined", "data is stored in element" );
	ok( element.data("lazyLoader").jsUrls.length==0, "no js in store" );
	ok( element.data("lazyLoader").jsUrlQueue.length==1, "one js in queue" );
	ok( element.data("lazyLoader").cssUrls.length==0, "no css in store" );
	ok( element.data("lazyLoader").cssUrlQueue.length==1, "one css in queue" );
	element.one("scriptsLoaded",function(event,ui){
		ok( element.data("lazyLoader").jsUrls.length==1, "one js in store" );
		ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
		ok( element.data("lazyLoader").cssUrls.length==1, "one css in store" );
		ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );	
		start();
	});
	stop();
	element.lazyLoader("load");
	
});

test( "load Scripts", function() {
	expect( 14 );
	var options={};
	var element = $( "#lazyLoader" );
	element.lazyLoader(options);
	
	ok( typeof element.data("lazyLoader")!="undefined", "data is stored in element" );
	ok( element.data("lazyLoader").jsUrls.length==0, "no js in store" );
	ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
	ok( element.data("lazyLoader").cssUrls.length==0, "no css in store" );
	ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );
	
	element.one("scriptsLoaded",function(event,ui){
		ok( element.data("lazyLoader").jsUrls.length==1, "one js in store" );
		ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
		ok( element.data("lazyLoader").cssUrls.length==0, "no css in store" );
		ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );	
		start();
	});
	element.bind("scriptLoaded",function(event,ui){
		ok(true,"a script is loaded")
	});
	stop();
	element.lazyLoader("loadScripts",["test.js","test.js"]);
	
	element.one("scriptsLoaded",function(event,ui){
		ok( element.data("lazyLoader").jsUrls.length==1, "one js in store" );
		ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
		ok( element.data("lazyLoader").cssUrls.length==0, "no css in store" );
		ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );	
		start();
	});
	stop();
	element.lazyLoader("loadScripts",["test.js"]);
	
	
});

}( jQuery ) );
