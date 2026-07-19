package com.justwen.androidnga.base.network.retrofit.converter;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import gov.anzong.androidnga.base.logger.Logger;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Justwen on 2017/10/10.
 */

public class JsonStringConvertFactory extends Converter.Factory {

    public static JsonStringConvertFactory create() {
        return new JsonStringConvertFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == String.class) {
            return JsonStringConverter.sInstance;
        }
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    private static class JsonStringConverter implements Converter<ResponseBody, String> {

        private static final JsonStringConverter sInstance = new JsonStringConverter();

        @Override
        public String convert(ResponseBody responseBody) {
            try (InputStream is = responseBody.byteStream()) {
                String body = IOUtils.toString(is, "GBK");
                Logger.d(body);
                return body;
            } catch (IOException e) {
                return "";
            }
        }
    }
}
