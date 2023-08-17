/*
 * Copyright (C) 2020 Shift GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exthmui.microlauncher.duoqin.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.exthmui.microlauncher.duoqin.R;

import java.net.URISyntaxException;

public class PackageManagerHelper {

    /**
     * Creates a new market search intent.
     */
    public static Intent getMarketSearchIntent(Context context, String query) {
        try {
            Intent intent = Intent.parseUri(context.getString(R.string.market_search_intent), 0);
            if (!TextUtils.isEmpty(query)) {
                intent.setData(
                        intent.getData().buildUpon().appendQueryParameter("q", query).build());
            }
            return intent;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
