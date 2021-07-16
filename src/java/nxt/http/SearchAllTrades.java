/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016-2017 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of the Nxt software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package nxt.http;

import nxt.Order;
import nxt.Trade;
import nxt.db.DbIterator;
import nxt.util.Convert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class SearchAllTrades extends APIServlet.APIRequestHandler {

    static final SearchAllTrades instance = new SearchAllTrades();

    private SearchAllTrades() {
        super(new APITag[] {APITag.AE}, "query", "timestamp", "firstIndex", "lastIndex");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) throws ParameterException {
        String query = Convert.nullToEmpty(req.getParameter("query"));
        if (query.isEmpty()) {
            return JSONResponses.missing("query");
        }
        
        final int timestamp = ParameterParser.getTimestamp(req);
        int firstIndexToInclude = ParameterParser.getFirstIndex(req);
        int lastIndexToInclude = ParameterParser.getLastIndex(req);
        
        JSONObject response = new JSONObject();
        JSONArray trades = new JSONArray();
        int elementsFiltered = 0;
        try (DbIterator<Trade> tradeIterator = Trade.getAllTrades()) {
            while (tradeIterator.hasNext()) {
                Trade trade = tradeIterator.next();
                if (trade.getTimestamp() < timestamp) {
                    break;
                }
                JSONObject tradeJSON = JSONData.trade(trade, true);
                
                // filter by name or description fields by the query parameter
            	if (matchByName(tradeJSON, query)) {
            		if (elementsFiltered >= firstIndexToInclude && elementsFiltered <= lastIndexToInclude) {
            			trades.add(tradeJSON);
            		}
            		
            		elementsFiltered++;
            		
            		if (elementsFiltered > lastIndexToInclude) {
            			break;
            		}
            	}
            }
        }
        
       response.put("trades", trades);
       return response;
    }
    
    private boolean matchByName(JSONObject tradeJSON, String query) {
		if ((tradeJSON.containsKey(NAME_FIELD) && ((String)tradeJSON.get(NAME_FIELD)).toLowerCase().indexOf(query) != -1)){
			return true;
		} 

		return false;
    }
}
