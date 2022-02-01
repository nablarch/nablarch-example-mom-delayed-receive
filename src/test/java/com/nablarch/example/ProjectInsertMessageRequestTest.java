package com.nablarch.example;

import nablarch.test.core.messaging.MessagingReceiveTestSupport;
import nablarch.test.junit5.extension.messaging.MessagingReceiveTest;
import org.junit.jupiter.api.Test;

@MessagingReceiveTest
public class ProjectInsertMessageRequestTest {

    MessagingReceiveTestSupport support;

    /**
     * 正常系のテスト。
     */
    @Test
    public void testNormalEnd() {
        support.execute(support.testName.getMethodName());
    }
}
