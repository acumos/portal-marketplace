/*! ngTagsInput v3.2.0 License: MIT */ ! function() {
    "use strict";
    var a = {
            backspace: 8,
            tab: 9,
            enter: 13,
            escape: 27,
            space: 32,
            up: 38,
            down: 40,
            left: 37,
            right: 39,
            "delete": 46,
            comma: 188
        },
        b = 9007199254740991,
        c = ["text", "email", "url"],
        d = angular.module("ngTagsInput", []);
    d.directive("tagsInput", ["$timeout", "$document", "$window", "$q", "tagsInputConfig", "tiUtil", function(d, e, f, g, h, i) {
        function j(a, b, c, d) {
            var e, f, h, j, k = {};
            return e = function(b) {
                return i.safeToString(b[a.displayProperty])
            }, f = function(b, c) {
                b[a.displayProperty] = c
            }, h = function(b) {
                var d = e(b),
                    f = d && d.length >= a.minLength && d.length <= a.maxLength && a.allowedTagsPattern.test(d) && !i.findInObjectArray(k.items, b, a.keyProperty || a.displayProperty);
                return g.when(f && c({
                    $tag: b
                })).then(i.promisifyValue)
            }, j = function(a) {
                return g.when(d({
                    $tag: a
                })).then(i.promisifyValue)
            }, k.items = [], k.addText = function(a) {
                var b = {};
                return f(b, a), k.add(b)
            }, k.add = function(c) {
                var d = e(c);
                return a.replaceSpacesWithDashes && (d = i.replaceSpacesWithDashes(d)), f(c, d), h(c).then(function() {
                    k.items.push(c), b.trigger("tag-added", {
                        $tag: c
                    })
                })["catch"](function() {
                    d && b.trigger("invalid-tag", {
                        $tag: c
                    })
                })
            }, k.remove = function(a) {
                var c = k.items[a];
                return j(c).then(function() {
                    return k.items.splice(a, 1), k.clearSelection(), b.trigger("tag-removed", {
                        $tag: c
                    }), c
                })
            }, k.select = function(a) {
                0 > a ? a = k.items.length - 1 : a >= k.items.length && (a = 0), k.index = a, k.selected = k.items[a]
            }, k.selectPrior = function() {
                k.select(--k.index)
            }, k.selectNext = function() {
                k.select(++k.index)
            }, k.removeSelected = function() {
                return k.remove(k.index)
            }, k.clearSelection = function() {
                k.selected = null, k.index = -1
            }, k.getItems = function() {
                return a.useStrings ? k.items.map(e) : k.items
            }, k.clearSelection(), k
        }

        function k(a) {
            return -1 !== c.indexOf(a)
        }
        return {
            restrict: "E",
            require: "ngModel",
            scope: {
                tags: "=ngModel",
                text: "=?",
                templateScope: "=?",
                tagClass: "&",
                onTagAdding: "&",
                onTagAdded: "&",
                onInvalidTag: "&",
                onTagRemoving: "&",
                onTagRemoved: "&",
                onTagClicked: "&"
            },
            replace: !1,
            transclude: !0,
            templateUrl: "ngTagsInput/tags-input.html",
            controller: ["$scope", "$attrs", "$element", function(a, c, d) {
                a.events = i.simplePubSub(), h.load("tagsInput", a, c, {
                    template: [String, "ngTagsInput/tag-item.html"],
                    type: [String, "text", k],
                    placeholder: [String, "Add a tag"],
                    tabindex: [Number, null],
                    removeTagSymbol: [String, String.fromCharCode(215)],
                    replaceSpacesWithDashes: [Boolean, !0],
                    minLength: [Number, 3],
                    maxLength: [Number, b],
                    addOnEnter: [Boolean, !0],
                    addOnSpace: [Boolean, !1],
                    addOnComma: [Boolean, !0],
                    addOnBlur: [Boolean, !0],
                    addOnPaste: [Boolean, !1],
                    pasteSplitPattern: [RegExp, /,/],
                    allowedTagsPattern: [RegExp, /.+/],
                    enableEditingLastTag: [Boolean, !1],
                    minTags: [Number, 0],
                    maxTags: [Number, b],
                    displayProperty: [String, "text"],
                    keyProperty: [String, ""],
                    allowLeftoverText: [Boolean, !1],
                    addFromAutocompleteOnly: [Boolean, !1],
                    spellcheck: [Boolean, !0],
                    useStrings: [Boolean, !1]
                }), a.tagList = new j(a.options, a.events, i.handleUndefinedResult(a.onTagAdding, !0), i.handleUndefinedResult(a.onTagRemoving, !0)), this.registerAutocomplete = function() {
                    d.find("input");
                    return {
                        addTag: function(b) {
                            return a.tagList.add(b)
                        },
                        getTags: function() {
                            return a.tagList.items
                        },
                        getCurrentTagText: function() {
                            return a.newTag.text()
                        },
                        getOptions: function() {
                            return a.options
                        },
                        getTemplateScope: function() {
                            return a.templateScope
                        },
                        on: function(b, c) {
                            return a.events.on(b, c, !0), this
                        }
                    }
                }, this.registerTagItem = function() {
                    return {
                        getOptions: function() {
                            return a.options
                        },
                        removeTag: function(b) {
                            a.disabled || a.tagList.remove(b)
                        }
                    }
                }
            }],
            link: function(b, c, g, h) {
                var j, k, l = [a.enter, a.comma, a.space, a.backspace, a["delete"], a.left, a.right],
                    m = b.tagList,
                    n = b.events,
                    o = b.options,
                    p = c.find("input"),
                    q = ["minTags", "maxTags", "allowLeftoverText"];
                j = function() {
                    h.$setValidity("maxTags", m.items.length <= o.maxTags), h.$setValidity("minTags", m.items.length >= o.minTags), h.$setValidity("leftoverText", b.hasFocus || o.allowLeftoverText ? !0 : !b.newTag.text())
                }, k = function() {
                    d(function() {
                        p[0].focus()
                    })
                }, h.$isEmpty = function(a) {
                    return !a || !a.length
                }, b.newTag = {
                    text: function(a) {
                        return angular.isDefined(a) ? (b.text = a, void n.trigger("input-change", a)) : b.text || ""
                    },
                    invalid: null
                }, b.track = function(a) {
                    return a[o.keyProperty || o.displayProperty]
                }, b.getTagClass = function(a, c) {
                    var d = a === m.selected;
                    return [b.tagClass({
                        $tag: a,
                        $index: c,
                        $selected: d
                    }), {
                        selected: d
                    }]
                }, b.$watch("tags", function(a) {
                    if (a) {
                        if (m.items = i.makeObjectArray(a, o.displayProperty), o.useStrings) return;
                        b.tags = m.items
                    } else m.items = []
                }), b.$watch("tags.length", function() {
                    j(), h.$validate()
                }), g.$observe("disabled", function(a) {
                    b.disabled = a
                }), b.eventHandlers = {
                    input: {
                        keydown: function(a) {
                            n.trigger("input-keydown", a)
                        },
                        focus: function() {
                            b.hasFocus || (b.hasFocus = !0, n.trigger("input-focus"))
                        },
                        blur: function() {
                            d(function() {
                                var a = e.prop("activeElement"),
                                    d = a === p[0],
                                    f = c[0].contains(a);
                                (d || !f) && (b.hasFocus = !1, n.trigger("input-blur"))
                            })
                        },
                        paste: function(a) {
                            a.getTextData = function() {
                                var b = a.clipboardData || a.originalEvent && a.originalEvent.clipboardData;
                                return b ? b.getData("text/plain") : f.clipboardData.getData("Text")
                            }, n.trigger("input-paste", a)
                        }
                    },
                    host: {
                        click: function() {
                            b.disabled || k()
                        }
                    },
                    tag: {
                        click: function(a) {
                            n.trigger("tag-clicked", {
                                $tag: a
                            })
                        }
                    }
                }, n.on("tag-added", b.onTagAdded).on("invalid-tag", b.onInvalidTag).on("tag-removed", b.onTagRemoved).on("tag-clicked", b.onTagClicked).on("tag-added", function() {
                    b.newTag.text("")
                }).on("tag-added tag-removed", function() {
                    b.tags = m.getItems(), h.$setDirty(), k()
                }).on("invalid-tag", function() {
                    b.newTag.invalid = !0
                }).on("option-change", function(a) {
                    -1 !== q.indexOf(a.name) && j()
                }).on("input-change", function() {
                    m.clearSelection(), b.newTag.invalid = null
                }).on("input-focus", function() {
                    c.triggerHandler("focus"), h.$setValidity("leftoverText", !0)
                }).on("input-blur", function() {
                    o.addOnBlur && !o.addFromAutocompleteOnly && m.addText(b.newTag.text()), c.triggerHandler("blur"), j()
                }).on("input-keydown", function(c) {
                    var d, e, f, g, h = c.keyCode,
                        j = {};
                    i.isModifierOn(c) || -1 === l.indexOf(h) || (j[a.enter] = o.addOnEnter, j[a.comma] = o.addOnComma, j[a.space] = o.addOnSpace, d = !o.addFromAutocompleteOnly && j[h], e = (h === a.backspace || h === a["delete"]) && m.selected, g = h === a.backspace && 0 === b.newTag.text().length && o.enableEditingLastTag, f = (h === a.backspace || h === a.left || h === a.right) && 0 === b.newTag.text().length && !o.enableEditingLastTag, d ? m.addText(b.newTag.text()) : g ? (m.selectPrior(), m.removeSelected().then(function(a) {
                        a && b.newTag.text(a[o.displayProperty])
                    })) : e ? m.removeSelected() : f && (h === a.left || h === a.backspace ? m.selectPrior() : h === a.right && m.selectNext()), (d || f || e || g) && c.preventDefault())
                }).on("input-paste", function(a) {
                    if (o.addOnPaste) {
                        var b = a.getTextData(),
                            c = b.split(o.pasteSplitPattern);
                        c.length > 1 && (c.forEach(function(a) {
                            m.addText(a)
                        }), a.preventDefault())
                    }
                })
            }
        }
    }]), d.directive("tiTagItem", ["tiUtil", function(a) {
        return {
            restrict: "E",
            require: "^tagsInput",
            template: '<ng-include src="$$template"></ng-include>',
            scope: {
                $scope: "=scope",
                data: "="
            },
            link: function(b, c, d, e) {
                var f = e.registerTagItem(),
                    g = f.getOptions();
                b.$$template = g.template, b.$$removeTagSymbol = g.removeTagSymbol, b.$getDisplayText = function() {
                    return a.safeToString(b.data[g.displayProperty])
                }, b.$removeTag = function() {
                    f.removeTag(b.$index)
                }, b.$watch("$parent.$index", function(a) {
                    b.$index = a
                })
            }
        }
    }]), d.directive("autoComplete", ["$document", "$timeout", "$sce", "$q", "tagsInputConfig", "tiUtil", function(b, c, d, e, f, g) {
        function h(a, b, c) {
            var d, f, h, i = {};
            return h = function() {
                return b.tagsInput.keyProperty || b.tagsInput.displayProperty
            }, d = function(a, c) {
                return a.filter(function(a) {
                    return !g.findInObjectArray(c, a, h(), function(a, c) {
                        return b.tagsInput.replaceSpacesWithDashes && (a = g.replaceSpacesWithDashes(a), c = g.replaceSpacesWithDashes(c)), g.defaultComparer(a, c)
                    })
                })
            }, i.reset = function() {
                f = null, i.items = [], i.visible = !1, i.index = -1, i.selected = null, i.query = null
            }, i.show = function() {
                b.selectFirstMatch ? i.select(0) : i.selected = null, i.visible = !0
            }, i.load = g.debounce(function(c, j) {
                i.query = c;
                var k = e.when(a({
                    $query: c
                }));
                f = k, k.then(function(a) {
                    k === f && (a = g.makeObjectArray(a.data || a, h()), a = d(a, j), i.items = a.slice(0, b.maxResultsToShow), i.items.length > 0 ? i.show() : i.reset())
                })
            }, b.debounceDelay), i.selectNext = function() {
                i.select(++i.index)
            }, i.selectPrior = function() {
                i.select(--i.index)
            }, i.select = function(a) {
                0 > a ? a = i.items.length - 1 : a >= i.items.length && (a = 0), i.index = a, i.selected = i.items[a], c.trigger("suggestion-selected", a)
            }, i.reset(), i
        }

        function i(a, b) {
            var c = a.find("li").eq(b),
                d = c.parent(),
                e = c.prop("offsetTop"),
                f = c.prop("offsetHeight"),
                g = d.prop("clientHeight"),
                h = d.prop("scrollTop");
            h > e ? d.prop("scrollTop", e) : e + f > g + h && d.prop("scrollTop", e + f - g)
        }
        return {
            restrict: "E",
            require: "^tagsInput",
            scope: {
                source: "&",
                matchClass: "&"
            },
            templateUrl: "ngTagsInput/auto-complete.html",
            controller: ["$scope", "$element", "$attrs", function(a, b, c) {
                a.events = g.simplePubSub(), f.load("autoComplete", a, c, {
                    template: [String, "ngTagsInput/auto-complete-match.html"],
                    debounceDelay: [Number, 100],
                    minLength: [Number, 3],
                    highlightMatchedText: [Boolean, !0],
                    maxResultsToShow: [Number, 10],
                    loadOnDownArrow: [Boolean, !1],
                    loadOnEmpty: [Boolean, !1],
                    loadOnFocus: [Boolean, !1],
                    selectFirstMatch: [Boolean, !0],
                    displayProperty: [String, ""]
                }), a.suggestionList = new h(a.source, a.options, a.events), this.registerAutocompleteMatch = function() {
                    return {
                        getOptions: function() {
                            return a.options
                        },
                        getQuery: function() {
                            return a.suggestionList.query
                        }
                    }
                }
            }],
            link: function(b, c, d, e) {
                var f, h = [a.enter, a.tab, a.escape, a.up, a.down],
                    j = b.suggestionList,
                    k = e.registerAutocomplete(),
                    l = b.options,
                    m = b.events;
                l.tagsInput = k.getOptions(), f = function(a) {
                    return a && a.length >= l.minLength || !a && l.loadOnEmpty
                }, b.templateScope = k.getTemplateScope(), b.addSuggestionByIndex = function(a) {
                    j.select(a), b.addSuggestion()
                }, b.addSuggestion = function() {
                    var a = !1;
                    return j.selected && (k.addTag(angular.copy(j.selected)), j.reset(), a = !0), a
                }, b.track = function(a) {
                    return a[l.tagsInput.keyProperty || l.tagsInput.displayProperty]
                }, b.getSuggestionClass = function(a, c) {
                    var d = a === j.selected;
                    return [b.matchClass({
                        $match: a,
                        $index: c,
                        $selected: d
                    }), {
                        selected: d
                    }]
                }, k.on("tag-added tag-removed invalid-tag input-blur", function() {
                    j.reset()
                }).on("input-change", function(a) {
                    f(a) ? j.load(a, k.getTags()) : j.reset()
                }).on("input-focus", function() {
                    var a = k.getCurrentTagText();
                    l.loadOnFocus && f(a) && j.load(a, k.getTags())
                }).on("input-keydown", function(c) {
                    var d = c.keyCode,
                        e = !1;
                    if (!g.isModifierOn(c) && -1 !== h.indexOf(d)) return j.visible ? d === a.down ? (j.selectNext(), e = !0) : d === a.up ? (j.selectPrior(), e = !0) : d === a.escape ? (j.reset(), e = !0) : (d === a.enter || d === a.tab) && (e = b.addSuggestion()) : d === a.down && b.options.loadOnDownArrow && (j.load(k.getCurrentTagText(), k.getTags()), e = !0), e ? (c.preventDefault(), c.stopImmediatePropagation(), !1) : void 0
                }), m.on("suggestion-selected", function(a) {
                    i(c, a)
                })
            }
        }
    }]), d.directive("tiAutocompleteMatch", ["$sce", "tiUtil", function(a, b) {
        return {
            restrict: "E",
            require: "^autoComplete",
            template: '<ng-include src="$$template"></ng-include>',
            scope: {
                $scope: "=scope",
                data: "="
            },
            link: function(c, d, e, f) {
                var g = f.registerAutocompleteMatch(),
                    h = g.getOptions();
                c.$$template = h.template, c.$index = c.$parent.$index, c.$highlight = function(c) {
                    return h.highlightMatchedText && (c = b.safeHighlight(c, g.getQuery())), a.trustAsHtml(c)
                }, c.$getDisplayText = function() {
                    return b.safeToString(c.data[h.displayProperty || h.tagsInput.displayProperty])
                }
            }
        }
    }]), d.directive("tiTranscludeAppend", function() {
        return function(a, b, c, d, e) {
            e(function(a) {
                b.append(a)
            })
        }
    }), d.directive("tiAutosize", ["tagsInputConfig", function(a) {
        return {
            restrict: "A",
            require: "ngModel",
            link: function(b, c, d, e) {
                var f, g, h = a.getTextAutosizeThreshold();
                f = angular.element('<span class="input"></span>'), f.css("display", "none").css("visibility", "hidden").css("width", "auto").css("white-space", "pre"), c.parent().append(f), g = function(a) {
                    var b, e = a;
                    return angular.isString(e) && 0 === e.length && (e = d.placeholder), e && (f.text(e), f.css("display", ""), b = f.prop("offsetWidth"), f.css("display", "none")), c.css("width", b ? b + h + "px" : ""), a
                }, e.$parsers.unshift(g), e.$formatters.unshift(g), d.$observe("placeholder", function(a) {
                    e.$modelValue || g(a)
                })
            }
        }
    }]), d.directive("tiBindAttrs", function() {
        return function(a, b, c) {
            a.$watch(c.tiBindAttrs, function(a) {
                angular.forEach(a, function(a, b) {
                    c.$set(b, a)
                })
            }, !0)
        }
    }), d.provider("tagsInputConfig", function() {
        var a = {},
            b = {},
            c = 3;
        this.setDefaults = function(b, c) {
            return a[b] = c, this
        }, this.setActiveInterpolation = function(a, c) {
            return b[a] = c, this
        }, this.setTextAutosizeThreshold = function(a) {
            return c = a, this
        }, this.$get = ["$interpolate", function(d) {
            var e = {};
            return e[String] = function(a) {
                return a
            }, e[Number] = function(a) {
                return parseInt(a, 10)
            }, e[Boolean] = function(a) {
                return "true" === a.toLowerCase()
            }, e[RegExp] = function(a) {
                return new RegExp(a)
            }, {
                load: function(c, f, g, h) {
                    var i = function() {
                        return !0
                    };
                    f.options = {}, angular.forEach(h, function(h, j) {
                        var k, l, m, n, o, p;
                        k = h[0], l = h[1], m = h[2] || i, n = e[k], o = function() {
                            var b = a[c] && a[c][j];
                            return angular.isDefined(b) ? b : l
                        }, p = function(a) {
                            f.options[j] = a && m(a) ? n(a) : o()
                        }, b[c] && b[c][j] ? g.$observe(j, function(a) {
                            p(a), f.events.trigger("option-change", {
                                name: j,
                                newValue: a
                            })
                        }) : p(g[j] && d(g[j])(f.$parent))
                    })
                },
                getTextAutosizeThreshold: function() {
                    return c
                }
            }
        }]
    }), d.factory("tiUtil", ["$timeout", "$q", function(a, b) {
        var c = {};
        return c.debounce = function(b, c) {
            var d;
            return function() {
                var e = arguments;
                a.cancel(d), d = a(function() {
                    b.apply(null, e)
                }, c)
            }
        }, c.makeObjectArray = function(a, b) {
            if (!angular.isArray(a) || 0 === a.length || angular.isObject(a[0])) return a;
            var c = [];
            return a.forEach(function(a) {
                var d = {};
                d[b] = a, c.push(d)
            }), c
        }, c.findInObjectArray = function(a, b, d, e) {
            var f = null;
            return e = e || c.defaultComparer, a.some(function(a) {
                return e(a[d], b[d]) ? (f = a, !0) : void 0
            }), f
        }, c.defaultComparer = function(a, b) {
            return c.safeToString(a).toLowerCase() === c.safeToString(b).toLowerCase()
        }, c.safeHighlight = function(a, b) {
            function d(a) {
                return a.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1")
            }
            if (a = c.encodeHTML(a), b = c.encodeHTML(b), !b) return a;
            var e = new RegExp("&[^;]+;|" + d(b), "gi");
            return a.replace(e, function(a) {
                return a.toLowerCase() === b.toLowerCase() ? "<em>" + a + "</em>" : a
            })
        }, c.safeToString = function(a) {
            return angular.isUndefined(a) || null == a ? "" : a.toString().trim()
        }, c.encodeHTML = function(a) {
            return c.safeToString(a).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
        }, c.handleUndefinedResult = function(a, b) {
            return function() {
                var c = a.apply(null, arguments);
                return angular.isUndefined(c) ? b : c
            }
        }, c.replaceSpacesWithDashes = function(a) {
            return c.safeToString(a).replace(/\s/g, "-")
        }, c.isModifierOn = function(a) {
            return a.shiftKey || a.ctrlKey || a.altKey || a.metaKey
        }, c.promisifyValue = function(a) {
            return a = angular.isUndefined(a) ? !0 : a, b[a ? "when" : "reject"]()
        }, c.simplePubSub = function() {
            var a = {};
            return {
                on: function(b, c, d) {
                    return b.split(" ").forEach(function(b) {
                        a[b] || (a[b] = []);
                        var e = d ? [].unshift : [].push;
                        e.call(a[b], c)
                    }), this
                },
                trigger: function(b, d) {
                    var e = a[b] || [];
                    return e.every(function(a) {
                        return c.handleUndefinedResult(a, !0)(d)
                    }), this
                }
            }
        }, c
    }]), d.run(["$templateCache", function(a) {
        a.put("ngTagsInput/tags-input.html", '<div class="host" tabindex="-1" ng-click="eventHandlers.host.click()" ti-transclude-append><div class="tags" ng-class="{focused: hasFocus}"><ul class="tag-list"><li class="tag-item" ng-repeat="tag in tagList.items track by track(tag)" ng-class="getTagClass(tag, $index)" ng-click="eventHandlers.tag.click(tag)"><ti-tag-item scope="templateScope" data="::tag"></ti-tag-item></li></ul><input class="input" autocomplete="off" ng-model="newTag.text" ng-model-options="{getterSetter: true}" ng-keydown="eventHandlers.input.keydown($event)" ng-focus="eventHandlers.input.focus($event)" ng-blur="eventHandlers.input.blur($event)" ng-paste="eventHandlers.input.paste($event)" ng-trim="false" ng-class="{\'invalid-tag\': newTag.invalid}" ng-disabled="disabled" ti-bind-attrs="{type: options.type, placeholder: options.placeholder, tabindex: options.tabindex, spellcheck: options.spellcheck}" ti-autosize></div></div><span style="display:none" ng-class="{\'invalid-tag\': newTag.invalid}">Duplicate tags cannot be added.</span> '), a.put("ngTagsInput/tag-item.html", '<span ng-bind="$getDisplayText()"></span> <a class="remove-button" ng-click="$removeTag()" ng-bind="::$$removeTagSymbol"></a>'), a.put("ngTagsInput/auto-complete.html", '<div class="autocomplete" ng-if="suggestionList.visible"><ul class="suggestion-list"><li class="suggestion-item" ng-repeat="item in suggestionList.items track by track(item)" ng-class="getSuggestionClass(item, $index)" ng-click="addSuggestionByIndex($index)" ng-mouseenter="suggestionList.select($index)"><ti-autocomplete-match scope="templateScope" data="::item"></ti-autocomplete-match></li></ul></div>'), a.put("ngTagsInput/auto-complete-match.html", '<span ng-bind-html="$highlight($getDisplayText())"></span>')
    }])
}();