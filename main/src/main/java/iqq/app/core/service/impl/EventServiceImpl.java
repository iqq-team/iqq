package iqq.app.core.service.impl;
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or UIplied.
 * See the License for the specific language governing permissions and
 * lUIitations under the License.
 */

import iqq.app.core.service.EventService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventListener;
import iqq.app.ui.event.UIEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 6/14/14
 * License  : Apache License 2.0
 */
@Service
public class EventServiceImpl implements EventService {
    private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);
    /**
     * Type => list<listener> *
     */
    private Map<UIEventType, List<UIEventListener>> lookup;

    public EventServiceImpl() {
        lookup = new HashMap<>();
    }

    @Override
    public void register(UIEventType[] intrestedEvents, UIEventListener listener) {
        for (UIEventType type : intrestedEvents) {
            List<UIEventListener> list = lookup.get(type);
            if (list == null) {
                list = new ArrayList<UIEventListener>();
                lookup.put(type, list);
            }
            list.add(listener);
        }
    }

    @Override
    public void unregister(UIEventType[] intrestedEvents, UIEventListener listener) {
        for (UIEventType type : intrestedEvents) {
            List<UIEventListener> list = lookup.get(type);
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    @Override
    public void unregister(UIEventListener listener) {
        unregister(lookup.keySet().toArray(new UIEventType[0]), listener);
    }

    @Override
    public void broadcast(final UIEvent event) {
        if (EventQueue.isDispatchThread()) {
            doBroadcast(event);
        } else {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    doBroadcast(event);
                }
            });
        }
    }

    private void doBroadcast(UIEvent event) {
        List<UIEventListener> list = lookup.get(event.getType());
        LOG.debug("Broadcast UIEvent: " + event.getType() + ", listeners: " + (list != null ? list.size() : 0));
        if (list != null && list.size() > 0) {
            list = new ArrayList<UIEventListener>(list);
            for (UIEventListener listener : list) {
                try {
                    listener.onUIEvent(event);
                } catch (Throwable e) {
                    LOG.warn("Broadcast UIEvent Error!", e);
                }
            }
        }
    }

}
