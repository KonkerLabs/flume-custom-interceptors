// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FlattenMapJSONHandler.java

package com.konkerlabs.analytics.ingestion.handler;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlattenMapJSONHandler implements HTTPSourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JSONHandler.class);
    private Type type = new TypeToken<HashMap<String, String>>() {}.getType();
    private final Gson gson = (new GsonBuilder()).disableHtmlEscaping().create();

    public FlattenMapJSONHandler() {
    }

    public List getEvents(HttpServletRequest request) throws Exception {
        BufferedReader reader = request.getReader();
        String charset = request.getCharacterEncoding();
        if (charset == null) {
            LOG.debug("Charset is null, default charset of UTF-8 will be used.");
            charset = "UTF-8";
        } else if (!charset.equalsIgnoreCase("utf-8")
                && !charset.equalsIgnoreCase("utf-16")
                && !charset.equalsIgnoreCase("utf-32")) {
            LOG.error("Unsupported character set in request {}. JSON handler supports UTF-8, UTF-16 and UTF-32 only.", charset);
            throw new UnsupportedCharsetException("JSON handler supports UTF-8, UTF-16 and UTF-32 only.");
        }
        Map eventMap;
        try {
            eventMap = gson.fromJson(reader, type);
            LOG.trace("Event: " + eventMap);
        } catch (JsonSyntaxException ex) {
            throw new HTTPBadRequestException("Request has invalid JSON Syntax.", ex);
        }

        if (eventMap == null || eventMap.isEmpty()) {
            LOG.warn("An Empty Document was received. Dropping the empty document");
            return Collections.emptyList();
        } else {
            Event event = EventBuilder.withBody(null);
            event.setHeaders(eventMap);

            return Collections.singletonList(event);
        }
    }

    public void configure(Context context) {
    }

}
