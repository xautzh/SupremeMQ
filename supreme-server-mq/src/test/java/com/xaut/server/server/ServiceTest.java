package com.xaut.server.server;

import com.xaut.server.manager.SupremeMQServerManager;
import com.xaut.server.queue.SupremeMQMessageContainer;
import com.xaut.server.response.DataResponse;
import com.xaut.server.service.PageDataService;
import com.xaut.server.service.serviceImp.PageDataServiceImp;
import com.xaut.server.single.SupremeMQServerManagerInitSingle;
import com.xaut.server.vo.QueueVo;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceTest {

    SupremeMQServerManagerInitSingle supremeMQServerManagerInitSingle;
    SupremeMQServerManager supremeMQServerManager;
    @Test
    public void dataResponse() {
        DataResponse dataResponse = new DataResponse();
        supremeMQServerManagerInitSingle = SupremeMQServerManagerInitSingle.getSupremeMQServerManagerInitSingle();
        supremeMQServerManager = supremeMQServerManagerInitSingle.getSupremeMQServerManager();
        ConcurrentHashMap<String, SupremeMQMessageContainer> messageMap = supremeMQServerManager.getSupremeMQMessageManager().getMessageContainerMap();
        Set<String> messageSet = messageMap.keySet();
        Iterator iterator = messageSet.iterator();
        while (iterator.hasNext()) {
            QueueVo queueVo = new QueueVo();
            String queueName = (String) iterator.next();
            queueVo.setQueueName(queueName);
            queueVo.setProviderMessageNumber(messageMap.get(queueName).getMessageQueueSize());
            dataResponse.getQueueResult().add(queueVo);
        }
        for (QueueVo q:dataResponse.getQueueResult()){
            System.out.println(q);
        }
    }

}
