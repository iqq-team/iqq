package iqq.api.common;
 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 6/14/14
 * License  : Apache License 2.0
 */
public class Intent implements Serializable {
    protected Map<String, Object> dataMap = new HashMap<String, Object>();

    public void putData(String key, Object value){
        this.dataMap.put(key, value);
    }

    public boolean hasData(String key){
        return this.dataMap.containsKey(key);
    }

    public <T> T getData(String key){
        return (T) this.dataMap.get(key);
    }

    public int getInt(String key){
        return getDataEx(key);
    }

    public long getLong(String key){
        return getDataEx(key);
    }

    public boolean getBoolean(String key){
        return getDataEx(key);
    }

    public String getString(String key){
        return getDataEx(key);
    }

    private <T> T getDataEx(String key){
        if(hasData(key)){
            return (T) getData(key);
        }
        return null;
    }
}
