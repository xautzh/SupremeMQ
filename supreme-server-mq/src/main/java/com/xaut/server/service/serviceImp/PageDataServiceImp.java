package com.xaut.server.service.serviceImp;

import com.xaut.server.manager.SupremeMQServerManager;
import com.xaut.server.queue.SupremeMQMessageContainer;
import com.xaut.server.response.DataResponse;
import com.xaut.server.service.PageDataService;
import com.xaut.server.single.SupremeMQServerManagerInitSingle;
import com.xaut.server.vo.QueueVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PageDataServiceImp implements PageDataService {
    SupremeMQServerManagerInitSingle supremeMQServerManagerInitSingle;
    SupremeMQServerManager supremeMQServerManager;

    @Override
    public DataResponse dataResponse() {
        DataResponse dataResponse = new DataResponse();
        supremeMQServerManagerInitSingle = SupremeMQServerManagerInitSingle.getSupremeMQServerManagerInitSingle();
        supremeMQServerManager = supremeMQServerManagerInitSingle.getSupremeMQServerManager();
        ConcurrentHashMap<String, SupremeMQMessageContainer> messageMap = supremeMQServerManager.getSupremeMQMessageManager().getMessageContainerMap();
        Set<String> messageSet = messageMap.keySet();
        Iterator iterator = messageSet.iterator();
        List<QueueVo> queueVoList = new ArrayList<>();
        while (iterator.hasNext()) {
            QueueVo queueVo = new QueueVo();
            String queueName = (String) iterator.next();
            queueVo.setQueueName(queueName);
            //总共生产了多少条消息
            queueVo.setProviderMessageNumber(messageMap.get(queueName).getMessageQueueSize());
            queueVoList.add(queueVo);
        }
        dataResponse.setQueueResult(queueVoList);
        return dataResponse;
    }
}
