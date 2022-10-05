package com.netty.study.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author Steven
 * @date 2022年10月06日 2:22
 */
public abstract class SslContextTool {

    protected SslContext sslContext() throws CertificateException, SSLException {
        boolean SSL = System.getProperty("ssl") != null;
        // Configure SSL.
        final SslContext sslContext;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslContext = null;
        }
        return sslContext;
    }
}
