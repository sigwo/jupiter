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

import nxt.Asset;
import nxt.Nxt;
import nxt.NxtException;
import nxt.Transaction;
import nxt.util.Convert;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class GetAsset extends APIServlet.APIRequestHandler {

    static final GetAsset instance = new GetAsset();

    private GetAsset() {
        super(new APITag[] {APITag.AE}, "asset", "includeCounts");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) throws NxtException {
        boolean includeCounts = "true".equalsIgnoreCase(req.getParameter("includeCounts"));
        Asset asset = ParameterParser.getAsset(req);
        JSONObject assetJson = JSONData.asset(asset, includeCounts);
        
        if (assetJson.containsKey("asset")){
        	 Transaction transaction = Nxt.getBlockchain().getTransaction(asset.getId());
        	 if (transaction != null && transaction.getMessage() != null) {
     			String messageString = Convert.toString(transaction.getMessage().getMessage(), transaction.getMessage().isText());
     			assetJson.put(MESSAGE_FIELD, messageString);
     		}
        }
        
        return assetJson;
    }

}
