(function($, undefined) {

	$.widget("ui.worksheet", {
		version : "0.1.0",
		options : {
			isInitialized:false,
			sessionId:0,
			jsUrls : [ "javascripts/libs/jquery-ui-1.9.2/ui/jquery.ui.menubar.js" ],
			cssUrls : [ "stylesheets/jquery-ui-1.9.2/themes/base/jquery.ui.menubar.css" ],
			initialized : function(event) {},
			optionsChanged : function(event, options) {
				if(!$.browser.msie)
					window.console.debug("Event: optionsChanged from worksheet: " + options.id);
			}
		},
		//jTODO Blocks[] is not needed;
		blocks : [],
		blocksLoading : 0,
		_create : function() {
			//DEBUG alert("worksheet _create");
			$("body").lazyLoader();
			$("body").lazyLoader("loadStyles", this.options.cssUrls);
			$("body").one("scriptsLoaded", 0, $.proxy(this._create2, this));
			$("body").lazyLoader("loadScripts", this.options.jsUrls);
			this.options.isInitialized=false;
			
		},
		_create2 : function() {
			//DEBUG alert("worksheet _create2");
			this.element.addClass("ui-worksheet ui-widget ui-corner-none");

			this.options.id = this.element.attr("id");
			
			if (this.options.hasMenu) {
				var worksheetMenu = null;
				if (this.options.menu.length > 0) {
					worksheetMenu = this.createULFromNodeArrayRecursive(this.options.menu);
				} else {
					worksheetMenu = $("<ul></ul>");
				}
				worksheetMenu.addClass("ui-worksheet-menu").menubar();
				this.element.append(worksheetMenu);

			}
			if (this.options.hasBody) {
				var worksheetBody = $("<div></div>").addClass("ui-worksheet-body ui-widget-content ");
				this.element.append(worksheetBody);
				worksheetBody.sortable({
					items : "> .ui-block",
					handle : ".ui-sort-handle",
					update : function(event, ui) {
						$("#ui-id-1").worksheet("moveInBlocks", parseInt(ui.item.attr("tabindex")) - 1, ui.item.prevAll().length);
					}
				});

				if (this.options.blocks.length > 0) {
					//JTODO Why Backup?
					var blockOptions = this.options.blocks;
					this.options.blocks = [];
					for ( var x = 0; x < blockOptions.length; x++) {
						this.appendBlock(blockOptions[x]);						
					}
					
					
				}
			}
		},
		createULFromNodeArrayRecursive : function(nodes) {
			//DEBUG alert("worksheet createULFromNodeArrayRecursive");
			
			var menu = $("<ul></ul>");
			for ( var x = 0; x < nodes.length; x++) {
				var menuItem = this.nodeToUL(nodes[x]);
				if (nodes[x].children.length > 0) {
					menuItem.append(this.createULFromNodeArrayRecursive(nodes[x].children));
				}
				menu.append(menuItem);
			}
			return menu;
		},
		nodeToUL : function(node) {
			//DEBUG alert("worksheet nodeToUL");
			
			var nodeItem = $("<li></li>");
			if (node.itemClass != "") {
				nodeItem.addClass(node.itemClass);
			}
			var item = $("<a></a>");

			if (node.iconClass != "") {
				var icon = $("<span></span>").addClass("ui-icon " + node.iconClass);
				item.append(icon);
			}
			if (node.text != "") {
				item.append(node.text);
			}
			item.click(node.click);
			nodeItem.append(item);
			return nodeItem;
		},
		appendBlock : function(blockOptions) {
			//DEBUG alert("worksheet appendBlock");
			
			var index = this.options.blocks.length;
			return this.insertBlock(blockOptions, index);
		},
		insertBlock : function(blockOptions,index){
			// set options for block if needed
			//DEBUG alert("worksheet insertBlock");
			if (blockOptions != null && blockOptions != {}){
				blockOptions.worksheetId = this.options.id;
				this.insertIntoBlocks(index, blockOptions);
			}
			this.blocksLoading++;
			var block = $("<div></div>");
			if (this.options.blocks.length > 1) {
				$(this.element.find(".ui-worksheet-body>.ui-block")[index - 1]).after(block);
			} else {
				this.element.find(".ui-worksheet-body").append(block);
			}
			block.one("blockinitialized",$.proxy(this.blockLoaded,this));
			block.block(this.options.blocks[index]);
			block.bind("blockoptionschanged", $.proxy(function(event, options) {
				var index=this.getBlockIndexById(options.id);
				this.options.blocks[index]=options;
				this._trigger("optionsChanged", 0, [ this.options ]);
			}, this));
			block.attr("tabindex",index+1);
			this.element.find(".ui-worksheet-body").sortable("refresh");
			this._trigger("optionsChanged",0,this.options);
		},
		blockLoaded: function(event,id){
			//DEBUG alert("worksheet blockLoaded");
			
			this.blocksLoading--;
			if(!this.options.isInitialized && this.blocksLoading==0 && this._blocksInitialized()){
				this._triggerInitialized();
				this._trigger("optionsChanged",0,[this.options]);
			}
		},
		_blocksInitialized:function(){
			//DEBUG alert("worksheet _blocksInitialized");
			for(var x=0;x<this.options.blocks.length;x++){
				if(!this.options.blocks[x].isInitialized)
					return false;
			}
			return true;
		},
		insertIntoBlocks : function(index, element) {
			//DEBUG alert("worksheet insertIntoBlocks");
			this.options.blocks.push(null);
			for ( var x = this.options.blocks.length - 1; x > index; x--) {
				this.options.blocks[x] = this.options.blocks[x - 1];
			}
			this.options.blocks[x] = element;
			$("#" + element.id).attr("tabindex", index + 1);
		},
		removeFromBlocks : function(index) {
			//DEBUG alert("worksheet removeFromBlocks");
			
			for ( var x = index; x < this.options.blocks.length - 1; x++) {
				this.options.blocks[x] = this.options.blocks[x + 1];
			}
			this.options.blocks.pop();
		},
		moveInBlocks : function(indexFrom, indexTo) {
			//DEBUG alert("worksheet moveInBlocks");
			
			var temp = this.options.blocks[indexTo];
			this.options.blocks[indexTo] = this.options.blocks[indexFrom];
			this.options.blocks[indexFrom] = temp;
			$("#" + this.options.blocks[indexTo].id).attr("tabindex", indexTo + 1);
			$("#" + this.options.blocks[indexFrom].id).attr("tabindex", indexFrom + 1);
			// TODO inform server about position change
			// TODO call eval (following)
		},
		removeBlock : function(index) {
			//DEBUG alert("worksheet removeBlock");
			
			var block=$("#" + this.options.blocks[index].id);
			block.block("destroy");
			block.remove();
			for ( var x = index; x < this.options.blocks.length - 1; x++) {
				this.options.blocks[x] = this.options.blocks[x + 1];
			}
			this.options.blocks.pop();
			this.element.find(".ui-worksheet-body").sortable("refresh");
		},
		getBlockIndexById : function(id) {
			//DEBUG alert("worksheet getBlockIndexById");
			for ( var x = 0; x < this.options.blocks.length; x++) {
				if (this.options.blocks[x].id == id)
					break;
			}
			return x;
		},
		removeBlockById : function(id) {
			//DEBUG alert("worksheet removeBlockById");
			
			var index = this.getBlockIndexById(id);
			this.removeBlock(index);
		},
		insertMenu : function(menu) {
			//DEBUG alert("worksheet insertMenu");
			
			this.element.children(".ui-worksheet-menu").empty().append(menu);
		},
		isLastBlock : function(blockId) {
			//DEBUG alert("worksheet isLastBlock");
			return this.blocks[this.blocks.length - 1].block("option", "id") == blockId;
		},
		getBlocks : function() {
			//DEBUG alert("worksheet getBlocks");
			return this.blocks;
		},
		_setOption :function(key,val){
			this._super( "_setOption", key, value );
			this._trigger("optionsChanged",0,this.options);
		},
		evaluate:function(blockId){
			//DEBUG alert("worksheet evaluate");
			var msg=this.options.blocks[this.getBlockIndexById(blockId)];
            delete msg.menu;
            
			var content = this._addParameter("", "block", $.toJSON(msg));
			content = this._addParameter(content,"worksheetSessionId", wsid);
			$.ajax("setBlock", {
				type : "POST",
				data : content
			}).done($.proxy(function(data, status, xhr) {
				var content = this._addParameter("", "id", blockId);
	            content = this._addParameter(content, "worksheetSessionId", wsid);
	            $.ajax("evaluate", {
					type : "POST",
					data : content
				}).done($.proxy(function(data, status, xhr) {
					var text=xhr.responseText;
					data = jQuery.parseJSON(xhr.responseText);
					data = $.recursiveFunctionTest(data);

					var index=this.getBlockIndexById(data[0].id);
					for(var x=this.options.blocks.length-1;x>=index;x--){
						this.removeBlock(x);
					}

					for ( var x = 0; x < data.length; x++) {
						 this.appendBlock(data[x]);
					}
				},this));
			},this));
		},
		_addParameter : function(res, key, value) {
			if (res != "")
				res += "&";
			res += encodeURIComponent(key) + "="
					+ encodeURIComponent(value);
			return res;
		},
		_destroy : function(){
			$(".ui-block")
				.block("destroy")
				.remove();
			this.element
				.empty()
				.removeClass("ui-worksheet ui-widget ui-corner-none");
			if(this.element.attr("class")=="")
				this.element.removeAttr("class");
			//LazyLoader needs to be destroyed seperatly because it could be needed in future or by other plugins
		},
		_triggerInitialized:function(){
			//DEBUG alert("worksheet _triggerInitialized");
			this.options.isInitialized=true;
			if(!$.browser.msie){
				window.console.debug("Event: initialized from worksheet");				
			}
			this._trigger("initialized",0,[this.options.id]);
		}

	});
}(jQuery));
