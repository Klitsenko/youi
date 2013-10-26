var visualSearchCache = {};

function setVisualSearchQuery(id, query) {
    var vs = window['vs' + id];
    vs.searchBox.value(query);
}

function createVisualSearch(pageId, id, url) {
    var facetCacheKey = id + '.facets';

    $(document).ready(function() {
        window['vs' + id] = VS.init({
            container: $('#' + id),
            query: '',
            autosearch: true,
            showFacets: true,
            unquotable: [],
            remainder: 'text',
            preserveMatches: true,
            callbacks  : {
                search : function(query, searchCollection) {
                    visualSearchCache[facetCacheKey] = null;
                    realtimeSend(id, 'search', {
                        query: query,
                        facets: searchCollection.facets()
                    });
                },
                facetMatches : function(callback) {
                    var cache = visualSearchCache[facetCacheKey];
                    if (cache != null) {
                        callback(cache);
                    } else {
                        $.getJSON(url, {
                            pageId: pageId,
                            fieldId: id,
                            requestType: 'facets',
                            r: Math.random()
                        }, function(result) {
//                            visualSearchCache[facetCacheKey] = result;
                            callback(result, { preserveOrder: true });
                        });
                    }
                },
                valueMatches : function(facet, searchTerm, callback) {
                    var facetResultsKey = id + '.' + facet;
                    var facetExactMatchKey = id + '.' + facet + '.exactMatch';
                    var cache = visualSearchCache[facetResultsKey];
                    if (cache != null) {
                        var exactMatch = visualSearchCache[facetExactMatchKey];
                        searchCallback(searchTerm, cache, exactMatch, callback);
                    } else {
                        $.getJSON(url, {
                            pageId: pageId,
                            fieldId: id,
                            requestType: 'values',
                            facet: facet,
                            term: searchTerm,
                            r: Math.random()
                        }, function(result) {
                            var results = result.results;
                            var exactMatch = result.exactMatch;
                            searchCallback(searchTerm, results, exactMatch, callback);
                        });
                    }
                }
            }
        });
    });
}

function searchCallback(searchTerm, results, exactMatch, callback) {
    if (!exactMatch && results.length > 1 && searchTerm != '') {
        results = results.slice(0);
        results.unshift({
            value: searchTerm,
            label: 'Search for ' + searchTerm
        });
    }
//    console.log('Callback: ' + results);
    callback(results);
}