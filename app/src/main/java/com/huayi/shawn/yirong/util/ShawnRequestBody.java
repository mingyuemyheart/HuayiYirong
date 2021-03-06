package com.huayi.shawn.yirong.util;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * OkHttp3上传文件进度监听
 */
public class ShawnRequestBody extends RequestBody {

    public interface ProgressListener {
        void transferred(long size);
    }

    public static final int SEGMENT_SIZE = 2*1024; // okio.Segment.SIZE

    protected File file;
    protected ProgressListener rogressListener;
    protected String contentType;

    public ShawnRequestBody(File file, String contentType, ProgressListener rogressListener) {
        this.file = file;
        this.contentType = contentType;
        this.rogressListener = rogressListener;
    }

    protected ShawnRequestBody() {}

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();
                if (rogressListener != null) {
                    rogressListener.transferred(total);
                }
            }

        } finally {
            Util.closeQuietly(source);
        }
    }

}

